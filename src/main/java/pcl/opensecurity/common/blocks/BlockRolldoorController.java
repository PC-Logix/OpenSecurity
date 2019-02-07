package pcl.opensecurity.common.blocks;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class BlockRolldoorController extends BlockDoorController {
    public static final String NAME = "rolldoor_controller";

    public BlockRolldoorController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityRolldoorController();
    }
}
