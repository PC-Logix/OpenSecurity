package pcl.opensecurity.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockNanoFog;

public class ItemNanoDNA extends ItemBlock {
    public static final String NAME = "nanodna";
    public static ItemStack DEFAULTSTACK;

    public ItemNanoDNA() {
        super(BlockNanoFog.DEFAULTITEM);
        setUnlocalizedName("opensecurity." + NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Override
    /* dont allow players to place this block */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        return EnumActionResult.FAIL;
    }
}