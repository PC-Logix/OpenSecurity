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
import pcl.opensecurity.common.blocks.BlockSecureDoor;

public class ItemSecureDoor extends ItemBlock
{
    private final Block block;

    public ItemSecureDoor(Block block)
    {
    	super(block);
        this.block = block;
		//setUnlocalizedName("secure_door");
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
                placeDoor(worldIn, pos, enumfacing, this.block, flag);
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

    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door, boolean isRightHinge)
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
        IBlockState iblockstate = door.getDefaultState().withProperty(BlockSecureDoor.FACING, facing).withProperty(BlockSecureDoor.HINGE, isRightHinge ? BlockSecureDoor.EnumHingePosition.RIGHT : BlockSecureDoor.EnumHingePosition.LEFT).withProperty(BlockSecureDoor.OPEN, false);
        worldIn.setBlockState(pos, iblockstate.withProperty(BlockSecureDoor.HALF, BlockSecureDoor.EnumDoorHalf.LOWER), 2);
        worldIn.setBlockState(blockpos2, iblockstate.withProperty(BlockSecureDoor.HALF, BlockSecureDoor.EnumDoorHalf.UPPER), 2);
        worldIn.notifyNeighborsOfStateChange(pos, door);
        worldIn.notifyNeighborsOfStateChange(blockpos2, door);
    }
}