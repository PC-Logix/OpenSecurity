package pcl.opensecurity.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;

public class BlockKeypad extends BlockOSBase {

	public BlockKeypad(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("keypad");
		setRegistryName("keypad");
		setHardness(.5f);
		// TODO Auto-generated constructor stub
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return side.getOpposite().getHorizontalIndex() != blockState.getBlock().getMetaFromState(blockState);
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityKeypad();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		//BLLog.debug("Activate with hit at %f, %f, %f", hitX, hitY, hitZ);
		if (player.isSneaking())
			return false;
		if (side.getOpposite().getHorizontalIndex() != state.getBlock().getMetaFromState(state)) {
			return false;
		}
		
		//BLLog.debug("side = %d", side);
		float relX=0f,relY=hitY*16f;
		//normalize face-relative "x" pixel position
		switch(side.getIndex())
		{
		case 2: relX=hitX*16f; break;
		case 3: relX=(1f-hitX)*16f; break;
		case 4: relX=(1f-hitZ)*16f; break;
		case 5: relX=hitZ*16f; break;
		default:
			break;
		}
		
		//figure out what, if any, button was hit?
		if (relX<4f || relX>12 || relY<2f || relY>11.5f)
		{
			//BLLog.debug("outside button area.");			
			//completely outside area of buttons, return
			return true;
		}
		int col=(int)((relX-4f)/3f);
		float colOff=(relX-4f)%3f;
		int row=(int)((relY-2f)/2.5f);
		float rowOff=(relY-2f)%2.5f;
		//check and return if between buttons
		if (colOff>2f || rowOff>2f)
		{
			//BLLog.debug("between buttons.");
			return true;
		}		
		
		//ok! hit a button!
		//BLLog.debug("Hit button on row %d in col %d", row, col);
		int idx = (2-col)+3*(3-row);
		TileEntityKeypad te=(TileEntityKeypad)world.getTileEntity(pos);
		te.pressedButton(player,idx);
		return true;
	}

}
