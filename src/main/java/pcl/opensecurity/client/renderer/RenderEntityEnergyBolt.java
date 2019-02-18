package pcl.opensecurity.client.renderer;

import net.minecraft.client.renderer.GlStateManager;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.client.models.ModelEnergyBolt;
import pcl.opensecurity.common.entity.EntityEnergyBolt;

public class RenderEntityEnergyBolt extends Render<EntityEnergyBolt> {
	private static final ResourceLocation textures = new ResourceLocation("opensecurity:textures/model/turret.png");
	private ModelEnergyBolt model;
	
	public RenderEntityEnergyBolt(RenderManager renderManager) {
		super(renderManager);
		this.model = new ModelEnergyBolt();
	}
	
	@Override
	public void doRender(EntityEnergyBolt entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		bindEntityTexture(entity);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(-entity.rotationPitch, 1, 0, 0);
		model.render(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEnergyBolt entity) {
		return textures;
	}
}