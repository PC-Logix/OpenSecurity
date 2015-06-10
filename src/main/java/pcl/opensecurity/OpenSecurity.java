package pcl.opensecurity;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;

import pcl.opensecurity.BuildInfo;
import pcl.opensecurity.blocks.Alarm;
import pcl.opensecurity.blocks.MagReader;
import pcl.opensecurity.blocks.RFIDReader;
import pcl.opensecurity.gui.SecurityGUIHandler;
import pcl.opensecurity.items.MagCard;
import pcl.opensecurity.items.RFIDCard;
import pcl.opensecurity.tileentity.AlarmTE;
import pcl.opensecurity.tileentity.MagReaderTE;
import pcl.opensecurity.tileentity.RFIDReaderTE;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=OpenSecurity.MODID, name="OpenSecurity", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "after:OpenComputers")
//@NetworkMod(clientSideRequired=true)
public class OpenSecurity {
	
	public static final String MODID = "opensecurity";
	
		public static Block magCardReader;
		public static Block rfidCardReader;
		public static Block Alarm;
		public static Item  magCard;
		public static Item  rfidCard;
		public static ItemBlock  securityitemBlock;
		
        @Instance(value = MODID)
        public static OpenSecurity instance;
        
        @SidedProxy(clientSide="pcl.opensecurity.ClientProxy", serverSide="pcl.opensecurity.CommonProxy")
        public static CommonProxy proxy;
        public static Config cfg = null;
        public static boolean render3D = true;
        
        private static boolean debug = true;
        public static org.apache.logging.log4j.Logger logger;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	
        	
        	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        	render3D = cfg.render3D;
        	/*
            if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD){
                try {
                    Class.forName("pcl.openprinter.mud.ModUpdateDetector")
                    		.getDeclaredMethod("registerMod", 
                    		ModContainer.class, URL.class, URL.class).invoke(null,
                            FMLCommonHandler.instance().findContainerFor(this),
                            new URL("http://PC-Logix.com/OpenSecurity/get_latest_build.php"),
                            new URL("http://PC-Logix.com/OpenSecurity/changelog.txt")
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            */
            logger = event.getModLog();
        	
        	
            NetworkRegistry.INSTANCE.registerGuiHandler(this, new SecurityGUIHandler());
        	GameRegistry.registerTileEntity(MagReaderTE.class, "MagCardTE");
        	GameRegistry.registerTileEntity(RFIDReaderTE.class, "RFIDTE");
        	GameRegistry.registerTileEntity(AlarmTE.class, "AlarmTE");
        	
        	//Register Blocks
        	magCardReader = new MagReader();
        	GameRegistry.registerBlock(magCardReader, "magreader");
        	magCardReader.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
        	
        	rfidCardReader = new RFIDReader();
        	GameRegistry.registerBlock(rfidCardReader, "rfidreader");
        	rfidCardReader.setCreativeTab(li.cil.oc.api.CreativeTab.instance);

        	Alarm = new Alarm();
        	GameRegistry.registerBlock(Alarm, "alarm");
        	Alarm.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
        	
        	
        	
        	//Register Items
        	magCard = new MagCard();
    		GameRegistry.registerItem(magCard, "opensecurity.magCard");
    		magCard.setUnlocalizedName("magCard");
    		magCard.setTextureName("minecraft:paper");
    		magCard.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
    		
        	rfidCard = new RFIDCard();
    		GameRegistry.registerItem(rfidCard, "opensecurity.rfidCard");
    		rfidCard.setUnlocalizedName("rfidCard");
    		rfidCard.setTextureName("opensecurity:rfidCard");
    		rfidCard.setCreativeTab(li.cil.oc.api.CreativeTab.instance);
        	
        }
        
        @EventHandler
    	public void load(FMLInitializationEvent event)
    	{
        	
    		proxy.registerRenderers();
    	}
}