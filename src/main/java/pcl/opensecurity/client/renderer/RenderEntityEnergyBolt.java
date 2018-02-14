package pcl.opensecurity.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.client.models.ModelEnergyBolt;
import pcl.opensecurity.common.entity.EntityEnergyBolt;

public class RenderEntityEnergyBolt extends Render<EntityEnergyBolt> {
	private static final ResourceLocation textures = new ResourceLocation("opensecurity:turret.png");
	private ModelEnergyBolt model;
	
	public RenderEntityEnergyBolt(RenderManager renderManager) {
		super(renderManager);
		this.model = new ModelEnergyBolt();
	}
	
	@Override
	public void doRender(EntityEnergyBolt entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		bindEntityTexture(entity);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.0F, (float)y + 0.0F, (float)z + 0.0F);
		this.model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, entity.getYaw(), -entity.getPitch());
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEnergyBolt entity) {
		// TODO Auto-generated method stub
		return textures;
	}
}