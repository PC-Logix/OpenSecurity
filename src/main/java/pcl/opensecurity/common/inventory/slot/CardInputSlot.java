package pcl.opensecurity.common.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CardInputSlot extends BaseSlot<TileEntityCardWriter> implements ISlotTooltip {

    public CardInputSlot(TileEntityCardWriter tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return (stack.getItem() instanceof ItemCard || stack.getItem().equals(li.cil.oc.api.Items.get("eeprom").item()));
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList(new String[]{"Accepted Items:", "EEPROM", "Magnetic Card", "RFID Card"}));
    }
}
