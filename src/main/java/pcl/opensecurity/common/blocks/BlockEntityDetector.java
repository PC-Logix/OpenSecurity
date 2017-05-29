package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;

/**
 * Created by Michi on 5/29/2017.
 */
public class BlockEntityDetector extends BlockOSBase {

    public BlockEntityDetector(Material materialIn) {
        super(materialIn);
        setUnlocalizedName("entity_detector");
        setRegistryName("entity_detector");
        setHardness(.5f);
        // TODO Auto-generated constructor stub
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEntityDetector();
    }

}
