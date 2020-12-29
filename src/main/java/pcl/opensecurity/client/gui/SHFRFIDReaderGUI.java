package pcl.opensecurity.client.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.CardWriterContainer;
import pcl.opensecurity.common.inventory.SHRFIDReaderContainer;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntitySHFRFIDReader;

public class SHFRFIDReaderGUI extends ContainerGUI {
    public static final int WIDTH = 175;
    public static final int HEIGHT = 195;

    private static final ResourceLocation background = new ResourceLocation(OpenSecurity.MODID, "textures/gui/rfidwriter.png");

    public SHFRFIDReaderGUI(TileEntitySHFRFIDReader containerTileEntity, SHRFIDReaderContainer shrfidReaderContainer) {
        super(shrfidReaderContainer, WIDTH, HEIGHT);
    }

    @Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        drawCenteredString(new TextComponentTranslation("opensecurity.gui.string.cardslot").getUnformattedText(), 20, 4210752);
        drawCenteredString(new TextComponentTranslation("opensecurity.gui.string.cardwriter").getUnformattedText(), 5, 4210752);
	}

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}