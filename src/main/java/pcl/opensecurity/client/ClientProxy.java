package pcl.opensecurity.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;
import pcl.opensecurity.common.items.ItemCard;

public class ClientProxy extends CommonProxy {
	public static File alarmSounds;
	public static List<String> alarmList = new ArrayList<String>();

	@Override
	public void init(){
		super.init();
		Minecraft mc = Minecraft.getMinecraft();
		mc.getItemColors().registerItemColorHandler( new CardColorHandler( ContentRegistry.itemRFIDCard ), ContentRegistry.itemRFIDCard );
		mc.getItemColors().registerItemColorHandler( new CardColorHandler( ContentRegistry.itemMagCard ), ContentRegistry.itemMagCard );
	}
	
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
		registerBlockItem(ContentRegistry.cardWriter, 0, "card_writer");
		registerItem(ContentRegistry.itemRFIDCard, "RFIDCard");
		registerItem(ContentRegistry.itemMagCard, "MagCard");
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
    

	@SideOnly(Side.CLIENT)
	private static class CardColorHandler implements IItemColor
	{
		private final ItemCard card;

		private CardColorHandler(ItemCard card)
		{
			this.card = card;
		}

		@Override
		public int getColorFromItemstack(ItemStack stack, int layer)
		{
			return layer == 0 ? 0xFFFFFF : card.getColor(stack);
		}
	}

}