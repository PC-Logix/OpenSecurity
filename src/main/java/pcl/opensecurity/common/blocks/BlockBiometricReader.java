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
		setCreativeTab(OpenSecurity.CreativeTab);
		setUnlocalizedName("biometric_reader");
		setHardness(.5f);
		random = new Random();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityBiometricReader tile = (TileEntityBiometricReader) world.getTileEntity(pos);
		if (!world.isRemote && side.getOpposite().getHorizontalIndex() == state.getBlock().getMetaFromState(state)) {
			tile.doRead(player, side);
			return true;
		}
		if (side.getIndex() == state.getBlock().getMetaFromState(state)) {
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityBiometricReader();
	}
}
