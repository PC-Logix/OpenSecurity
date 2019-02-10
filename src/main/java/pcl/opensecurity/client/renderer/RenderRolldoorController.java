package pcl.opensecurity.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.ModelCubeTexturedTESR;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class RenderRolldoorController extends TileEntitySpecialRenderer<TileEntityRolldoorController> {
    static ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/blocks/rolldoor.png");
    double height;
    ModelCubeTexturedTESR model = new ModelCubeTexturedTESR(0, 0, 0, 0, 0, 0);

    @Override
    public void render(TileEntityRolldoorController tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        height = tileEntity.getCurrentHeight();
        model.setP1(0, 0, 1f/16 * 6);
        model.setP2(tileEntity.getWidth(), (float) height, 1f - 1f/16 * 6);

        GlStateManager.enableTexture2D();
        bindTexture(texture);

        GlStateManager.disableLighting();

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		GlStateManager.rotate(tileEntity.facing().getHorizontalAngle(), 0, 1, 0);
        GlStateManager.translate(-0.5-tileEntity.getWidth(), -0.5, -0.5);

        GlStateManager.scale(1, -1, 1);

        model.drawCube();

        GlStateManager.popMatrix();
    }
}
