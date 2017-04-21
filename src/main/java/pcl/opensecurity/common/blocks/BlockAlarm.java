package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;

public class BlockAlarm extends BlockOSBase {
	private Random random;

	public BlockAlarm(Material materialIn) {
		super(materialIn);
		setCreativeTab(OpenSecurity.CreativeTab);
		setUnlocalizedName("alarm");
		setHardness(.5f);
		random = new Random();
	}
	
}
