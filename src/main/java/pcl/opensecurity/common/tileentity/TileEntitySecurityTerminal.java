package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class TileEntitySecurityTerminal extends TileEntityOSBase {
    public void setOwner(String UUID) {
        this.ownerUUID = UUID;
    }

    public String getOwner() {
        return this.ownerUUID;
    }
    String ownerUUID = "";
    private String password = "";
    public Block block;
    private Boolean enabled = false;
    boolean enableParticles = false;

    public TileEntitySecurityTerminal(){
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
    }

    private static String getComponentName() {
        return "os_securityterminal";
    }

    @Callback
    public Object[] isEnabled(Context context, Arguments args) {
        return new Object[] { isEnabled() };
    }

    @Callback
    public Object[] setPassword(Context context, Arguments args) {
            if (getPass().isEmpty()) {
                setPass(args.checkString(0));
                return new Object[] { true, "Password set" };
            } else {
                if (args.checkString(0).equals(getPass())) {
                    setPass(args.checkString(1));
                    return new Object[] { true, "Password Changed" };
                } else {
                    return new Object[] { false, "Password was not changed" };
                }
            }
    }

    @Callback
    public Object[] toggleParticle(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enableParticles = !enableParticles;
            return new Object[] { enableParticles };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback
    public Object[] enable(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enabled = true;
            return new Object[] { true };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback
    public Object[] disable(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enabled = false;
            return new Object[] { true };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    public void setPass(String pass) {
        this.password = pass;
    }

    public String getPass() {
        return this.password;
    }
    int ticksExisted = 0;
    @Override
    public void update() {
        super.update();
        if (node != null && node.network() == null) {
            Network.joinOrCreateNetwork(this);
        }
        if (!world.isRemote && ticksExisted%40==0 && isParticleEnabled()) {
            double motionX = world.rand.nextGaussian() * 0.02D;
            double motionY = world.rand.nextGaussian() * 0.02D;
            double motionZ = world.rand.nextGaussian() * 0.02D;
            WorldServer wServer = (WorldServer) world;
            //1
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 20,
                    pos.getY() + 20,
                    pos.getZ() + 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //2
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 20,
                    pos.getY() + 20,
                    pos.getZ() - 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //3
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() - 20,
                    pos.getY() - 20,
                    pos.getZ() - 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //4
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() - 20,
                    pos.getY() + 20,
                    pos.getZ() + 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //5
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() - 20,
                    pos.getY() + 20,
                    pos.getZ() - 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //6
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() - 20,
                    pos.getY() - 20,
                    pos.getZ() + 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //7
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 20,
                    pos.getY() - 20,
                    pos.getZ() - 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //8
            wServer.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 20,
                    pos.getY() - 20,
                    pos.getZ() + 20,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);

        }
        ticksExisted++;
    }

    private boolean isParticleEnabled() {
        return enableParticles;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (node != null && node.host() == this) {
            node.load(nbt.getCompoundTag("oc:node"));
        }
        this.ownerUUID = nbt.getString("owner");
        this.password= nbt.getString("password");
        this.enabled=nbt.getBoolean("enabled");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
        nbt.setString("owner", this.ownerUUID);
        nbt.setString("password", this.password);
        nbt.setBoolean("enabled", this.isEnabled());
        return nbt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void usePower() {
        node.changeBuffer(30);
    }
}
