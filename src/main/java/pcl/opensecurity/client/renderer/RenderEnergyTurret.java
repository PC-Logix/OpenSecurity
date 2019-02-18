package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.client.models.ModelEnergyTurret;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

public class RenderEnergyTurret extends TileEntitySpecialRenderer<TileEntityEnergyTurret> {
    private final ModelEnergyTurret model;
    private ResourceLocation textures;

    public RenderEnergyTurret() {
        super();
        this.model = new ModelEnergyTurret();
        this.textures = new ResourceLocation("opensecurity:textures/model/turret.png");
    }

    @Override
    public void render(TileEntityEnergyTurret te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.textures);
        GlStateManager.disableBlend();

        if (te != null && te.getWorld() != null){
            // render in world
            this.model.render(0.0625F, te.getEnergyTurret());
        } else {
            // probably render in some inventory
            GlStateManager.translate(-0.1, 0.1, 0); // align to slot
            this.model.render(0.0625F, null);
        }

        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

}