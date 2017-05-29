package pcl.opensecurity.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEnergyBolt extends ModelBase
{
	  public ModelRenderer bolt;
	  
	  public ModelEnergyBolt()
	  {
	    this.textureWidth = 64;
	    this.textureHeight = 64;
	    this.bolt = new ModelRenderer(this, 23, 38);
	    this.bolt.setRotationPoint(0.0F, 0.0F, 0.0F);
	    this.bolt.addBox(-0.5F, -0.5F, -4.0F, 1, 1, 8, 0.0F);
	  }
	  
	  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	  {
	    render(entity, f, f1, f2, f3, f4, f5, 0.0F, 0.0F);
	  }
	  
	  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, float y, float p)
	  {
	    setRotateAngle(this.bolt, p, y, 0.0F);
	    this.bolt.render(f5);
	  }
	  
	  public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	  {
	    modelRenderer.rotateAngleX = x;
	    modelRenderer.rotateAngleY = y;
	    modelRenderer.rotateAngleZ = z;
	  }
	}
