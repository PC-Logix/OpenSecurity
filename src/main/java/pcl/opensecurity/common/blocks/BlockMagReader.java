package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntityMagReader;

public class BlockMagReader extends BlockOSBase {


	public BlockMagReader(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("mag_reader");
		setRegistryName("mag_reader");
		setHardness(.5f);
		random = new Random();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMagReader();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		Item equipped = heldItem.getItem();
		TileEntityMagReader tile = (TileEntityMagReader) world.getTileEntity(pos);
		if (!world.isRemote && equipped instanceof ItemMagCard) {
			if (tile.doRead(heldItem, player, side.getIndex())) {
				//world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3, 1);
			} else {
				//world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2, 1);
			}
			//world.scheduleBlockUpdate(xCoord, yCoord, zCoord, this, 30);
		}
		return true;
	}

}
