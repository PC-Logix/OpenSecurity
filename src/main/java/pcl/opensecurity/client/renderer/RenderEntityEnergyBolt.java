package pcl.opensecurity.client.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.client.model.ModelEnergyBolt;
import pcl.opensecurity.entity.EntityEnergyBolt;

public class RenderEntityEnergyBolt
  extends Render
{
  private static final ResourceLocation textures = new ResourceLocation("opensecurity:turret.png");
  private final ModelEnergyBolt model;
  
  public RenderEntityEnergyBolt()
  {
    this.model = new ModelEnergyBolt();
  }
  
  public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_)
  {
    bindEntityTexture(entity);
    GL11.glPushMatrix();
    GL11.glTranslatef((float)x + 0.0F, (float)y + 0.0F, (float)z + 0.0F);
    this.model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, ((EntityEnergyBolt)entity).getYaw(), -((EntityEnergyBolt)entity).getPitch());
    GL11.glPopMatrix();
  }
  
  protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
    return textures;
  }
}