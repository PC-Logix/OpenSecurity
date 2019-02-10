package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.ModelCube;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class RenderRolldoorController extends TileEntitySpecialRenderer<TileEntityRolldoorController> {

    @Override
    public void render(TileEntityRolldoorController tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){

        double height = tileEntity.getCurrentHeight();

        ModelCube model = new ModelCube(0, 0, 1f/16 * 6, tileEntity.getWidth(), (float) height, 1f - 1f/16 * 6);

        ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/blocks/rolldoor.png");


        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.color(1f, 1f, 1f, 1);
        GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

        GlStateManager.translate(0.5, 0.5, 0.5);
		GlStateManager.rotate(tileEntity.facing().getHorizontalAngle(), 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        GlStateManager.translate(-tileEntity.getWidth(), 0, 0);
        GlStateManager.scale(1, -1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        model.drawCube();

		GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
    }
}
