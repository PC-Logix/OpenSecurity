package pcl.opensecurity.client.models;
/**
 * @author ben_mkiv
 */
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;


@SideOnly(Side.CLIENT)
public abstract class ModelNanoFogSwarm extends ModelBase {
    public static int resolution = 8;
    static double resolutionOffset = 1d/resolution/2d;


    public static void setupResolution(int res){
        resolution = res;
        resolutionOffset = 1d/resolution/2d;
    }

    static float interpolate(float current, float max){
        return 1f/max * Math.min(current, max);
    }

    @SideOnly(Side.CLIENT)
    public void render(EntityNanoFogSwarm entity, float partialTicks, float scale) {

        float cubeScaleFactor = getCubeScaleFactor(entity);
        float cubeOffsetFactor = getCubeOffsetFactor(entity);

        scale*=getEntityScaleFactor(entity);

        GlStateManager.translate(resolutionOffset, resolutionOffset + 0.5, resolutionOffset);
        GlStateManager.color(0, 0, 0, getAlpha(entity));
        GlStateManager.scale(scale, scale, scale);

        for(EntityNanoFogSwarm.Cube cube : entity.cubes)
            cube.render(partialTicks, cubeScaleFactor, cubeOffsetFactor);
    }

    abstract float getCubeScaleFactor(EntityNanoFogSwarm entity);
    abstract float getAlpha(EntityNanoFogSwarm entity);
    abstract float getEntityScaleFactor(EntityNanoFogSwarm entity);

    abstract float getCubeOffsetFactor(EntityNanoFogSwarm entity);

    public static void drawCube(float scale){
        scale/=2f;

        ModelCube.drawCube(-scale, -scale, -scale, scale, scale, scale);

        /*
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(scale,scale,-scale);   // Top Right Of The Quad (Top)
        GlStateManager.glVertex3f(-scale,scale,-scale);  // Top Left Of The Quad (Top)
        GlStateManager.glVertex3f(-scale,scale,scale);   // Bottom Left Of The Quad (Top)
        GlStateManager.glVertex3f(scale,scale,scale);    // Bottom Right Of The Quad (Top)

        GlStateManager.glVertex3f(scale,-scale,scale);   // Top Right Of The Quad (Bottom)
        GlStateManager.glVertex3f(-scale,-scale,scale);  // Top Left Of The Quad (Bottom)
        GlStateManager.glVertex3f(-scale,-scale,-scale); // Bottom Left Of The Quad (Bottom)
        GlStateManager.glVertex3f(scale,-scale,-scale);  // Bottom Right Of The Quad (Bottom)

        GlStateManager.glVertex3f(scale,scale,scale);    // Top Right Of The Quad (Front)
        GlStateManager.glVertex3f(-scale,scale,scale);   // Top Left Of The Quad (Front)
        GlStateManager.glVertex3f(-scale,-scale,scale);  // Bottom Left Of The Quad (Front)
        GlStateManager.glVertex3f(scale,-scale,scale);   // Bottom Right Of The Quad (Front)

        GlStateManager.glVertex3f(scale,-scale,-scale);  // Top Right Of The Quad (Back)
        GlStateManager.glVertex3f(-scale,-scale,-scale); // Top Left Of The Quad (Back)
        GlStateManager.glVertex3f(-scale,scale,-scale);  // Bottom Left Of The Quad (Back)
        GlStateManager.glVertex3f(scale,scale,-scale);   // Bottom Right Of The Quad (Back)

        GlStateManager.glVertex3f(-scale,scale,scale);   // Top Right Of The Quad (Left)
        GlStateManager.glVertex3f(-scale,scale,-scale);  // Top Left Of The Quad (Left)
        GlStateManager.glVertex3f(-scale,-scale,-scale); // Bottom Left Of The Quad (Left)
        GlStateManager.glVertex3f(-scale,-scale,scale);  // Bottom Right Of The Quad (Left)

        GlStateManager.glVertex3f(scale,scale,-scale);   // Top Right Of The Quad (Right)
        GlStateManager.glVertex3f(scale,scale,scale);    // Top Left Of The Quad (Right)
        GlStateManager.glVertex3f(scale,-scale,scale);   // Bottom Left Of The Quad (Right)
        GlStateManager.glVertex3f(scale,-scale,-scale);   // Bottom Right Of The Quad (Right)
        GlStateManager.glEnd();*/
    }

}
