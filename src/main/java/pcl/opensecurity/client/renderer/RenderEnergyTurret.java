package pcl.opensecurity.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.client.models.ModelEnergyTurret;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

public class RenderEnergyTurret extends TileEntitySpecialRenderer<TileEntityEnergyTurret> {
	private final ModelEnergyTurret model;
	ResourceLocation textures;

	public RenderEnergyTurret() {
		super();
		this.model = new ModelEnergyTurret();
		this.textures = new ResourceLocation("opensecurity:turret.png");
	}

	public void renderTileEntityAt(TileEntityEnergyTurret te, double x, double y, double z, float partialTicks, int destroyStage) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		Minecraft.getMinecraft().renderEngine.bindTexture(this.textures);
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		if(te!=null && te instanceof TileEntityEnergyTurret)
		{
			TileEntityEnergyTurret et = te;
			this.model.render(0.0625F, et.isUpright(), et.shaft, et.barrel, et.getRealYaw(), et.getRealPitch());
		}
		else
			this.model.render(0.0625F, true, 1.0F, 1.0F, 0F, 0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}