package pcl.opensecurity.common.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.items.ItemSHFRFIDCard;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntitySHFRFIDReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

public class SHRFIDSlot extends BaseSlot<TileEntitySHFRFIDReader> implements ISlotTooltip {

    public SHRFIDSlot(TileEntitySHFRFIDReader te, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(te, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return (stack.getItem() instanceof ItemSHFRFIDCard);
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList("Accepted Items:", "SHRFID Card"));
    }
}
