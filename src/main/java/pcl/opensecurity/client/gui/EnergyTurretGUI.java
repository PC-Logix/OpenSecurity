package pcl.opensecurity.client.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.opengl.GL11;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.EnergyTurretContainer;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

public class EnergyTurretGUI  extends ContainerGUI {

	public EnergyTurretGUI(TileEntityEnergyTurret containerTileEntity, EnergyTurretContainer energyTurretContainer) {
		// the container is instanciated and passed to the superclass for
		// handling
		super(energyTurretContainer, 175, 195);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// the parameters for drawString are: string, x, y, color
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret").getUnformattedComponentText(), 55, 4, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.damagemod").getUnformattedComponentText(), 27, 18, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.damagemod").getUnformattedComponentText(), 27, 36, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.movementmod").getUnformattedComponentText(), 27, 54, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.movementmod").getUnformattedComponentText(), 27, 72, 4210752);
		
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.cooldownmod").getUnformattedComponentText(), 96, 18, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.cooldownmod").getUnformattedComponentText(), 96, 36, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.energymod").getUnformattedComponentText(), 96, 54, 4210752);
		mc.fontRenderer.drawString(new TextComponentTranslation("opensecurity.gui.string.energyturret.energymod").getUnformattedComponentText(), 96, 72, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		// draw your Gui here, only thing you need to change is the path
		ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/gui/energy_turret.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}