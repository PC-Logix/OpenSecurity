package pcl.opensecurity.common.nanofog;

/* based on McJty's RFTools Shield */

/**
 * @author ben_mkiv
 */

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.blocks.BlockNanoFog;

public class BakedModelLoader implements ICustomModelLoader {
    public static final CamoModel MIMIC_MODEL = new CamoModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(OpenSecurity.MODID)) {
            return false;
        }
        if (modelLocation instanceof ModelResourceLocation && ((ModelResourceLocation)modelLocation).getVariant().equals("inventory")) {
            return false;
        }
        return BlockNanoFog.CAMO.equals(modelLocation.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return BlockNanoFog.CAMO.equals(modelLocation.getResourcePath()) ? MIMIC_MODEL : null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}
}