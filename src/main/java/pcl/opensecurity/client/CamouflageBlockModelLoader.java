package pcl.opensecurity.client;

/* based on McJty's RFTools Shield */

/**
 * @author ben_mkiv
 */

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.CamoModel;
import pcl.opensecurity.client.models.CamouflageBakedModel;
import pcl.opensecurity.common.blocks.BlockCamouflage;

import java.util.HashMap;

public class CamouflageBlockModelLoader implements ICustomModelLoader {
    public static HashMap<String, CamoModel> camoModelBlocks = new HashMap<>();


    static {
        camoModelBlocks.put(BlockCamouflage.CAMO, new CamoModel());
    }

    public static void registerBlock(BlockCamouflage block){
        ModelResourceLocation modelLocationDefault = new ModelResourceLocation(block.getRegistryName().toString(), "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, modelLocationDefault);

        ModelResourceLocation modelLocation = new ModelResourceLocation(block.getRegistryName().toString());
        camoModelBlocks.put(modelLocation.getResourcePath(), new CamoModel());
        block.initModel(CamouflageBakedModel.modelFacade);
        //use 'invalid' metaindex to register another model for the same block
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 1, modelLocation);
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(OpenSecurity.MODID)) {
            return false;
        }
        if (modelLocation instanceof ModelResourceLocation && ((ModelResourceLocation)modelLocation).getVariant().equals("inventory")) {
            return false;
        }

        return camoModelBlocks.containsKey(modelLocation.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return camoModelBlocks.containsKey(modelLocation.getResourcePath()) ? camoModelBlocks.get(modelLocation.getResourcePath()) : null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}
}