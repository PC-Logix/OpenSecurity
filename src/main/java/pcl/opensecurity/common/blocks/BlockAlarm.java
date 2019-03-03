package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;

public class BlockAlarm extends BlockOSBase {
    public static final String NAME = "alarm";
    public static Block DEFAULTITEM;

    public BlockAlarm() {
        super(NAME, Material.IRON, 0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAlarm();
    }
}
