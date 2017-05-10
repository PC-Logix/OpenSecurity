package pcl.opensecurity.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;

public class ClientProxy extends CommonProxy {
	public static File alarmSounds;
	public static List<String> alarmList = new ArrayList<String>();

	
	public void registerRenderers() {
		//TileEntitySpecialRenderer<TileEntityRadio> radioRenderer = new RadioRenderer();
		//ClientRegistry.bindTileEntitySpecialRenderer(pcl.OpenFM.TileEntity.TileEntityRadio.class, radioRenderer);
		//OpenFM.logger.info("Registering TESR");		
	}

	@Override
	public void registerItemRenderers() {
		registerBlockItem(ContentRegistry.alarmBlock, 0, "Alarm");
		registerBlockItem(ContentRegistry.biometricReaderBlock, 0, "biometric_reader");
		registerBlockItem(ContentRegistry.dataBlock, 0, "data_block");
		registerItem(ContentRegistry.itemRFIDCard, "RFIDCard");
		//registerItem(ContentRegistry.itemRadioTuner, "RadioTuner");
	}
	
	public static void registerBlockItem(final Block block, int meta, final String blockName) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(OpenSecurity.MODID + ":" + blockName, "inventory"));
		OpenSecurity.logger.info("Registering " + blockName + " Item Renderer");
    }
	
	public static void registerItem(final Item item, final String itemName)  {
		ModelLoader.setCustomModelResourceLocation(item,  0, new ModelResourceLocation(OpenSecurity.MODID + ":" + itemName, "inventory"));
		OpenSecurity.logger.info("Registering " + itemName + " Item Renderer");
    }
	
	
	public void listFilesForFolder(final File folder) {
		AlarmResource r = new AlarmResource();
		int i = 1;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	r.addSoundReferenceMapping(i, fileEntry.getName()); //add map soundlocation -> recordX
	        	i++;
	        }
	    }
	    
	    r.registerAsResourceLocation(); //finalise IResourcePack
	}
	
    @Override
    public void registerSounds () {
		File[] listOfFiles;
		File alarmSounds = new File("./mods/OpenSecurity/assets/opensecurity/sounds/alarms");
		if (alarmSounds.exists()) {
			listOfFiles = alarmSounds.listFiles();
			
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
				alarmList.add(listOfFiles[i].getName());
		      }
		    }
		}
        listFilesForFolder(alarmSounds);
    }

}