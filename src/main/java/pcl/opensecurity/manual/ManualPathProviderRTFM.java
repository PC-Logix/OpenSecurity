package pcl.opensecurity.manual;

import li.cil.manual.api.ManualAPI;
import li.cil.manual.api.detail.ManualDefinition;
import li.cil.manual.api.manual.PathProvider;
import li.cil.manual.api.prefab.manual.TextureTabIconRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;

public class ManualPathProviderRTFM extends ManualPathProvider implements PathProvider {
    static ManualDefinition manual;

    public void initialize(ResourceLocation iconResourceLocation, String tooltip, String path) {
        manual = ManualAPI.createManual(false);

        manual.addProvider(new ManualPathProviderRTFM());
        manual.addProvider(new ManualContentProviderRTFM());
        manual.setDefaultPage(path);

        manual.addTab(new TextureTabIconRenderer(iconResourceLocation), tooltip, path);
        manual.addTab(new TextureTabIconRenderer(new ResourceLocation(OpenSecurity.MODID, "textures/blocks/door_controller.png")), "Blocks", "assets/" + OpenSecurity.MODID + "/doc/Blocks");
        manual.addTab(new TextureTabIconRenderer(new ResourceLocation(OpenSecurity.MODID, "textures/items/mag_card.png")), "Items", "assets/" + OpenSecurity.MODID + "/doc/Items");
    }

    public static Item getManualItem(){
        return ManualAPI.createItemForManual(manual);
    }

}
