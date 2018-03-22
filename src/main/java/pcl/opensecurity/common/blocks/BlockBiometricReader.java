package pcl.opensecurity.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityBiometricReader;

public class BlockBiometricReader extends BlockOSBase {

	public BlockBiometricReader(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("biometric_reader");
		setRegistryName("biometric_reader");
		setHardness(.5f);
		random = new Random();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntityBiometricReader tile = (TileEntityBiometricReader) worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && facing.getOpposite().getHorizontalIndex() == state.getBlock().getMetaFromState(state) && hand.equals(EnumHand.OFF_HAND)) {
			tile.doRead(playerIn, facing);
			return true;
		}
        return facing.getIndex() == state.getBlock().getMetaFromState(state);
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityBiometricReader();
	}
}
