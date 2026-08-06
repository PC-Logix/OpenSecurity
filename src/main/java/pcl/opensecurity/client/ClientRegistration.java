package pcl.opensecurity.client;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.data.CardData;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@EventBusSubscriber(modid = OpenSecurity.MOD_ID, value = Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? CardData.read(stack).color() : 0xFFFFFFFF,
                OpenSecurity.RFID_CARD.get(), OpenSecurity.MAG_CARD.get());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnergyTurretRenderer.LAYER, EnergyTurretModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OpenSecurity.ENERGY_BOLT.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
        event.registerBlockEntityRenderer(OpenSecurity.ENERGY_TURRET_BE.get(), EnergyTurretRenderer::new);
    }

    private ClientRegistration() {}
}
