package pcl.opensecurity;

import java.net.URL;
import java.util.List;

import pcl.opensecurity.BuildInfo;
import pcl.opensecurity.blocks.BlockAlarm;
import pcl.opensecurity.blocks.BlockMagReader;
import pcl.opensecurity.blocks.BlockRFIDReader;
import pcl.opensecurity.blocks.BlockCardWriter;
import pcl.opensecurity.client.CreativeTab;
import pcl.opensecurity.gui.OSGUIHandler;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
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

@Mod(modid = OpenSecurity.MODID, name = "OpenSecurity", version = BuildInfo.versionNumber
		+ "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers")
public class OpenSecurity {

	public static final String MODID = "opensecurity";

	public static Block magCardReader;
	public static Block rfidCardReader;
	public static Block rfidCardWriter;
	public static Block Alarm;
	public static Item magCard;
	public static Item rfidCard;
	public static ItemBlock securityitemBlock;

	@Instance(value = MODID)
	public static OpenSecurity instance;

	@SidedProxy(clientSide = "pcl.opensecurity.ClientProxy", serverSide = "pcl.opensecurity.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;
	public static boolean render3D = true;
	public static boolean debug = false;

	public static org.apache.logging.log4j.Logger logger;

	public static List<String> alarmList;

	private CreativeTabs CreativeTab;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		cfg = new Config(new Configuration(
				event.getSuggestedConfigurationFile()));
		render3D = cfg.render3D;
		alarmList = cfg.alarmsConfigList;

		if ((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD) {
			try {
				Class.forName("pcl.openprinter.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null, FMLCommonHandler.instance().findContainerFor(this),
								new URL("http://PC-Logix.com/OpenSecurity/get_latest_build.php"),
								new URL("http://PC-Logix.com/OpenSecurity/changelog.txt"));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	    CreativeTabs CreativeTab = new CreativeTab("OpenSecurity");
		logger = event.getModLog();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OSGUIHandler());
		
		
		


		// Register Blocks
		magCardReader = new BlockMagReader();
		GameRegistry.registerBlock(magCardReader, "magreader");
		magCardReader.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityMagReader.class, "MagCardTE");

		rfidCardReader = new BlockRFIDReader();
		GameRegistry.registerBlock(rfidCardReader, "rfidreader");
		rfidCardReader.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityRFIDReader.class, "RFIDTE");
		
		rfidCardWriter = new BlockCardWriter();
		GameRegistry.registerBlock(rfidCardWriter, "rfidwriter");
		rfidCardWriter.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityCardWriter.class, "RFIDWriterTE");

		Alarm = new BlockAlarm();
		GameRegistry.registerBlock(Alarm, "alarm");
		Alarm.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "AlarmTE");

		// Register Items
		magCard = new ItemMagCard();
		GameRegistry.registerItem(magCard, "opensecurity.magCard");
		magCard.setCreativeTab(CreativeTab);

		rfidCard = new ItemRFIDCard();
		GameRegistry.registerItem(rfidCard, "opensecurity.rfidCard");
		rfidCard.setCreativeTab(CreativeTab);
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {

		proxy.registerRenderers();
	}
}