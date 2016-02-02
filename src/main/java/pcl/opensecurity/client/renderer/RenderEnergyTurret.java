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
    GL11.glTranslatef((float)x, (float)y, (float)z);
    Minecraft.getMinecraft().renderEngine.bindTexture(this.textures);
    GL11.glPushMatrix();
    if(te!=null && te instanceof TileEntityEnergyTurret)
    {
      TileEntityEnergyTurret et = (TileEntityEnergyTurret)te;
      this.model.render(0.0625F, et.isUpright(), et.shaft, et.barrel, et.getRealYaw(), et.getRealPitch());
    }
    else
      this.model.render(0.0625F, true, 1.0F, 1.0F, 0F, 0F);
    GL11.glPopMatrix();
    GL11.glPopMatrix();
  }
}