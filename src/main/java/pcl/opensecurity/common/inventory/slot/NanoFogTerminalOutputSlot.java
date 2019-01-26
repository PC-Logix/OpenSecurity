package pcl.opensecurity.common.inventory.slot;

import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;

public class NanoFogTerminalOutputSlot extends NanoFogTerminalSlot {
    public NanoFogTerminalOutputSlot(TileEntityNanoFogTerminal tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
        validItems.clear();
    }

}
