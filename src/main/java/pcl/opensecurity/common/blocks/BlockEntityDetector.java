package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.Reference;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;

/**
 * Created by Michi on 5/29/2017.
 */
public class BlockEntityDetector extends BlockOSBase {

    public BlockEntityDetector() {
        super(Reference.Names.BLOCK_ENTITY_DETECTOR, Material.IRON, 0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEntityDetector();
    }

}
