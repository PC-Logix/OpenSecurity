package pcl.opensecurity.manual;

import li.cil.oc.api.Manual;
import li.cil.oc.api.manual.PathProvider;
import li.cil.oc.api.prefab.TextureTabIconRenderer;
import net.minecraft.util.ResourceLocation;

public class ManualPathProviderOC extends ManualPathProvider implements PathProvider {
    public void initialize(ResourceLocation iconResourceLocation, String tooltip, String path) {
        Manual.addProvider(new ManualPathProviderOC());
        Manual.addProvider(new ManualContentProviderOC());
        Manual.addTab(new TextureTabIconRenderer(iconResourceLocation), tooltip, path);
    }
}
