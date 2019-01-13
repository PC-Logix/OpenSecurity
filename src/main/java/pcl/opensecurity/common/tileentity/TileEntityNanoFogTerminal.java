package pcl.opensecurity.common.tileentity;
/**
 * @author ben_mkiv
 */
import com.mojang.authlib.GameProfile;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.Config;
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;
import pcl.opensecurity.util.BlockUtils;
import pcl.opensecurity.util.ClassHelper;
import pcl.opensecurity.util.ItemUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static pcl.opensecurity.common.ContentRegistry.nanoDNAItem;
import static pcl.opensecurity.common.ContentRegistry.nanoFog;

public class TileEntityNanoFogTerminal extends TileEntityOSBase implements ITickable {
    public static final int terminalRange = 32, FogBlockLimit = 256;
    public static final int FogBuildCost = 50, FogUpkeepCost = 1;
    public static final int maxKnockback = 2, maxDamage = 5;
    public static final int knockbackCost = 2, damageCost = 5;

    public static final int inventorySlots = 7;  // input/output slot + 5 upgrade slots (not used yet)
    
    private FakePlayer fakePlayer;
    private ItemStackHandler inventory;
    private int livingTicks = 0;
    private HashSet<BlockPos> fogBlocks = new HashSet<>();

    public TileEntityNanoFogTerminal() {
        super("os_nanofog_terminal");
        inventory = new ItemStackHandler(inventorySlots);
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(512).create();
    }

    @Override
    public void validate(){
        super.validate();

        if(!world.isRemote)
            fakePlayer = new FakePlayer(DimensionManager.getWorld(getWorld().provider.getDimension()), new GameProfile(UUID.randomUUID(), getComponentName()));
    }

    public void removed(){
        // unset all fog blocks
        resetAllBlocks();

        // drop inventory
        for(int slot=0; slot < inventory.getSlots(); slot++)
            ItemUtils.dropItem(inventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);        
    }

    @Override
    public void markDirty(){
        super.markDirty();

        IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
    }

    public HashSet<BlockPos> getFogBlocks(){
        return fogBlocks;
    }

    @Override
    public void update() {
        super.update();

        if(getWorld().isRemote)
            return;

        if(livingTicks++ < 100)
            return;

        if(!consumeUpkeepEnergy())
            resetAllBlocks();
    }

    @Callback(doc = "function():boolean; returns material buffer", direct = true)
    public Object[] getMaterial(Context context, Arguments args) {
        return new Object[]{ inventory.getStackInSlot(0) };
    }

    @Callback(doc = "function():array; returns fog locations", direct = true)
    public Object[] getBlocks(Context context, Arguments args) {
        return new Object[]{ getFogBlocks().toArray() };
    }

    @Callback(doc = "function(int x, int y, int z, string material [, int metaindex]):boolean; set a block", direct = false)
    public Object[] set(Context context, Arguments args) {
        if(args.count() < 4)
            return new Object[]{ "not enough arguments" };

        BlockPos worldPosition = getPos(args);
        if(worldPosition == null)
            return new Object[]{ false, "invalid arguments" };


        String material = args.checkString(3);

        int metadata = args.count() > 4 ? args.checkInteger(4) : 0;

        return setBlock(worldPosition, material, metadata);
    }

    @Callback(doc = "function(int x1, int y1, int z1, int x2, int y2, int z2, string material [, int metaindex]):boolean; set a block", direct = false)
    public Object[] setArea(Context context, Arguments args) {
        if(args.count() < 7)
            return new Object[]{ "not enough arguments" };

        BlockPos worldPositionA = getPos(args);
        BlockPos worldPositionB = getPos(args, 3);
        if(worldPositionA == null || worldPositionB == null)
            return new Object[]{ false, "invalid arguments" };

        String material = args.checkString(6);

        int metadata = args.count() > 7 ? args.checkInteger(7) : 0;

        ArrayList<Object> list = new ArrayList<>();

        for(int x = worldPositionA.getX(); x <= worldPositionB.getX(); x++)
            for(int y = worldPositionA.getY(); y <= worldPositionB.getY(); y++)
                for(int z = worldPositionA.getZ(); z <= worldPositionB.getZ(); z++)
                    list.add(setBlock(new BlockPos(x, y, z), material, metadata));

        return new Object[]{ list.toArray() };
    }

    @Callback(doc = "function(int x1, int y1, int z1, int x2, int y2, int z2, string material [, int metaindex]):boolean; set a block", direct = false)
    public Object[] resetArea(Context context, Arguments args) {
        if (args.count() < 7)
            return new Object[]{"not enough arguments"};

        BlockPos worldPositionA = getPos(args);
        BlockPos worldPositionB = getPos(args, 3);
        if (worldPositionA == null || worldPositionB == null)
            return new Object[]{false, "invalid arguments"};

        ArrayList<Object> list = new ArrayList<>();

        for(int x = worldPositionA.getX(); x <= worldPositionB.getX(); x++)
            for(int y = worldPositionA.getY(); y <= worldPositionB.getY(); y++)
                for(int z = worldPositionA.getZ(); z <= worldPositionB.getZ(); z++)
                    list.add(removeBlock(new BlockPos(x, y, z)));

        return new Object[]{ list.toArray() };
    }

    private Object[] setBlock(BlockPos worldPosition, String material, int metadata){
        switch(getBlock(worldPosition)){
            case "air":
                if(getPos().getDistance(pos.getX(), pos.getY(), pos.getZ()) > terminalRange)
                    return new Object[]{ false, "block out of range" };
                if(getFogBlocks().size() == FogBlockLimit)
                    return new Object[]{ false, "block limit reached" };
                return new Object[]{ consumeBuildEnergy() && consumeMaterial() && setShieldBlock(worldPosition, material, metadata).equals("nanoFog") };
            case "nanoFog":
                return new Object[]{ updateShieldBlock(worldPosition, material, metadata).equals("nanoFog") };
            default:
                return new Object[]{ false, "target block is not air" };
        }
    }

    @Callback(doc = "function(int x, int y, int z):boolean; set a fog block to solid", direct = true)
    public Object[] setSolid(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false };

        fog.setSolid(true);
        return new Object[]{ true };
    }

    @Callback(doc = "function(int x, int y, int z):boolean; set a fog block to shield", direct = true)
    public Object[] setShield(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false };

        fog.setSolid(false);
        return new Object[]{ true };
    }

    @Callback(doc = "function(int x, int y, int z, int power):boolean; set knockback power [0, 1, 2]", direct = true)
    public Object[] setKnockback(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false };

        int knockback = args.checkInteger(3);
        if(knockback < 0)
            knockback = 0;
        if(knockback > maxKnockback)
            knockback = maxKnockback;

        fog.setKnockback(knockback);

        return new Object[]{ true };
    }

    @Callback(doc = "function(int x, int y, int z, int power):boolean; set damage power [0 - 5]", direct = true)
    public Object[] setDamage(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false };

        int damage = args.checkInteger(3);
        if(damage < 0)
            damage = 0;
        if(damage > maxDamage)
            damage = maxDamage;

        fog.setDamage(damage);

        return new Object[]{ true };
    }

    @Callback(doc = "function():array; displays a list of all available entities", direct = true)
    public Object[] getEntityClassNames(Context context, Arguments args) {
        HashSet<Object> entities = new HashSet<>();
        for(Class<? extends Entity> c : ClassHelper.getEntityList())
            entities.add(c.getSimpleName());

        return new Object[]{ entities.toArray() };
    }

    @Callback(doc = "function(int x, int y, int z, string type/class, boolean passEntity, [boolean damageEntity (false) , string name (\"\")]):boolean; sets fog filter", direct = true)
    public Object[] setFilter(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false };


        String filterType = args.checkString(3).toLowerCase();
        boolean allowPass = args.checkBoolean(4);
        boolean damageEntity = args.count() >= 6 && args.checkBoolean(5);
        String name = args.count() >= 7 ? args.checkString(6) : "";

        Class<? extends Entity> clazz;

        switch(filterType){
            case "player":  clazz = EntityPlayer.class; break;
            case "hostile": clazz = EntityMob.class; break;
            case "animal":  clazz = EntityAnimal.class; break;
            case "item":    clazz = EntityItem.class; break;
            case "all":     clazz = Entity.class; break;
            default:
                clazz = Entity.class;
                for(Class<? extends Entity> c : ClassHelper.getEntityList()){
                    if(c.getSimpleName().toLowerCase().equals(filterType.toLowerCase()))
                        clazz = c;
                }
        }

        if(allowPass)
            fog.filterPass.add(clazz, name);
        else
            fog.filterPass.remove(clazz, name);

        if(damageEntity)
            fog.filterDamage.add(clazz, name);
        else
            fog.filterDamage.remove(clazz, name);

        fog.markDirtyClient();

        return new Object[]{ true };
    }

    @Callback(doc = "function(int x, int y, int z):array; get pass filter", direct = true)
    public Object[] getFilterPass(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false, "no fog block" };

        ArrayList<Object[]> list = new ArrayList<>();
        for(Map.Entry<String, HashSet<String>> entry : fog.filterPass.getList().entrySet())
            list.add(new Object[]{ entry.getKey(), entry.getValue().toArray() });

        return list.toArray();
    }

    @Callback(doc = "function(int x, int y, int z):array; get damage filter", direct = true)
    public Object[] getFilterDamage(Context context, Arguments args) {
        TileEntityNanoFog fog = getFog(getPos(args));
        if(fog == null)
            return new Object[]{ false, "no fog block" };

        ArrayList<Object[]> list = new ArrayList<>();
        for(Map.Entry<String, HashSet<String>> entry : fog.filterDamage.getList().entrySet())
            list.add(new Object[]{ entry.getKey(), entry.getValue().toArray() });

        return list.toArray();
    }

    @Callback(doc = "function(int x, int y, int z):string; get block type", direct = true)
    public Object[] get(Context context, Arguments args) {
        if(args.count() < 3)
            return new Object[]{ "not enough arguments" };

        return new Object[]{ getFog(getPos(args)) != null ? "nanoFog" : "unknown" };
    }

    @Callback(doc = "function(int x, int y, int z):boolean; disassemble fog block", direct = false)
    public Object[] reset(Context context, Arguments args) {
        if(args.count() < 3)
            return new Object[]{ false, "not enough arguments" };

        TileEntityNanoFog fog = getFog(getPos(args));

        if(fog == null)
            return new Object[]{ false, "not a fog block" };

        removeBlock(getPos(args));
        return new Object[]{ getFog(getPos(args)) == null };
    }

    @Callback(doc = "function():boolean; disassembles all fog blocks", direct = false)
    public Object[] resetAll(Context context, Arguments args) {
        resetAllBlocks();
        return new Object[]{ getFogBlocks().size() == 0 };
    }

    void resetAllBlocks(){
        HashSet<BlockPos> blocks = new HashSet<>();
        blocks.addAll(getFogBlocks());
        for(BlockPos pos : blocks)
            removeBlock(pos);
    }

    public boolean consumeKnockbackEnergy(int knockbackPower){
        return node.tryChangeBuffer(-knockbackCost * knockbackPower);
    }

    public boolean consumeDamageEnergy(int damagePower){
        return node.tryChangeBuffer(-damageCost * damagePower);
    }

    private boolean consumeMaterial(){
        ItemStack stack = inventory.getStackInSlot(0);

        if(stack.getItem().equals(nanoDNAItem) && stack.getCount() > 0){
            stack.shrink(1);
            inventory.setStackInSlot(0, stack);

            return true;
        }

        return false;
    }

    private boolean consumeUpkeepEnergy(){
        return node.tryChangeBuffer(-FogUpkeepCost * getFogBlocks().size());
    }

    public boolean consumeBuildEnergy(){
        return node.tryChangeBuffer(-FogBuildCost);
    }

    private void updateEnergyBufferSize(){
        node.setLocalBufferSize(4096 + 10 * getFogBlocks().size());
    }

    private String setShieldBlock(BlockPos pos, String material, int metadata){
        placeBlock(pos, new ItemStack(nanoFog, 1));
        updateEnergyBufferSize();
        TileEntityNanoFog te = (TileEntityNanoFog) getWorld().getTileEntity(pos);
        if(te == null)
            return getBlock(pos);

        if(Config.getConfig().getCategory("general").get("instantNanoFog").getBoolean()){
            te.isBuild = true;
        } else {
            EntityNanoFogSwarm entity = new EntityNanoFogSwarm(world);
            entity.setTravelToFogBlock(getPos(), pos);
            getWorld().spawnEntity(entity);
        }

        te.setTerminalLocation(getPos());
        markDirty();
        return updateShieldBlock(pos, material, metadata);
    }

    @Deprecated
    private String updateShieldBlock(BlockPos pos, String material, int metadata){
        TileEntityNanoFog te = (TileEntityNanoFog) getWorld().getTileEntity(pos);

        te.setCamoBlock(Block.getBlockFromName(material), metadata);
        return getBlock(pos);
    }


    TileEntityNanoFog getFog(BlockPos pos){
        if(pos == null) return null;
        TileEntity te = getWorld().getTileEntity(pos);
        return te instanceof TileEntityNanoFog ? (TileEntityNanoFog) te : null;
    }

    // maps relative cli arguments to absolute world position
    BlockPos getPos(Arguments args){
        return getPos(args, 0);
    }

    BlockPos getPos(Arguments args, int offset){
        if(args.count() < 3 + offset) return null;
        int x = args.checkInteger(0 + offset);
        int y = args.checkInteger(1 + offset);
        int z = args.checkInteger(2 + offset);

        return new BlockPos(x, y, z).add(getPos());
    }

    public boolean returnFogMaterial(ItemStack outputStack){
        if(outputStack.isEmpty())
            return false;

        ItemStack inventoryInputStack = inventory.getStackInSlot(0);

        // try to move to input
        if(inventoryInputStack.getCount() == 0) {
            inventory.setStackInSlot(0, outputStack);
            return true;
        }

        if(inventoryInputStack.getCount() < 64) {
            outputStack.grow(inventoryInputStack.getCount());
            inventory.setStackInSlot(0, outputStack);
            return true;
        }

        ItemStack inventoryOutputStack = inventory.getStackInSlot(1);

        // try to move to output
        if(inventoryOutputStack.getCount() == 0) {
            inventory.setStackInSlot(1, outputStack);
            return true;
        }

        if(inventoryOutputStack.getCount() < 64) {
            outputStack.grow(inventoryOutputStack.getCount());
            inventory.setStackInSlot(1, outputStack);
            return true;
        }

        // drop at terminal location
        ItemUtils.dropItem(outputStack, getWorld(), getPos(), false, 10);
        return true;
    }

    boolean removeBlock(BlockPos pos){
        if(!fogBlocks.contains(pos))
            return false;

        fogBlocks.remove(pos);

        if(Config.getConfig().getCategory("general").get("instantNanoFog").getBoolean())
            getFog(pos).notifyRemove();
        else {
            EntityNanoFogSwarm entity = new EntityNanoFogSwarm(world);
            entity.setTravelToTerminal(pos, getPos());
            getWorld().spawnEntity(entity);
        }

        updateEnergyBufferSize();
        markDirty();

        return true;
    }

    String getBlock(BlockPos pos){
        if(getWorld().isAirBlock(pos))
            return "air";

        TileEntity te = getWorld().getTileEntity(pos);

        return te instanceof TileEntityNanoFog ? "nanoFog" : te.getClass().toString();
    }

    public void placeBlock(BlockPos pos, ItemStack consumedStack){
        getWorld().setBlockState(pos, BlockUtils.placeStackAt(fakePlayer, consumedStack, getWorld(), pos, null), 3);
        fogBlocks.add(pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventory.deserializeNBT(data.getCompoundTag("invIn"));

        fogBlocks.clear();
        for(int shieldBlock=0; data.hasKey("sBX"+shieldBlock); shieldBlock++){
            fogBlocks.add(new BlockPos(
                    data.getInteger("sBX"+shieldBlock),
                    data.getInteger("sBY"+shieldBlock),
                    data.getInteger("sBZ"+shieldBlock)
            ));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("invIn", inventory.serializeNBT());

        int shieldBlock = 0;
        for(BlockPos pos : fogBlocks){
            data.setInteger("sBX"+shieldBlock, pos.getX());
            data.setInteger("sBY"+shieldBlock, pos.getY());
            data.setInteger("sBZ"+shieldBlock, pos.getZ());

            shieldBlock++;
        }

        return data;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.UP) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }

        return super.getCapability(capability, facing);
    }



}
