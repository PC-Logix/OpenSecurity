package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRFIDReader;

public class BlockRFIDReader extends BlockOSBase {
    public static final String NAME = "rfid_reader";
    public static Block DEFAULTITEM;

    public BlockRFIDReader() {
        super(NAME, Material.IRON, 0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRFIDReader();
    }

}
