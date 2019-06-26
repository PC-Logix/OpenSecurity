package pcl.opensecurity.manual;

import li.cil.oc.api.Manual;
import li.cil.oc.api.manual.PathProvider;
import li.cil.oc.api.prefab.TextureTabIconRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

class ManualPathProviderOC extends ManualPathProvider implements PathProvider {
    void initialize(ResourceLocation iconResourceLocation, String tooltip, String path) {
        if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT)) {
            Manual.addProvider(new ManualPathProviderOC());
            Manual.addProvider(new ManualContentProviderOC());
            Manual.addTab(new TextureTabIconRenderer(iconResourceLocation), tooltip, path);
        }
    }
}
