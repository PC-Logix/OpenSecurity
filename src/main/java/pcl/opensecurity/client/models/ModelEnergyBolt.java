package pcl.opensecurity.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelEnergyBolt extends ModelBase {
	private ModelRenderer bolt;

	public ModelEnergyBolt() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.bolt = new ModelRenderer(this, 23, 38);
		this.bolt.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bolt.addBox(-0.5F, -0.5F, -4.0F, 1, 1, 8, 0.0F);
	}

	public void render(float scale) {
		bolt.render(scale);
	}
}
