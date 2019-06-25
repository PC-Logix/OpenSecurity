package pcl.opensecurity.common.inventory.slot;

import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

public class NanoFogTerminalOutputSlot extends NanoFogTerminalSlot {
    public NanoFogTerminalOutputSlot(TileEntityNanoFogTerminal tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
        validItems.clear();
    }

    @Override
    public List<String> getTooltip(){
        return new ArrayList<>(Arrays.asList(new String[]{"ejects:", "NanoDNA"}));
    }

}
