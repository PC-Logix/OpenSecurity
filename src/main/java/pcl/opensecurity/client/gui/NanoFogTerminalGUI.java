package pcl.opensecurity.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.NanoFogTerminalContainer;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;

public class NanoFogTerminalGUI extends ContainerGUI {
    private static final int WIDTH = 175;
    private static final int HEIGHT = 195;

    private TileEntityNanoFogTerminal te;

    private static final ResourceLocation background = new ResourceLocation(OpenSecurity.MODID, "textures/gui/nanofog_terminal.png");

    public NanoFogTerminalGUI(TileEntityNanoFogTerminal tileEntity, NanoFogTerminalContainer container) {
        super(container, WIDTH, HEIGHT);
        te = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        FontRenderer fr = mc.fontRenderer;

        drawCenteredString("NanoFog Terminal", 5, 4210752);

        drawCenteredString("NanoDNA", 40, 4210752);

        fr.drawString("input", 28, 60, 4210752);
        fr.drawString("output", 123, 60, 4210752);


        drawCenteredString("energy usage: "+(TileEntityNanoFogTerminal.FogUpkeepCost * te.getFogBlocks().size())+" units/tick", 95, 4210752);
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
