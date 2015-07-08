package pcl.opensecurity.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.containers.CardWriterContainer;
import pcl.opensecurity.tileentity.TileEntityCardWriter;

public class CardWriterGUI extends GuiContainer {

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	public CardWriterGUI(InventoryPlayer inventoryPlayer, TileEntityCardWriter tileEntity) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(new CardWriterContainer(inventoryPlayer, tileEntity));
		this.xSize = 175;
		this.ySize = 195;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// the parameters for drawString are: string, x, y, color
		mc.fontRenderer.drawString(StatCollector.translateToLocal("gui.string.cardslot"), 64, 20, 4210752);
		mc.fontRenderer.drawString(StatCollector.translateToLocal("gui.string.cardwriter"), 60, 5, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		// draw your Gui here, only thing you need to change is the path
		ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/gui/rfidwriter.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}