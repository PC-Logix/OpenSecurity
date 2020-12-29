package pcl.opensecurity.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.client.gui.CardWriterGUI;
import pcl.opensecurity.client.gui.EnergyTurretGUI;
import pcl.opensecurity.client.gui.NanoFogTerminalGUI;
import pcl.opensecurity.client.gui.SHFRFIDReaderGUI;
import pcl.opensecurity.common.inventory.CardWriterContainer;
import pcl.opensecurity.common.inventory.EnergyTurretContainer;
import pcl.opensecurity.common.inventory.NanoFogTerminalContainer;
import pcl.opensecurity.common.inventory.SHRFIDReaderContainer;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;
import pcl.opensecurity.common.tileentity.TileEntitySHFRFIDReader;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCardWriter) {
            return new CardWriterContainer(player.inventory, (TileEntityCardWriter) te);
        } else if (te instanceof TileEntityEnergyTurret) {
            return new EnergyTurretContainer(player.inventory, (TileEntityEnergyTurret) te);
        } else if (te instanceof TileEntityNanoFogTerminal) {
            return new NanoFogTerminalContainer(player.inventory, (TileEntityNanoFogTerminal) te);
        } else if (te instanceof TileEntitySHFRFIDReader) {
            return new SHRFIDReaderContainer(player.inventory, (TileEntitySHFRFIDReader) te);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCardWriter) {
        	TileEntityCardWriter containerTileEntity = (TileEntityCardWriter) te;
            return new CardWriterGUI(containerTileEntity, new CardWriterContainer(player.inventory, containerTileEntity));
        } else if (te instanceof TileEntityEnergyTurret) {
        	TileEntityEnergyTurret containerTileEntity = (TileEntityEnergyTurret) te;
            return new EnergyTurretGUI(containerTileEntity, new EnergyTurretContainer(player.inventory, containerTileEntity));
        } else if (te instanceof TileEntityNanoFogTerminal) {
            TileEntityNanoFogTerminal containerTileEntity = (TileEntityNanoFogTerminal) te;
            return new NanoFogTerminalGUI(containerTileEntity, new NanoFogTerminalContainer(player.inventory, containerTileEntity));
        } else if (te instanceof TileEntitySHFRFIDReader) {
        	TileEntitySHFRFIDReader containerTileEntity = (TileEntitySHFRFIDReader) te;
            return new SHFRFIDReaderGUI(containerTileEntity, new SHRFIDReaderContainer(player.inventory, containerTileEntity));
        }
        return null;
    }
}