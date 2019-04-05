package pcl.opensecurity.common.integration.galacticraft.blocks;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockNanoFog;

public class galacticraftIntegration {
    public static void preInit(){
        OpenSecurity.logger.info("NanoFog with Galacticraft support");
        ContentRegistry.modCamoBlocks.remove(BlockNanoFog.DEFAULTITEM);
        ContentRegistry.modCamoBlocks.add(BlockNanoFog.DEFAULTITEM = new BlockNanoFogGC());
    }
}
