package pcl.opensecurity.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.ModelCubeTexturedTESR;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class RenderRolldoorController extends TileEntitySpecialRenderer<TileEntityRolldoorController> {
    static ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/blocks/rolldoor.png");
    double height;
    ModelCubeTexturedTESR model = new ModelCubeTexturedTESR(0, 0, 0, 0, 0, 0);

    @Override
    public void render(TileEntityRolldoorController tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        Vec3d renderPosition = tileEntity.getElementsRenderBoundingBox().getCenter();
        float width = tileEntity.getWidth()/2f;

        if(width == 0)
            return;

        height = tileEntity.getCurrentHeight();
        model.setP1(-width, 0, 1f/16 * 6);
        model.setP2(width, (float) height, 1f - 1f/16 * 6);

        GlStateManager.enableTexture2D();
        bindTexture(texture);

        GlStateManager.disableLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPosition.x + x, y, renderPosition.z + z);


        switch(tileEntity.facing()) {
            case EAST:
            case WEST:
                GlStateManager.rotate(90, 0, 1, 0);
        }

        GlStateManager.translate(0, 0, -0.5);
        GlStateManager.scale(1, -1, 1);

        model.drawCube();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityRolldoorController te){
        return true;
    }
}
