package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.network.EnvironmentHost;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import pcl.opensecurity.util.ICamo;

public class TileEntityOSCamoBase extends TileEntityOSBase implements ICamo {
    MimicBlock mimicBlock = new MimicBlock();

    public TileEntityOSCamoBase(String name){
        super(name);
    }

    public TileEntityOSCamoBase(String name, EnvironmentHost host){
        super(name, host);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("camo"))
            mimicBlock.readFromNBT(nbt.getCompoundTag("camo"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("camo", mimicBlock.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(nbt);
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

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }
}
