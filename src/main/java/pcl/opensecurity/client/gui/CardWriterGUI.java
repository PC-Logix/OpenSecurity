package pcl.opensecurity.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.CardWriterContainer;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;

public class CardWriterGUI extends GuiContainer {
    public static final int WIDTH = 175;
    public static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OpenSecurity.MODID, "textures/gui/rfidwriter.png");

    public CardWriterGUI(TileEntityCardWriter tileEntity, CardWriterContainer container) {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// the parameters for drawString are: string, x, y, color
		mc.fontRendererObj.drawString(I18n.translateToLocal("gui.string.cardslot"), 64, 20, 4210752);
		mc.fontRendererObj.drawString(I18n.translateToLocal("gui.string.cardwriter"), 60, 5, 4210752);
	}
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}