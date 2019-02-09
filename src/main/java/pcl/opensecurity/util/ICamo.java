package pcl.opensecurity.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public interface ICamo {
    IBlockState getCamoBlock();
    void setCamoBlock(Block block, int meta);

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
            nbt.setInteger("camoId", camoId);
            nbt.setInteger("camoMeta", camoMeta);
            return nbt;
        }

        public void readFromNBT(NBTTagCompound nbt){
            camoId = nbt.getInteger("camoId");
            camoMeta = nbt.getInteger("camoMeta");
            mimic = camoId != -1 ? Block.getBlockById(camoId).getStateFromMeta(camoMeta) : null;
        }

    }
}
