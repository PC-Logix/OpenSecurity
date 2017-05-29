package pcl.opensecurity.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelEnergyTurret extends ModelBase {
	  public ModelRenderer base;
	  public ModelRenderer rotorBase;
	  public ModelRenderer rotorShaft1;
	  public ModelRenderer rotorShaft2;
	  public ModelRenderer gunBase;
	  public ModelRenderer gunBarrell;
	  public ModelRenderer gunEnd;

	  public ModelEnergyTurret()
	  {
	    this.textureWidth = 64;
	    this.textureHeight = 64;
	    this.base = new ModelRenderer(this, 0, 0);
	    this.base.setRotationPoint(8.0F, 8.0F, 8.0F);
	    this.base.addBox(-8.0F, -8.0F, -8.0F, 16, 6, 16, 0.0F);

	    this.rotorBase = new ModelRenderer(this, 0, 23);
	    this.rotorBase.setRotationPoint(0.0F, 0.0F, 0.0F);
	    this.rotorBase.addBox(-4.0F, -2.0F, -4.0F, 8, 1, 8, 0.0F);
	    this.base.addChild(this.rotorBase);
	    
	    this.rotorShaft1 = new ModelRenderer(this, 32, 22);
	    this.rotorShaft1.addBox(-3.0F, -1.0F, -1.5F, 1, 7, 3, 0.0F);
	    this.rotorShaft1.addBox(2.0F, -1.0F, -1.5F, 1, 7, 3, 0.0F);
	    this.rotorBase.addChild(this.rotorShaft1);
	    
	    this.rotorShaft2 = new ModelRenderer(this, 32, 22);
	    this.rotorShaft2.addBox(-2.0F, -1.0F, -1.5F, 1, 7, 3, 0.0F);
	    this.rotorShaft2.addBox(1.0F, -1.0F, -1.5F, 1, 7, 3, 0.0F);
	    this.rotorShaft1.addChild(this.rotorShaft2);
	    
	    this.gunBase = new ModelRenderer(this, 40, 24);
	    this.gunBase.setRotationPoint(0.0F, 5.5F, 0.0F);
	    this.gunBase.addBox(-1.0F, -2.0F, -2.0F, 2, 4, 4, 0.0F);
	    this.rotorShaft2.addChild(this.gunBase);
	    
	    this.gunBarrell = new ModelRenderer(this, 0, 32);
	    this.gunBarrell.addBox(-2.0F, -2.0F, -7.5F, 4, 4, 15, 0.0F);
	    this.gunBase.addChild(this.gunBarrell);
	    
	    this.gunEnd = new ModelRenderer(this, 38, 41);
	    this.gunEnd.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 8, 0.0F);
	    this.gunBarrell.addChild(this.gunEnd);
	  }
	  
	  public void render(float k, boolean upright, float shaft, float barrel, float yaw, float pitch)
	  {
	    if(upright)
	    {
	      this.base.rotateAngleZ = 0;
	    }
	    else
	    {
	      this.base.rotateAngleZ = (float)Math.PI;
	      yaw*=-1;
	      pitch*=-1;
	    }
	    
	    this.rotorBase.rotateAngleY = yaw;
	    this.gunBase.rotateAngleX = pitch;

	    float dy1 = Math.max(0f, Math.min(6F, shaft*6F));
	    float dy2 = Math.max(0f, Math.min(6F, shaft*6F-6f));
	    this.rotorShaft1.setRotationPoint(0.0F, dy1-6F, 0.0F);
	    this.rotorShaft2.setRotationPoint(0.0F, dy2, 0.0F);
	    this.gunBarrell.setRotationPoint(0.0F, 2.5F, 3.0F*barrel);
	    this.gunEnd.setRotationPoint(0.0F, 0.0F, -10.0F*barrel);
	    this.base.render(k);
	  }
	}