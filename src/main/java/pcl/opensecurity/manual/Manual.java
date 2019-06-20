package pcl.opensecurity.manual;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

import java.util.HashSet;

public class Manual {
    private static ResourceLocation iconResourceLocation = new ResourceLocation(OpenSecurity.MODID, "textures/blocks/security_terminal.png");
    private static String tooltip = "OpenSecurity";
    private static String homepage = "assets/" + OpenSecurity.MODID + "/doc/_Sidebar";

    public static HashSet<Item> items = new HashSet<>();


    public static void preInit(){
        if(Loader.isModLoaded("rtfm")) {
            new ManualPathProviderRTFM().initialize(iconResourceLocation, tooltip, homepage);
            items.add(ManualPathProviderRTFM.getManualItem().setUnlocalizedName("manual").setRegistryName("manual").setCreativeTab(ContentRegistry.creativeTab));
        }

        if(Loader.isModLoaded("opencomputers"))
            new ManualPathProviderOC().initialize(iconResourceLocation, tooltip, homepage);
    }

}
