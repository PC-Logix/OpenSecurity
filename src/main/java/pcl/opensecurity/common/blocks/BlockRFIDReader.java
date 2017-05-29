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
import pcl.opensecurity.common.tileentity.TileEntityRFIDReader;

public class BlockRFIDReader extends BlockOSBase {


	public BlockRFIDReader(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("rfid_reader");
		setRegistryName("rfid_reader");
		setHardness(.5f);
		random = new Random();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRFIDReader();
	}

}
