package pcl.opensecurity.common.tileentity;

import com.mojang.authlib.GameProfile;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.interfaces.IOwner;

import java.util.*;

public class TileEntitySecurityTerminal extends TileEntityOSBase implements IProtection, IOwner {
    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
        allowedUsers.add(this.ownerUUID);
    }

    public UUID getOwner() {
        return this.ownerUUID;
    }
    private UUID ownerUUID;
    private ArrayList<UUID> allowedUsers = new ArrayList<>();
    private String password = "";
    public Block block;
    private Boolean enabled = false;
    private boolean enableParticles = false;
    public int rangeMod = 1;

    public TileEntitySecurityTerminal(){
        super("os_securityterminal");
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32000).create();
    }

    @Override
    public void validate(){
        super.validate();
        Protection.addArea(getWorld(), getArea(), getPos());
    }

    @Override
    public void invalidate() {
        Protection.removeArea(getWorld(), getPos());
        super.invalidate();
    }

    @Override
    public boolean isProtected(Entity entityIn, Protection.UserAction action){
        if(!enabled)
            return false;

        if(!action.equals(Protection.UserAction.explode) && isUserAllowedToBypass(entityIn.getUniqueID()))
            return false;

        if(!usePower())
            return false;

        if(entityIn != null && entityIn instanceof EntityPlayer)
            ((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this block is protected"), false);

        return true;
    }

    private AxisAlignedBB getArea(){
        int rangeMod = this.rangeMod * 8;
        return new AxisAlignedBB(getPos().add(-rangeMod, -rangeMod, -rangeMod), getPos().add(rangeMod, rangeMod, rangeMod).add(1, 1, 1));
    }

    public boolean isUserAllowedToBypass(UUID uuid) {
        return allowedUsers.contains(uuid);
    }

    @Callback(doc = "function():boolean; Returns the status of the block", direct = true)
    public Object[] isEnabled(Context context, Arguments args) {
        return new Object[] { isEnabled() };
    }
    
    private boolean isUUID(String input){
        return input.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"); // not sure if this could be passed to the UUID class... input.matches("[0-9a-f]{32}");
    }

    @Callback(doc = "function(String:Password, String:Username):boolean; Adds the Minecraft User as an allowed user.", direct = true)
    public Object[] addUser(Context context, Arguments args) {
        if (args.checkString(0).equals(getPass())) {
            if (isUUID(args.checkString(1))) {
                allowedUsers.add(UUID.fromString(args.checkString(1)));
            } else {
                GameProfile gameprofile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(args.checkString(1));

                if (gameprofile == null)
                {
                    return new Object[] { true, "Failed to get UUID from username" };
                } else {
                    allowedUsers.add(gameprofile.getId());
                }
            }
            return new Object[] { true, "User added" };
        } else {
            return new Object[] { false, "Password was incorrect" };
        }
    }

    @Callback(doc = "function(String:Username):boolean; Removes the Minecraft User as an allowed user.", direct = true)
    public Object[] delUser(Context context, Arguments args) {
        if (args.checkString(0).equals(getPass())) {
            if (isUUID(args.checkString(1))) {
                allowedUsers.remove(UUID.fromString(args.checkString(1)));
            } else {
                GameProfile gameprofile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(args.checkString(1));

                if (gameprofile == null)
                {
                    return new Object[] { true, "Failed to get UUID from username" };
                } else {
                    allowedUsers.remove(gameprofile.getId());
                }
            }
            return new Object[] { true, "User removed" };
        } else {
            return new Object[] { false, "Password was incorrect" };
        }
    }

    @Callback(doc = "function(String:password):boolean; Sets the block password, required to enable/disable and other actions", direct = true)
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

    @Callback(doc = "function():boolean; Switches particles to show the corners of the protected area", direct = true)
    public Object[] toggleParticle(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enableParticles = !enableParticles;
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
            markDirty();
            return new Object[] { enableParticles };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback(doc = "function(Int:range):boolean; Sets the range of the protction area 8*range max 4 min 1, increasing range increases energy cost.", direct = true)
    public Object[] setRange(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            if (args.checkInteger(1) >= 1 && args.checkInteger(1) <= 4) {
                if(rangeMod != args.checkInteger(1)) {
                    rangeMod = args.checkInteger(1);
                    Protection.updateArea(getWorld(), getPos(), getArea());
                    world.markBlockRangeForRenderUpdate(pos, pos);
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
                    markDirty();
                }
                return new Object[] { true };
            }
            return new Object[] { false, "Range out of bounds 1-4" };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback(doc = "function(String:password):boolean; Enables the block, requires the correct password", direct = true)
    public Object[] enable(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enabled = true;
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
            markDirty();
            return new Object[] { true };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback(doc = "function(String:password):boolean; Disables the block, requires the correct password", direct = true)
    public Object[] disable(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            enabled = false;
            return new Object[] { true };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    @Callback(doc = "function(String:password):boolean; returns a comma delimited string of current allowed users.", direct = true)
    public Object[] getAllowedUsers(Context context, Arguments args) {
        if (args.optString(0, "").equals(getPass())) {
            HashMap<UUID, String> users = new HashMap<>();
            for (UUID uuid : allowedUsers) {
                GameProfile gameProfile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(uuid);
                if(gameProfile != null)
                    users.put(uuid, gameProfile.getName());
            }
            return new Object[] { users.values().toArray() };
        } else {
            return new Object[] { false, "Password incorrect" };
        }
    }

    /* only for debug *//*
    @Callback(doc = "function():boolean; removes all terminals from cache", direct = true)
    public Object[] removeAllTerminals(Context context, Arguments args) {
        Protection.get(getWorld()).clear();
        return new Object[] { true };
    }
    */

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
                    EnumParticleTypes.BARRIER,
                    pos.getX() + 8 * rangeMod +0.5f,
                    pos.getY() + 8 * rangeMod +0.5f,
                    pos.getZ() + 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //2
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() + 8 * rangeMod +0.5f,
                    pos.getY() + 8 * rangeMod +0.5f,
                    pos.getZ() - 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //3
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() - 8 * rangeMod +0.5f,
                    pos.getY() - 8 * rangeMod +0.5f,
                    pos.getZ() - 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //4
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() - 8 * rangeMod +0.5f,
                    pos.getY() + 8 * rangeMod +0.5f,
                    pos.getZ() + 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //5
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() - 8 * rangeMod +0.5f,
                    pos.getY() + 8 * rangeMod +0.5f,
                    pos.getZ() - 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //6
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() - 8 * rangeMod +0.5f,
                    pos.getY() - 8 * rangeMod +0.5f,
                    pos.getZ() + 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //7
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() + 8 * rangeMod +0.5f,
                    pos.getY() - 8 * rangeMod +0.5f,
                    pos.getZ() - 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            //8
            wServer.spawnParticle(
                    EnumParticleTypes.BARRIER,
                    pos.getX() + 8 * rangeMod +0.5f,
                    pos.getY() - 8 * rangeMod +0.5f,
                    pos.getZ() + 8 * rangeMod +0.5f,
                    25,
                    motionX,
                    motionY,
                    motionZ,
                    0.5);
            ticksExisted = 0;
        }
        ticksExisted++;
    }

    public boolean isParticleEnabled() {
        return enableParticles;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasUniqueId("owner"))
            this.ownerUUID = nbt.getUniqueId("owner");
        else if(nbt.hasKey("owner")) //keep this for compat with old nbt tags in world (after first worldsave they are "fixed"
            this.ownerUUID = UUID.fromString(nbt.getString("owner"));
        else
            this.ownerUUID = null;

        this.password= nbt.getString("password");
        this.enabled=nbt.getBoolean("enabled");
        this.rangeMod=nbt.getInteger("rangeMod");
        this.enableParticles=nbt.getBoolean("particles");

        allowedUsers.clear();
        for(int i=0; nbt.hasUniqueId("allowedUser"+i); i++)
            allowedUsers.add(nbt.getUniqueId("allowedUser"+i));

        if(nbt.hasKey("allowedUsers")) { //keep this for compat with old nbt tags in world (after first worldsave they are "fixed"
            for(String user : Arrays.asList(nbt.getString("allowedUsers").replaceAll(", $", "").split(", ")))
                this.allowedUsers.add(UUID.fromString(user));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if(ownerUUID != null)
            nbt.setUniqueId("owner", this.ownerUUID);

        nbt.setString("password", this.password);
        nbt.setBoolean("enabled", this.isEnabled());
        nbt.setInteger("rangeMod", this.rangeMod);
        nbt.setBoolean("particles", this.isParticleEnabled());

        for(int i=0; i < this.allowedUsers.size(); i++)
            nbt.setUniqueId("allowedUser"+i, this.allowedUsers.get(i));

        return nbt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Boolean usePower() {
        return node.tryChangeBuffer(-10 * rangeMod);
    }
}
