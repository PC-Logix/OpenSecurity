package pcl.opensecurity.client;

import li.cil.oc.api.Manual;
import li.cil.oc.api.prefab.TextureTabIconRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import pcl.opensecurity.OpenSecurity;

@EventBusSubscriber(modid = OpenSecurity.MOD_ID, value = Dist.CLIENT)
public final class ManualIntegration {
    @SubscribeEvent
    public static void registerManual(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Manual.addProvider(new WikiContentProvider());
            Manual.addTab(
                    new TextureTabIconRenderer(OpenSecurity.id("textures/item/manual.png")),
                    "OpenSecurity",
                    "opensecurity/_Sidebar");
        });
    }

    private ManualIntegration() {}
}
