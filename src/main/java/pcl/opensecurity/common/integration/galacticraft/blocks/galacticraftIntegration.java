package pcl.opensecurity.common.integration.galacticraft.blocks;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockNanoFog;

public class galacticraftIntegration {
    public static void preInit(){
        OpenSecurity.logger.info("NanoFog with Galacticraft support");
        ContentRegistry.modBlocks.remove(BlockNanoFog.DEFAULTITEM);
        ContentRegistry.modBlocks.add(BlockNanoFog.DEFAULTITEM = new BlockNanoFogGC());
    }
}
