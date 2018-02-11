package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import pcl.opensecurity.tileentity.TileEntityDisplayPanel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Caitlyn
 *
 */
public class RenderDisplayPanel extends TileEntitySpecialRenderer {
	private static final float X_OFFSET = 0.5F;
	private static final float Y_OFFSET = 1F - 0.125F;
	private static final float Z_OFFSET = 0.01F;
	private static final int RESOLUTION = 60;
	private static final float INV_RESOLUTION = 1.0F/(float)RESOLUTION;
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		f *= Minecraft.getMinecraft().isGamePaused() ? 0.0f : 1.0f;
		TileEntityDisplayPanel panel = (TileEntityDisplayPanel) tileEntity;

		float light = tileEntity.getWorldObj().getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 15);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);

		RenderManager renderMan = RenderManager.instance;
		FontRenderer fontRenderer = renderMan.getFontRenderer();
		
		GL11.glPushMatrix();

		int dir = tileEntity.getBlockMetadata();

		GL11.glNormal3f(0, 1, 0);
		if (dir == 2) {
			GL11.glTranslatef((float)x + X_OFFSET, (float)y, (float)z - Z_OFFSET);
			GL11.glRotatef(0F, 0F, 1F, 0F);
		} else if (dir == 5) {
			GL11.glTranslatef((float)x + 1f + Z_OFFSET, (float)y, (float)z + X_OFFSET);
			GL11.glRotatef(270F, 0F, 1F, 0F);
		} else if (dir == 3) {
			GL11.glTranslatef((float)x + X_OFFSET, (float)y, (float)z + 1F + Z_OFFSET);
			GL11.glRotatef(180F, 0F, 1F, 0F);
		} else if (dir == 4) {
			GL11.glTranslatef((float)x - Z_OFFSET, (float)y, (float)z + X_OFFSET);
			GL11.glRotatef(90, 0F, 1F, 0F);
		}

		GL11.glColorMask(false, false, false, false);

		// Stencil the text onto the block face using Z buffer shenanigans
		{
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque(255, 0, 255);
			tessellator.addVertex(-0.5D,  1.0D, -0.01D);
			tessellator.addVertex(0.5D,  1.0D, -0.01D);
			tessellator.addVertex(0.5D, 0.0D, -0.01D);
			tessellator.addVertex(-0.5D, 0.0D, -0.01D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glColorMask(true, true, true, true);

		GL11.glTranslatef(0.5f, Y_OFFSET, 0.0f);
		GL11.glScalef(-INV_RESOLUTION, -INV_RESOLUTION, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//panel.scrollLines(f*(RESOLUTION/16.0f));

		// TODO: More lines
		int outputWidth = fontRenderer.getStringWidth(panel.getScreenText());
		float scroll = panel.getLineScroll()-(float)RESOLUTION;
		if (scroll > outputWidth+RESOLUTION) {
			scroll = -RESOLUTION;
			panel.setLineScroll(scroll);
		}
		GL11.glTranslatef(-scroll, 0.0f, 0.0f);
		fontRenderer.drawString(panel.getScreenText(), 0, 0, panel.getScreenColor());


		GL11.glTranslatef(-scroll, 12.0f, 0.0f);
		fontRenderer.drawString(panel.getScreenText(), 0, 0, panel.getScreenColor());
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}
	
}