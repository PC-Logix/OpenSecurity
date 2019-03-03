package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;

public class BlockNanoFogTerminal extends BlockOSBase {
    public static final String NAME = "nanofog_terminal";
    public static Block DEFAULTITEM;

    public static final int GUI_ID = 3;


    public BlockNanoFogTerminal() {
        super(NAME, Material.IRON, 1f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityNanoFogTerminal();
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityNanoFogTerminal)) {
            return false;
        }
        player.openGui(OpenSecurity.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityNanoFogTerminal) {
                ((TileEntityNanoFogTerminal) te).removed();
            }
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }


}



