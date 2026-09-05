package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.Config;
import pcl.opensecurity.block.EnergyTurretBlock;
import pcl.opensecurity.entity.EnergyBoltEntity;
import pcl.opensecurity.item.TurretUpgradeItem;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.UUID;

public final class EnergyTurretBlockEntity extends SecurityBlockEntity {
    private static final float MAX_SHAFT = 0.5F;

    private final ItemStackHandler inventory = new ItemStackHandler(8) {
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return stack.getItem() instanceof TurretUpgradeItem; }
        @Override protected void onContentsChanged(int slot) { sync(); }
    };

    private UUID owner;

    private float yaw;
    private float pitch;
    private float targetYaw;
    private float targetPitch;
    private float shaft = MAX_SHAFT;
    private float targetShaft = MAX_SHAFT;
    private float barrel = 1.0F;

    // Previous tick values are client/rendering state only; they are not persisted.
    private float previousYaw;
    private float previousPitch;
    private float previousShaft = MAX_SHAFT;
    private float previousBarrel = 1.0F;

    private boolean powered;
    private boolean armed;
    private int cooldown;

    public EnergyTurretBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.ENERGY_TURRET_BE.get(), pos, state, "os_energyturret", 32);
    }

    public ItemStackHandler inventory() { return inventory; }

    public void setOwner(UUID owner) {
        this.owner = owner;
        sync();
    }

    public boolean insert(ItemStack held) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            if (inventory.getStackInSlot(slot).isEmpty()) {
                inventory.setStackInSlot(slot, held.split(1));
                return true;
            }
        }
        return false;
    }

    public boolean extract(Player player) {
        for (int slot = inventory.getSlots() - 1; slot >= 0; slot--) {
            ItemStack stack = inventory.extractItem(slot, 1, false);
            if (!stack.isEmpty()) {
                if (!player.addItem(stack)) player.drop(stack, false);
                return true;
            }
        }
        return false;
    }

    public void dropContents() {
        if (level == null) return;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, 1, false);
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        stack));
            }
        }
    }

    /**
     * The original 1.12 turret simulated its mechanical movement on both sides.
     * The server remains authoritative for OC callbacks, energy use and firing,
     * while the client mirrors the same movement locally for smooth animation.
     */
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (cooldown > 0) cooldown = Math.max(0, cooldown - cooldownRate());
            if (powered && !consumeEnergy(10)) {
                powered = false;
                // 1.12 froze the shaft at its current extension when power was lost.
                targetShaft = shaft;
                targetYaw = 0.0F;
                targetPitch = 0.0F;
                sync();
            }
        }

        animateMechanicalState();
    }

    private void animateMechanicalState() {
        previousYaw = yaw;
        previousPitch = pitch;
        previousShaft = shaft;
        previousBarrel = barrel;

        float movePerTick = 4.0F + movementBonus();

        // The old implementation only drove the shaft while powered (aside from
        // collision-forced retraction, which is not currently implemented here).
        if (powered) {
            shaft = approach(shaft, targetShaft, 0.05F);
            yaw = approachAngle(yaw, targetYaw, movePerTick);
        }

        float desiredPitch = powered ? targetPitch : -90.0F;
        if (!powered) movePerTick = 6.0F;

        // Preserve the old mechanical pitch limits so the barrel does not rotate
        // through the base while the shaft is retracted.
        if (isUpright()) {
            desiredPitch = Math.min(desiredPitch, (float) (Math.atan(shaft) * 360.0 / Math.PI));
            desiredPitch = Math.max(desiredPitch, (float) (-Math.atan(shaft) * 180.0 / Math.PI));
        } else {
            desiredPitch = Math.min(desiredPitch, (float) (Math.atan(shaft) * 180.0 / Math.PI));
            desiredPitch = Math.max(desiredPitch, (float) (-Math.atan(shaft) * 360.0 / Math.PI));
        }

        pitch = approach(pitch, desiredPitch, movePerTick);
        pitch = Mth.clamp(pitch, -90.0F, 90.0F);

        // In 1.12 the gun itself telescoped when armed/disarmed. That state was
        // completely omitted from the first 1.21 port.
        if (powered) {
            barrel = approach(barrel, armed ? 1.0F : 0.0F, 0.1F);
        }
    }

    public boolean isUpright() {
        return getBlockState().getValue(EnergyTurretBlock.MOUNT) == Direction.DOWN;
    }

    public float getRenderYaw(float partialTick) {
        float renderedYaw = lerpDegrees(previousYaw, yaw, partialTick);
        return Config.turretReverseRotation() ? -renderedYaw : renderedYaw;
    }

    public float getRenderPitch(float partialTick) {
        return Mth.lerp(partialTick, previousPitch, pitch);
    }

    public float getRenderShaft(float partialTick) {
        return Mth.lerp(partialTick, previousShaft, shaft);
    }

    public float getRenderBarrel(float partialTick) {
        return Mth.lerp(partialTick, previousBarrel, barrel);
    }

    @Callback(direct = true, doc = "function():number -- Returns current yaw in degrees.")
    public Object[] getYaw(Context context, Arguments args) { return new Object[]{yaw}; }

    @Callback(direct = true, doc = "function():number -- Returns current pitch in degrees.")
    public Object[] getPitch(Context context, Arguments args) { return new Object[]{pitch}; }

    @Callback(direct = true, doc = "function():boolean,number -- Returns on-target state and error.")
    public Object[] isOnTarget(Context context, Arguments args) {
        double delta = Math.abs(angleDelta(yaw, targetYaw)) + Math.abs(pitch - targetPitch);
        return new Object[]{delta < 0.5, delta};
    }

    @Callback(direct = true, doc = "function():boolean -- Returns whether the turret can fire.")
    public Object[] isReady(Context context, Arguments args) {
        return new Object[]{powered && armed && barrel >= 1.0F && cooldown <= 0};
    }

    @Callback(direct = true, doc = "function():boolean -- Returns power state.")
    public Object[] isPowered(Context context, Arguments args) { return new Object[]{powered}; }

    @Callback(doc = "function(length:number):number -- Extends the shaft from 0 through 0.5 blocks.")
    public Object[] extendShaft(Context context, Arguments args) {
        targetShaft = Mth.clamp((float) args.checkDouble(0), 0.0F, MAX_SHAFT);
        sync();
        return new Object[]{targetShaft};
    }

    @Callback(direct = true, doc = "function():number -- Returns current shaft extension.")
    public Object[] getShaftLength(Context context, Arguments args) { return new Object[]{shaft}; }

    @Callback(doc = "function(yaw:number,pitch:number):boolean -- Moves to degrees.")
    public Object[] moveTo(Context context, Arguments args) {
        if (!powered) return new Object[]{false, "powered off"};
        setTarget((float) args.checkDouble(0), (float) args.checkDouble(1));
        return new Object[]{true};
    }

    @Callback(doc = "function(yaw:number,pitch:number):boolean -- Moves relative in degrees.")
    public Object[] moveBy(Context context, Arguments args) {
        if (!powered) return new Object[]{false, "powered off"};
        setTarget(targetYaw + (float) args.checkDouble(0), targetPitch + (float) args.checkDouble(1));
        return new Object[]{true};
    }

    @Callback(doc = "function(yaw:number,pitch:number):boolean -- Moves to radians.")
    public Object[] moveToRadians(Context context, Arguments args) {
        if (!powered) return new Object[]{false, "powered off"};
        setTarget((float) Math.toDegrees(args.checkDouble(0)), (float) Math.toDegrees(args.checkDouble(1)));
        return new Object[]{true};
    }

    @Callback
    public Object[] setArmed(Context context, Arguments args) {
        armed = args.checkBoolean(0);
        sync();
        return new Object[]{true};
    }

    @Callback
    public Object[] powerOn(Context context, Arguments args) {
        powered = true;
        sync();
        return new Object[]{true};
    }

    @Callback
    public Object[] powerOff(Context context, Arguments args) {
        powered = false;
        targetShaft = shaft;
        targetYaw = 0.0F;
        targetPitch = 0.0F;
        sync();
        return new Object[]{true};
    }

    @Callback(doc = "function():boolean,string -- Fires an energy bolt.")
    public Object[] fire(Context context, Arguments args) {
        if (!powered) return new Object[]{false, "powered off"};
        if (!armed || barrel < 1.0F) return new Object[]{false, "not armed"};
        if (cooldown > 0) return new Object[]{false, "gun hasn't cooled"};

        float damage = damage();
        if (!consumeEnergy(2.0 * damage * energyFactor())) return new Object[]{false, "not enough energy"};
        if (level == null) return new Object[]{false, "world is unavailable"};

        cooldown = 100;
        double yawRadians = Math.toRadians(yaw) + Math.PI;
        double pitchRadians = Math.toRadians(pitch);
        Vec3 direction = new Vec3(
                Math.sin(yawRadians) * Math.cos(pitchRadians),
                Math.sin(pitchRadians),
                Math.cos(yawRadians) * Math.cos(pitchRadians));

        EnergyBoltEntity bolt = new EnergyBoltEntity(OpenSecurity.ENERGY_BOLT.get(), level);
        double dY = 0.5 + (isUpright() ? 1.0 : -1.0) * (0.125 + shaft * 0.375);
        bolt.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + dY, worldPosition.getZ() + 0.5);
        bolt.setDamage(damage);
        bolt.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);
        level.addFreshEntity(bolt);
        level.playSound(null, worldPosition, OpenSecurity.TURRET_FIRE.get(), SoundSource.BLOCKS, 15.5F, 1.0F);
        sync();
        return new Object[]{true};
    }

    private void setTarget(float yaw, float pitch) {
        targetYaw = ((yaw % 360.0F) + 360.0F) % 360.0F;
        targetPitch = Mth.clamp(pitch, -90.0F, 90.0F);
        sync();
    }

    private float damage() { return 5.0F * (float) Math.pow(3.0, upgradeCount(TurretUpgradeItem.Kind.DAMAGE)); }
    private float energyFactor() { return (float) Math.pow(0.7, upgradeCount(TurretUpgradeItem.Kind.ENERGY)); }
    private int cooldownRate() { return 1 + 3 * upgradeCount(TurretUpgradeItem.Kind.COOLDOWN); }
    private float movementBonus() { return 2.5F * upgradeCount(TurretUpgradeItem.Kind.MOVEMENT); }

    private int upgradeCount(TurretUpgradeItem.Kind kind) {
        int count = 0;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            if (inventory.getStackInSlot(slot).getItem() instanceof TurretUpgradeItem upgrade && upgrade.kind() == kind) {
                count++;
            }
        }
        return count;
    }

    private static float approach(float value, float target, float step) {
        float delta = target - value;
        return Math.abs(delta) <= step ? target : value + Math.copySign(step, delta);
    }

    private static float angleDelta(float value, float target) {
        return ((target - value + 540.0F) % 360.0F) - 180.0F;
    }

    private static float approachAngle(float value, float target, float step) {
        float delta = angleDelta(value, target);
        float result = Math.abs(delta) <= step ? target : value + Math.copySign(step, delta);
        return ((result % 360.0F) + 360.0F) % 360.0F;
    }

    private static float lerpDegrees(float from, float to, float partialTick) {
        return from + angleDelta(from, to) * partialTick;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null) tag.putUUID("owner", owner);
        tag.putFloat("yaw", yaw);
        tag.putFloat("pitch", pitch);
        tag.putFloat("syaw", targetYaw);
        tag.putFloat("spitch", targetPitch);
        tag.putFloat("shaft", shaft);
        tag.putFloat("sshaft", targetShaft);
        tag.putFloat("barrel", barrel);
        tag.putBoolean("powered", powered);
        tag.putBoolean("armed", armed);
        tag.putInt("cooldown", cooldown);
        tag.put("inventory", inventory.serializeNBT(provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        yaw = tag.getFloat("yaw");
        pitch = tag.getFloat("pitch");
        targetYaw = tag.getFloat("syaw");
        targetPitch = tag.getFloat("spitch");
        shaft = tag.contains("shaft") ? tag.getFloat("shaft") : MAX_SHAFT;
        targetShaft = tag.contains("sshaft") ? tag.getFloat("sshaft") : shaft;
        barrel = tag.contains("barrel") ? tag.getFloat("barrel") : 1.0F;
        powered = tag.getBoolean("powered");
        armed = tag.getBoolean("armed");
        cooldown = Math.max(0, tag.getInt("cooldown"));
        if (tag.contains("inventory")) inventory.deserializeNBT(provider, tag.getCompound("inventory"));

        previousYaw = yaw;
        previousPitch = pitch;
        previousShaft = shaft;
        previousBarrel = barrel;
    }
}
