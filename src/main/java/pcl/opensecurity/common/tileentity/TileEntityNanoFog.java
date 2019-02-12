package pcl.opensecurity.common.tileentity;

/* McJty's RFTools Shield was reference for this! :) */
/**
 * @author ben_mkiv
 */
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.nanofog.EntityFilter;
import pcl.opensecurity.util.ICamo;
import pcl.opensecurity.util.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static pcl.opensecurity.common.ContentRegistry.nanoDNAItem;


public class TileEntityNanoFog extends TileEntity implements ICamo {
    MimicBlock mimicBlock = new MimicBlock();

    public EntityFilter filterPass = new EntityFilter();
    public EntityFilter filterDamage = new EntityFilter();

    private boolean isSolid = false;
    private int knockback = 1, damage = 1;

    private BlockPos terminal;

    boolean isBuild = false;

    public TileEntityNanoFog(){}

    public TileEntityNanoFogTerminal getTerminal(){
        TileEntity te = getWorld().getTileEntity(terminal);
        return te != null ? (TileEntityNanoFogTerminal) te : null;
    }

    public void setTerminalLocation(BlockPos term){ terminal = term; }

    public int getKnockback() {
        return knockback;
    }

    public void setKnockback(int state){
        this.knockback = state;
        markDirtyClient();
    }

    public boolean isBuild(){
        return isBuild;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int state){
        this.damage = state;
        markDirtyClient();
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean state){
        this.isSolid = state;
        markDirtyClient();
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public IBlockState getCamoBlock() {
        return mimicBlock.get();
    }

    @Deprecated
    @Override
    public void setCamoBlock(Block block, int meta) {
        mimicBlock.set(block, meta);
        markDirtyClient();
    }

    @Override
    public boolean playerCanChangeCamo(EntityPlayer player){
        return false;
    }

    public void notifyBuild(){
        isBuild = true;
        markDirtyClient();
    }

    public ItemStack notifyRemove(){
        ItemStack stack = ItemStack.EMPTY;

        if(getWorld().isRemote){
            isBuild = false;
            markDirtyClient();
        }
        else {
            stack = getFogMaterial();
            getWorld().setBlockToAir(getPos());
        }

        return stack;
    }


    private ItemStack getFogMaterial(){
        TileEntityNanoFogTerminal te = getTerminal();

        if(te != null && te.consumeBuildEnergy())
            return new ItemStack(nanoDNAItem, 1);
        else {
            // drop at block location if energy isnt sufficient for fog to come back to terminal
            ItemUtils.dropItem(new ItemStack(nanoDNAItem, 1), getWorld(), getPos(), false, 10);
            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setTag("camo", mimicBlock.writeToNBT(new NBTTagCompound()));

        tagCompound.setInteger("knockback", knockback);
        tagCompound.setInteger("damageI", damage);

        tagCompound.setTag("damage", filterDamage.writeToNBT(new NBTTagCompound()));
        tagCompound.setTag("pass", filterPass.writeToNBT(new NBTTagCompound()));

        if(terminal != null) {
            tagCompound.setTag("terminal", NBTUtil.createPosTag(terminal));
        }

        tagCompound.setBoolean("solid", isSolid);
        tagCompound.setBoolean("build", isBuild);

        return tagCompound;
    }

    @Override
    @Deprecated
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        if(tagCompound.hasKey("camo"))
            mimicBlock.readFromNBT(tagCompound.getCompoundTag("camo"));
        else // compat, can be removed after some time has passed... :>
            mimicBlock.readFromNBT(tagCompound);

        isSolid = tagCompound.getBoolean("solid");
        isBuild = tagCompound.getBoolean("build");
        knockback = tagCompound.getInteger("knockback");
        damage = tagCompound.getInteger("damageI");
        filterDamage.readFromNBT(tagCompound.getCompoundTag("damage"));
        filterPass.readFromNBT(tagCompound.getCompoundTag("pass"));


        if(tagCompound.hasKey("terminal"))
            terminal = NBTUtil.getPosFromTag(tagCompound.getCompoundTag("terminal"));
        //keep for compat to old versions
        if(tagCompound.hasKey("termX"))
            terminal = new BlockPos(tagCompound.getInteger("termX"), tagCompound.getInteger("termY"), tagCompound.getInteger("termZ"));

        if (getWorld() != null && getWorld().isRemote) {
            // For some reason this is needed to force rendering on the client when apply is pressed.
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

}
