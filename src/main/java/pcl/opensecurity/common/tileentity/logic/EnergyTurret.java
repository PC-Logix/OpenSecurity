package pcl.opensecurity.common.tileentity.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.Config;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.common.blocks.BlockEnergyTurret;
import pcl.opensecurity.common.entity.EntityEnergyBolt;

public class EnergyTurret {
    private static final float maxShaftLengthForOneBlock = 0.5f;

    private ItemStackHandler inventory;

    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private float setpointYaw = 0.0F;
    private float setpointPitch = 0.0F;
    private float shaft = 1.0F;
    private float setShaft = 1.0F;
    private float barrel = 1.0F;
    private int tickCool = 0;
    private int soundTicks = 0;
    private boolean power = false;
    private boolean armed = false;

    private EnumFacing mountedDirection = EnumFacing.DOWN;

    private EnergyTurretStats energyTurretStats = new EnergyTurretStats();

    private EnergyTurretHost tile;

    public EnergyTurret(EnergyTurretHost energyTurretHost){
        if(energyTurretHost == null)
            return;

        tile = energyTurretHost;

        inventory = new ItemStackHandler(8) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public void onContentsChanged(int slot){
                stats().loadFromInventory(inventory);
            }
        };
    }

    public interface EnergyTurretHost{
        boolean consumeEnergy(double amount);
        void markDirtyClient();
        World getWorld();
        BlockPos getPos();
    }


    public void onLoad(){
        stats().loadFromInventory(inventory);
        mountedDirection = BlockEnergyTurret.getMount(getWorld().getBlockState(getPos()));
    }

    public void update(){
        boolean shaftLengthValid = true;
        boolean moveSound = false;

        float maxShaft = getMaxAvailableShaftLength(shaft);
        if (shaft > maxShaft){
            shaftLengthValid = false;

            if (setShaft > maxShaft)
                setShaft(maxShaft);
        }

        if (power && !getWorld().isRemote && !tile.consumeEnergy(10)) {
            doPowerOff();
        }

        tickCool-= energyTurretStats.getCooldown();

        float tmpSetPitch=setpointPitch;
        float ms=0F, my=0F, mp=0F;

        float movePerTick = energyTurretStats.getMoveSpeed();

        if (power || !shaftLengthValid) {
            if(Float.isNaN(shaft) || Float.isInfinite(shaft)) shaft = 0;
            float ds = setShaft - shaft;
            ms = Math.min(0.05F, Math.abs(ds));
            shaft += ms * Math.signum(ds);
            if(ms>0F) moveSound = true;
        }
        if (power) {
            if(Float.isNaN(yaw) || Float.isInfinite(yaw)) yaw = 0;

            float dy = (setpointYaw - yaw)%360;
            if(dy>180) dy = dy - 360;
            else if(dy<-180) dy = 360 + dy;
            my = Math.min(movePerTick, Math.abs(dy));
            yaw += my * Math.signum(dy);
            yaw = yaw%360;
            if(yaw<0F) yaw+=360F;
            if(my>0F) moveSound = true;
        } else {
            tmpSetPitch = -90F;
            movePerTick = 6;
        }

        if (isUpright()){
            tmpSetPitch = Math.min(tmpSetPitch, (float)(Math.atan(shaft)*360/Math.PI));
            tmpSetPitch = Math.max(tmpSetPitch, (float)(-Math.atan(shaft)*180/Math.PI));
        } else {
            tmpSetPitch = Math.min(tmpSetPitch, (float)(Math.atan(shaft)*180/Math.PI));
            tmpSetPitch = Math.max(tmpSetPitch, (float)(-Math.atan(shaft)*360/Math.PI));
        }

        if(Float.isNaN(pitch) || Float.isInfinite(pitch)) pitch = 0;
        float dp = tmpSetPitch - pitch;
        mp = Math.min(movePerTick, Math.abs(dp));
        if(power && mp>0F) moveSound = true;

        pitch += mp * Math.signum(dp);
        pitch = Math.min(90, Math.max(-90, pitch));

        if (power) {
            if (armed) {
                if(barrel<1F) {
                    barrel=Math.min(1F, barrel+0.1F);
                    moveSound = true;
                }
            }
            else {
                if (barrel>0F) {
                    barrel=Math.max(0F, barrel-0.1F);
                    moveSound = true;
                }
            }
        }

        if (moveSound) {
            if(soundTicks == 0) {
                getWorld().playSound(null, getPos(), SoundHandler.turretMove, SoundCategory.BLOCKS, 15.5F, 1.0F);
            }
            soundTicks++;
            if (soundTicks > 5) {
                soundTicks = 0;
            }
        } else {
            soundTicks = 0;
        }
    }

    private EnergyTurretStats stats(){
        return energyTurretStats;
    }

    public boolean isUpright(){
        return mountedDirection.equals(EnumFacing.DOWN);
    }

    public float getRealYaw() {
        //return ((float)Math.PI) * yaw / 180;
        if (Config.getConfig().getCategory("general").get("turretReverseRotation").getBoolean())
            return ((float)Math.PI) * (0 - yaw) / 180; // TODO: set legacy compatible offset (90? -90?)
        else
            return ((float)Math.PI) * yaw / 180;
    }

    public float getRealPitch() {
        return ((float)Math.PI) * pitch / 180;
    }


    private float getMaxAvailableShaftLength(float newExt)
    {
        if (newExt<0F) {
            newExt = 0F;
        }
        if (newExt>2F) {
            newExt = 2F;
        }
        int otherY = getPos().getY()+(isUpright()?1:-1);
        if(newExt>maxShaftLengthForOneBlock && (otherY<0 || otherY>255 /*|| world.isAirBlock(getPos().getX(), otherY, getPos().getZ())*/))
        {
            return maxShaftLengthForOneBlock;
        }
        return newExt;
    }

    public float setShaft(float newlen)
    {
        if (newlen<0F) {
            newlen = 0F;
        }
        float most = getMaxAvailableShaftLength(newlen);
        if (newlen>most) {
            newlen = most;
        }
        if(setShaft != newlen)
        {
            setShaft = newlen;
            tile.markDirtyClient();
        }

        return newlen;
    }

    public float getShaftLength(){
        return shaft;
    }

    public float getBarrel(){
        return barrel;
    }

    public void setYawPitch(float newYaw, float newPitch) {
        while(newYaw < 0)
            newYaw += 360;

        this.setpointYaw = newYaw % 360;

        this.setpointPitch = Math.min(90, Math.max(-90, newPitch));

        tile.markDirtyClient();
    }

    public boolean isArmed(){
        return armed;
    }

    public void setArmed(boolean val){
        if(armed == val)
            return;

        armed = val;
        tile.markDirtyClient();
    }


    public void doPowerOn() {
        if(power)
            return;

        power = true;
        tile.markDirtyClient();
    }

    public void doPowerOff() {
        if(!power)
            return;

        power = false;

        setShaft(shaft);
        setYawPitch(0, 0);
    }






    public ItemStackHandler getInventory(){
        return inventory;
    }

    public float yaw(){
        return yaw;
    }

    public float pitch(){
        return pitch;
    }

    public boolean isReady(){
        return !(tickCool > 0) && armed && barrel==1F;
    }

    public boolean isPowered(){
        return power;
    }

    public Object[] isOnTarget(){
        double dPitch = Math.abs(pitch-setpointPitch);
        double dYaw = Math.abs(yaw-setpointYaw);
        double delta = dPitch + dYaw;
        boolean onPoint = delta < 0.5F;
        return new Object[] { onPoint, delta };
    }

    public Object[] fire(){
        if (!isPowered())
            return new Object[] { false, "powered off" };

        if (!isArmed() || barrel < 1F)
            return new Object[] { false, "not armed" };

        if (this.tickCool > 0)
            return new Object[] { false, "gun hasn't cooled" };

        if (!tile.consumeEnergy(energyTurretStats.getEnergyUsage()))
            return new Object[] { false, "not enough energy" };

        this.tickCool = 100;
        float p = getRealPitch();
        float a = getRealYaw() + (float)Math.PI;
        EntityEnergyBolt bolt = new EntityEnergyBolt(getWorld());
        float dY = 0.5F + (isUpright() ? 1F : -1F) * (0.125F + shaft*0.375F);
        bolt.setHeading(a, p);
        bolt.setDamage(energyTurretStats.getDamage());
        bolt.setPositionAndUpdate(getPos().getX() + 0.5F, getPos().getY() + dY, getPos().getZ() + 0.5F);

        getWorld().playSound(null, getPos().add(0.5, 0.5, 0.5), SoundHandler.turretFire, SoundCategory.BLOCKS, 15.5F, 1.0F);

        tile.markDirtyClient();

        getWorld().spawnEntity(bolt);
        return new Object[] { true };
    }

    private World getWorld(){
       return tile.getWorld();
    }

    private BlockPos getPos(){
        return tile.getPos();
    }


    public void readFromNBT(NBTTagCompound tag) {
        this.power = tag.getBoolean("powered");
        this.armed = tag.getBoolean("armed");
        this.yaw = tag.getFloat("yaw");
        this.setpointYaw = tag.getFloat("syaw");
        this.pitch = tag.getFloat("pitch");
        this.setpointPitch = tag.getFloat("spitch");
        this.shaft = tag.getFloat("shaft");
        this.setShaft = tag.getFloat("sshaft");
        this.barrel = tag.getFloat("barrel");
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("powered", this.power);
        tag.setBoolean("armed", this.armed);
        tag.setFloat("yaw", this.yaw);
        tag.setFloat("syaw", this.setpointYaw);
        tag.setFloat("pitch", this.pitch);
        tag.setFloat("spitch", this.setpointPitch);
        tag.setFloat("shaft", this.shaft);
        tag.setFloat("sshaft", this.setShaft);
        tag.setFloat("barrel", this.barrel);
        tag.setTag("inventory", inventory.serializeNBT());

        return tag;
    }

}
