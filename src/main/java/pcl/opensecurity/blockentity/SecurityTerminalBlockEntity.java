package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public final class SecurityTerminalBlockEntity extends SecurityBlockEntity {
    private static final Map<Level, Set<BlockPos>> LOADED = new WeakHashMap<>();
    private UUID owner;
    private final Set<UUID> allowedUsers = new HashSet<>();
    private String password = "";
    private boolean enabled;
    private boolean particles;
    private int range = 1;
    private int ticks;

    public SecurityTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.SECURITY_TERMINAL_BE.get(), pos, state, "os_securityterminal", 32000);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        allowedUsers.add(owner);
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) LOADED.computeIfAbsent(level, ignored -> new HashSet<>()).add(worldPosition.immutable());
    }

    @Override
    public void setRemoved() {
        if (level != null) {
            Set<BlockPos> positions = LOADED.get(level);
            if (positions != null) positions.remove(worldPosition);
        }
        super.setRemoved();
    }

    public static boolean isProtected(Level level, BlockPos target, UUID actor) {
        if (level == null || level.isClientSide) return false;
        Set<BlockPos> positions = LOADED.get(level);
        if (positions == null) return false;
        Iterator<BlockPos> iterator = positions.iterator();
        while (iterator.hasNext()) {
            BlockPos position = iterator.next();
            if (!(level.getBlockEntity(position) instanceof SecurityTerminalBlockEntity terminal)) {
                iterator.remove();
                continue;
            }
            if (terminal.protects(target, actor)) return true;
        }
        return false;
    }

    private boolean protects(BlockPos target, UUID actor) {
        if (!enabled || actor != null && allowedUsers.contains(actor)) return false;
        int radius = range * 8;
        if (Math.abs(target.getX() - worldPosition.getX()) > radius
                || Math.abs(target.getY() - worldPosition.getY()) > radius
                || Math.abs(target.getZ() - worldPosition.getZ()) > radius) return false;
        return consumeEnergy(10.0 * range);
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel server) || !particles || ++ticks < 40) return;
        ticks = 0;
        int radius = range * 8;
        for (int x : new int[]{-radius, radius}) for (int y : new int[]{-radius, radius}) for (int z : new int[]{-radius, radius}) {
            server.sendParticles(ParticleTypes.END_ROD, worldPosition.getX() + x + 0.5,
                    worldPosition.getY() + y + 0.5, worldPosition.getZ() + z + 0.5,
                    4, 0.15, 0.15, 0.15, 0.0);
        }
    }

    @Callback(direct = true, doc = "function():boolean -- Returns whether protection is enabled.")
    public Object[] isEnabled(Context context, Arguments args) {
        return new Object[]{enabled};
    }

    @Callback(direct = true, doc = "function(password:string,user:string):boolean,string -- Allows a user.")
    public Object[] addUser(Context context, Arguments args) {
        if (!password.equals(args.checkString(0))) return new Object[]{false, "password was incorrect"};
        UUID user = resolveUser(args.checkString(1));
        if (user == null) return new Object[]{false, "failed to get UUID from username"};
        allowedUsers.add(user);
        setChanged();
        return new Object[]{true, "user added"};
    }

    @Callback(direct = true, doc = "function(password:string,user:string):boolean,string -- Removes an allowed user.")
    public Object[] delUser(Context context, Arguments args) {
        if (!password.equals(args.checkString(0))) return new Object[]{false, "password was incorrect"};
        UUID user = resolveUser(args.checkString(1));
        if (user == null) return new Object[]{false, "failed to get UUID from username"};
        if (owner != null && owner.equals(user)) return new Object[]{false, "owner cannot be removed"};
        allowedUsers.remove(user);
        setChanged();
        return new Object[]{true, "user removed"};
    }

    @Callback(direct = true, doc = "function(password:string[,newPassword:string]):boolean,string -- Sets or changes the password.")
    public Object[] setPassword(Context context, Arguments args) {
        if (password.isEmpty()) {
            password = args.checkString(0);
            setChanged();
            return new Object[]{true, "password set"};
        }
        if (!password.equals(args.checkString(0))) return new Object[]{false, "password was not changed"};
        password = args.checkString(1);
        setChanged();
        return new Object[]{true, "password changed"};
    }

    @Callback(direct = true, doc = "function([password:string]):boolean -- Toggles boundary particles.")
    public Object[] toggleParticle(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "password incorrect"};
        particles = !particles;
        setChanged();
        return new Object[]{particles};
    }

    @Callback(direct = true, doc = "function(password:string,range:number):boolean -- Sets range multiplier 1 through 4.")
    public Object[] setRange(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "password incorrect"};
        int requested = args.checkInteger(1);
        if (requested < 1 || requested > 4) return new Object[]{false, "range out of bounds 1-4"};
        range = requested;
        setChanged();
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function([password:string]):boolean -- Enables protection.")
    public Object[] enable(Context context, Arguments args) {
        return setEnabled(args.optString(0, ""), true);
    }

    @Callback(direct = true, doc = "function([password:string]):boolean -- Disables protection.")
    public Object[] disable(Context context, Arguments args) {
        return setEnabled(args.optString(0, ""), false);
    }

    @Callback(direct = true, doc = "function([password:string]):table -- Returns allowed users.")
    public Object[] getAllowedUsers(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "password incorrect"};
        ArrayList<String> users = new ArrayList<>();
        for (UUID uuid : allowedUsers) {
            Player player = level == null ? null : level.getPlayerByUUID(uuid);
            users.add(player == null ? uuid.toString() : player.getName().getString());
        }
        return new Object[]{users.toArray()};
    }

    private Object[] setEnabled(String supplied, boolean value) {
        if (!password.equals(supplied)) return new Object[]{false, "password incorrect"};
        enabled = value;
        setChanged();
        return new Object[]{true};
    }

    private UUID resolveUser(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            if (level == null || level.getServer() == null) return null;
            ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(value);
            return player == null ? null : player.getUUID();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null) tag.putUUID("owner", owner);
        tag.putString("password", password);
        tag.putBoolean("enabled", enabled);
        tag.putBoolean("particles", particles);
        tag.putInt("rangeMod", range);
        tag.putInt("allowedCount", allowedUsers.size());
        int index = 0;
        for (UUID user : allowedUsers) tag.putUUID("allowedUser" + index++, user);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        password = tag.getString("password");
        enabled = tag.getBoolean("enabled");
        particles = tag.getBoolean("particles");
        range = Math.max(1, Math.min(4, tag.contains("rangeMod") ? tag.getInt("rangeMod") : 1));
        allowedUsers.clear();
        for (int index = 0; index < tag.getInt("allowedCount"); index++) {
            if (tag.hasUUID("allowedUser" + index)) allowedUsers.add(tag.getUUID("allowedUser" + index));
        }
        if (owner != null) allowedUsers.add(owner);
    }
}
