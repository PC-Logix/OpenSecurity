package pcl.opensecurity.client.renderer;
/**
 * @author ben_mkiv
 */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.ModelNanoFogSwarmBuild;
import pcl.opensecurity.client.models.ModelNanoFogSwarmReturn;
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@SideOnly(Side.CLIENT)
public class NanoFogSwarmRenderer extends Render<EntityNanoFogSwarm> {
    public static Factory FACTORY = new Factory();

    ModelNanoFogSwarmBuild modelBuild;
    ModelNanoFogSwarmReturn modelReturn;

    private static final ResourceLocation textureLoc = new ResourceLocation(OpenSecurity.MODID + ":textures/model/nanofogswarm.png");

    public NanoFogSwarmRenderer(RenderManager manager){
        super(manager);
        modelBuild = new ModelNanoFogSwarmBuild();
        modelReturn = new ModelNanoFogSwarmReturn();
    }

    @Override
    public void doRender(EntityNanoFogSwarm entity, double x, double y, double z, float entityYaw, float partialTicks){
        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if(entity.isBuildTask())
            modelBuild.render(entity, partialTicks, 1);
        else if(entity.isReturnTask())
            modelReturn.render(entity, partialTicks, 1);

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityNanoFogSwarm par1EntityLiving) {
        return textureLoc;
    }

    public static class Factory implements IRenderFactory<EntityNanoFogSwarm> {
        @Override
        public Render<? super EntityNanoFogSwarm> createRenderFor(RenderManager manager) {
            return new NanoFogSwarmRenderer(manager);
        }
    }

}





