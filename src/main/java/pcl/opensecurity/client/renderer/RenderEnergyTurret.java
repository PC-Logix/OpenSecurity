package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.client.model.ModelEnergyTurret;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;

public class RenderEnergyTurret
  extends TileEntitySpecialRenderer
{
  private final ModelEnergyTurret model;
  ResourceLocation textures;
  
  public RenderEnergyTurret()
  {
    this.model = new ModelEnergyTurret();
    this.textures = new ResourceLocation("opensecurity:turret.png");
  }
  
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale)
  {
    GL11.glPushMatrix();
    GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
    Minecraft.getMinecraft().renderEngine.bindTexture(this.textures);
    GL11.glPushMatrix();
    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
    this.model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, ((TileEntityEnergyTurret)te).getRealYaw(), ((TileEntityEnergyTurret)te).getRealPitch());
    GL11.glPopMatrix();
    GL11.glPopMatrix();
  }
}