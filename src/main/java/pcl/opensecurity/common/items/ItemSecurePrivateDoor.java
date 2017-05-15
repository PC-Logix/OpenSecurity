package pcl.opensecurity.common.items;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockSecurePrivateDoor;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

public class ItemSecurePrivateDoor extends ItemBlock {
	
	private final Block block;
	
	public ItemSecurePrivateDoor(Block block) {
		super(block);
        this.block = block;
		// TODO Auto-generated constructor stub
	}
	
    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (facing != EnumFacing.UP)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();

            if (!block.isReplaceable(worldIn, pos))
            {
                pos = pos.offset(facing);
            }

            if (playerIn.canPlayerEdit(pos, facing, stack) && this.block.canPlaceBlockAt(worldIn, pos))
            {
                EnumFacing enumfacing = EnumFacing.fromAngle((double)playerIn.rotationYaw);
                int i = enumfacing.getFrontOffsetX();
                int j = enumfacing.getFrontOffsetZ();
                boolean flag = i < 0 && hitZ < 0.5F || i > 0 && hitZ > 0.5F || j < 0 && hitX > 0.5F || j > 0 && hitX < 0.5F;
                placeDoor(worldIn, pos, enumfacing, this.block, flag, playerIn);
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                --stack.stackSize;
                return EnumActionResult.SUCCESS;
            }
            else
            {
                return EnumActionResult.FAIL;
            }
        }
    }

    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge, EntityPlayer playerIn)
    {
        BlockPos blockpos = pos.offset(facing.rotateY());
        BlockPos blockpos1 = pos.offset(facing.rotateYCCW());
        int i = (worldIn.getBlockState(blockpos1).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos1.up()).isNormalCube() ? 1 : 0);
        int j = (worldIn.getBlockState(blockpos).isNormalCube() ? 1 : 0) + (worldIn.getBlockState(blockpos.up()).isNormalCube() ? 1 : 0);
        boolean flag = worldIn.getBlockState(blockpos1).getBlock() == door || worldIn.getBlockState(blockpos1.up()).getBlock() == door;
        boolean flag1 = worldIn.getBlockState(blockpos).getBlock() == door || worldIn.getBlockState(blockpos.up()).getBlock() == door;

        if ((!flag || flag1) && j <= i)
        {
            if (flag1 && !flag || j < i)
            {
                isRightHinge = false;
            }
        }
        else
        {
            isRightHinge = true;
        }

        BlockPos blockpos2 = pos.up();
        IBlockState iblockstate = door.getDefaultState().withProperty(BlockSecurePrivateDoor.FACING, facing).withProperty(BlockSecurePrivateDoor.HINGE, isRightHinge ? BlockSecurePrivateDoor.EnumHingePosition.RIGHT : BlockSecurePrivateDoor.EnumHingePosition.LEFT).withProperty(BlockSecurePrivateDoor.OPEN, false);
        worldIn.setBlockState(pos, iblockstate.withProperty(BlockSecurePrivateDoor.HALF, BlockSecurePrivateDoor.EnumDoorHalf.LOWER), 2);
        TileEntitySecureDoor teLower = (TileEntitySecureDoor) worldIn.getTileEntity(pos);
        teLower.setOwner(playerIn.getUniqueID().toString());
        worldIn.setBlockState(blockpos2, iblockstate.withProperty(BlockSecurePrivateDoor.HALF, BlockSecurePrivateDoor.EnumDoorHalf.UPPER), 2);
        TileEntitySecureDoor teUpper = (TileEntitySecureDoor) worldIn.getTileEntity(blockpos2);
        teUpper.setOwner(playerIn.getUniqueID().toString());
        worldIn.notifyNeighborsOfStateChange(pos, door);
        worldIn.notifyNeighborsOfStateChange(blockpos2, door);
    }
}
