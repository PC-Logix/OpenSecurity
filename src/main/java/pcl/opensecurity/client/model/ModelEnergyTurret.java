package pcl.opensecurity.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEnergyTurret
  extends ModelBase
{
  public ModelRenderer base;
  public ModelRenderer rotorBase;
  public ModelRenderer rotorLeft;
  public ModelRenderer rotorRight;
  public ModelRenderer gunBase;
  public ModelRenderer gunBarrell;
  public ModelRenderer gunEnd;
  
  public ModelEnergyTurret()
  {
    this.textureWidth = 64;
    this.textureHeight = 64;
    this.base = new ModelRenderer(this, 0, 0);
    this.base.setRotationPoint(-8.0F, 18.0F, -8.0F);
    this.base.addBox(0.0F, 0.0F, 0.0F, 16, 6, 16, 0.0F);
    this.rotorBase = new ModelRenderer(this, 0, 23);
    this.rotorBase.setRotationPoint(0.0F, 18.0F, 0.0F);
    this.rotorBase.addBox(-4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F);
    this.gunBase = new ModelRenderer(this, 40, 24);
    this.gunBase.setRotationPoint(0.0F, 10.5F, 0.0F);
    this.gunBase.addBox(-1.0F, -1.75F, -2.0F, 2, 4, 4, 0.0F);
    this.gunBarrell = new ModelRenderer(this, 0, 32);
    this.gunBarrell.setRotationPoint(0.0F, 10.5F, 0.0F);
    this.gunBarrell.addBox(-2.0F, -5.75F, -10.0F, 4, 4, 15, 0.0F);
    this.gunEnd = new ModelRenderer(this, 38, 41);
    this.gunEnd.setRotationPoint(0.0F, 10.5F, 0.0F);
    this.gunEnd.addBox(-1.0F, -4.85F, -18.0F, 2, 2, 8, 0.0F);
    this.rotorLeft = new ModelRenderer(this, 32, 22);
    this.rotorLeft.setRotationPoint(0.0F, 18.0F, 0.0F);
    this.rotorLeft.addBox(-2.0F, -8.0F, -1.5F, 1, 7, 3, 0.0F);
    this.rotorRight = new ModelRenderer(this, 32, 22);
    this.rotorRight.setRotationPoint(0.0F, 18.0F, 0.0F);
    this.rotorRight.addBox(1.0F, -8.0F, -1.5F, 1, 7, 3, 0.0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    this.base.render(f5);
    this.rotorBase.render(f5);
    this.gunBase.render(f5);
    this.gunBarrell.render(f5);
    this.gunEnd.render(f5);
    this.rotorLeft.render(f5);
    this.rotorRight.render(f5);
  }
  
  public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
  {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
  
  public void render(Entity entity, float f, float g, float h, float i, float j, float k, float yaw, float pitch)
  {
    setRotateAngle(this.rotorLeft, 0.0F, yaw, 0.0F);
    setRotateAngle(this.rotorRight, 0.0F, yaw, 0.0F);
    setRotateAngle(this.rotorBase, 0.0F, yaw, 0.0F);
    setRotateAngle(this.gunEnd, pitch, yaw, 0.0F);
    setRotateAngle(this.gunBase, pitch, yaw, 0.0F);
    setRotateAngle(this.gunBarrell, pitch, yaw, 0.0F);
    render(entity, f, g, h, i, j, k);
  }
}
