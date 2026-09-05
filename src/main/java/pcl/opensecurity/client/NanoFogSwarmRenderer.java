package pcl.opensecurity.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import pcl.opensecurity.entity.NanoFogSwarmEntity;

/** The swarm is represented entirely by particles emitted by its entity tick. */
public final class NanoFogSwarmRenderer extends EntityRenderer<NanoFogSwarmEntity> {
    public NanoFogSwarmRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override
    public ResourceLocation getTextureLocation(NanoFogSwarmEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
