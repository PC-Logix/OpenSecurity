package pcl.opensecurity.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.NanoFogTerminalContainer;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;

public class NanoFogTerminalGUI extends GuiContainer {
    public static final int WIDTH = 175;
    public static final int HEIGHT = 195;

    TileEntityNanoFogTerminal te;

    private static final ResourceLocation background = new ResourceLocation(OpenSecurity.MODID, "textures/gui/nanofog_terminal.png");

    public NanoFogTerminalGUI(TileEntityNanoFogTerminal tileEntity, NanoFogTerminalContainer container) {
        super(container);
        te = tileEntity;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    void drawCenteredString(String string, int y, int color){
        FontRenderer fr = mc.fontRenderer;
        fr.drawString(string, getXSize()/2 - fr.getStringWidth(string)/2, y, color);
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
