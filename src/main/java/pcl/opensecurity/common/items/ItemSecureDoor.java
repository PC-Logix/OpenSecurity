package pcl.opensecurity.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.ContentRegistry;

import javax.annotation.Nonnull;

public class ItemSecureDoor extends ItemDoor {

    private Block block;

    public ItemSecureDoor() {
        this(ContentRegistry.secureDoor);
    }

    @SuppressWarnings("ConstantConditions")
    ItemSecureDoor(Block block) {
        super(block);
        this.block = block;

        setRegistryName(block.getRegistryName());
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return block.getUnlocalizedName();
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }
}