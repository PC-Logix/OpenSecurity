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
import pcl.opensecurity.common.tileentity.TileEntityDataBlock;

public class BlockData extends BlockOSBase {

	public BlockData(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("data_block");
		setRegistryName("data_block");
		setHardness(.5f);
		random = new Random();
	}
		
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityDataBlock();
	}
}
