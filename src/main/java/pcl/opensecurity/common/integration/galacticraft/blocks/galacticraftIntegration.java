package pcl.opensecurity.common.integration.galacticraft.blocks;

import pcl.opensecurity.OpenSecurity;

import static pcl.opensecurity.OpenSecurity.contentRegistry;

public class galacticraftIntegration {
    public static void preInit(){
        OpenSecurity.logger.info("NanoFog with Galacticraft support");
        contentRegistry.nanoFog = new BlockNanoFogGC();
    }
}
