package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityDataBlock;

public class BlockData extends BlockOSBase {
    public static final String NAME = "data_block";
    public static Block DEFAULTITEM;

    public BlockData() {
        super(NAME, Material.IRON, 0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityDataBlock();
    }
}
