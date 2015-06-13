package pcl.opensecurity.gui;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.containers.RFIDWriterContainer;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class CardWriterGUI extends GuiContainer {

    public CardWriterGUI (InventoryPlayer inventoryPlayer, TileEntityCardWriter tileEntity) {
        //the container is instanciated and passed to the superclass for handling
        super(new RFIDWriterContainer(inventoryPlayer, tileEntity));
}

@Override
protected void drawGuiContainerForegroundLayer(int param1, int param2) {
	//the parameters for drawString are: string, x, y, color
	mc.fontRenderer.drawSplitString(StatCollector.translateToLocal("gui.string.cardslot"), 78, ySize - 160, 40, 4210752);
	mc.fontRenderer.drawString(StatCollector.translateToLocal("gui.string.cardwriter"), 62, -12, 4210752);
}

@Override
protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/gui/rfidwriter.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - 196) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, 196);
}
	
}