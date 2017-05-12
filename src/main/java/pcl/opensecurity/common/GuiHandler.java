package pcl.opensecurity.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import pcl.opensecurity.client.gui.CardWriterGUI;
import pcl.opensecurity.common.inventory.CardWriterContainer;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCardWriter) {
            return new CardWriterContainer(player.inventory, (TileEntityCardWriter) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCardWriter) {
        	TileEntityCardWriter containerTileEntity = (TileEntityCardWriter) te;
            return new CardWriterGUI(containerTileEntity, new CardWriterContainer(player.inventory, containerTileEntity));
        }
        return null;
    }
}