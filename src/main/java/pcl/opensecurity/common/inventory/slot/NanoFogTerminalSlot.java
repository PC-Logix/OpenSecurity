package pcl.opensecurity.common.inventory.slot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.items.ItemNanoDNA;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class NanoFogTerminalSlot extends BaseSlot<TileEntityNanoFogTerminal> {

    ArrayList<Item> validItems = new ArrayList<>();

    public NanoFogTerminalSlot(TileEntityNanoFogTerminal tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);

        validItems.add(ItemNanoDNA.DEFAULTSTACK.getItem());
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return validItems.contains(stack.getItem());
    }

    @Override
    public void onSlotChanged(){
        super.onSlotChanged();
    }
}

