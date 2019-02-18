package pcl.opensecurity.common.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ICamo {
    IBlockState getCamoBlock();
    void setCamoBlock(Block block, int meta);

    default boolean setCamoBlock(EntityPlayer player, ItemStack stack){
        return playerCanChangeCamo(player) && setCamoBlock(stack);
    }

    default boolean setCamoBlock(ItemStack stack){
        if(!isValidCamoBlock(stack))
            return false;

        setCamoBlock(Block.getBlockFromItem(stack.getItem()), stack.getMetadata());
        return true;
    }

    default boolean playerCanChangeCamo(EntityPlayer player){
        return true;
    }

    default boolean isValidCamoBlock(ItemStack stack){
        if(stack.isEmpty())
            return false;

        if(!(stack.getItem() instanceof ItemBlock))
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());

        if(block.isFullCube(block.getDefaultState()))
            return true;

        if(block instanceof BlockGlass || block instanceof BlockStainedGlass)
            return true;

        return false;
    }

    class MimicBlock{
        private IBlockState mimic = null;
        public int camoId = -1;
        public int camoMeta = 0;

        public IBlockState get() {
            return mimic != null ? mimic : Blocks.AIR.getDefaultState();
        }

        @Deprecated
        public void set(Block block, int meta) {
            this.camoId = Block.getIdFromBlock(block);
            this.camoMeta = meta;
            mimic = camoId != -1 ? Block.getBlockById(camoId).getStateFromMeta(meta) : null;
        }

        public NBTTagCompound writeToNBT(NBTTagCompound nbt){
            nbt.setInteger("id", camoId);
            nbt.setInteger("meta", camoMeta);
            return nbt;
        }

        public void readFromNBT(NBTTagCompound nbt){
            camoId = nbt.getInteger("id");
            camoMeta = nbt.getInteger("meta");
            mimic = camoId != -1 ? Block.getBlockById(camoId).getStateFromMeta(camoMeta) : null;
        }

    }
}
