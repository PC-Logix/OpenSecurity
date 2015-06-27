package pcl.opensecurity;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import li.cil.oc.api.fs.FileSystem;
import pcl.opensecurity.BuildInfo;
import pcl.opensecurity.blocks.BlockAlarm;
import pcl.opensecurity.blocks.BlockEntityDetector;
import pcl.opensecurity.blocks.BlockMagReader;
import pcl.opensecurity.blocks.BlockRFIDReader;
import pcl.opensecurity.blocks.BlockCardWriter;
import pcl.opensecurity.client.CreativeTab;
import pcl.opensecurity.drivers.RFIDReaderCardDriver;
import pcl.opensecurity.gui.OSGUIHandler;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;
import pcl.opensecurity.items.ItemRFIDReaderCard;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import pcl.opensecurity.tileentity.TileEntityEntityDetector;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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

@Mod(modid = OpenSecurity.MODID, name = "OpenSecurity", version = BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers")
public class OpenSecurity {

	public static final String MODID = "opensecurity";

	public static Block magCardReader;
	public static Block rfidCardReader;
	public static Block cardWriter;
	public static Block Alarm;
	public static Block EntityDetector;
	public static Item magCard;
	public static Item rfidCard;
	public static Item rfidReaderCard;
	public static ItemBlock securityitemBlock;
	public static ItemStack secureOS_disk;

	@Instance(value = MODID)
	public static OpenSecurity instance;

	@SidedProxy(clientSide = "pcl.opensecurity.ClientProxy", serverSide = "pcl.opensecurity.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;
	//public static boolean render3D = true;
	public static boolean debug = false;
	public static int rfidRange;
	public static boolean enableplaySoundAt = false;

	public static org.apache.logging.log4j.Logger logger;

	public static List<String> alarmList;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		cfg = new Config(new Configuration(
				event.getSuggestedConfigurationFile()));
		//render3D = cfg.render3D;
		alarmList = cfg.alarmsConfigList;
		rfidRange = cfg.rfidMaxRange;
		enableplaySoundAt = cfg.enableplaySoundAt;

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
		
		cardWriter = new BlockCardWriter();
		GameRegistry.registerBlock(cardWriter, "rfidwriter");
		cardWriter.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityCardWriter.class, "RFIDWriterTE");

		Alarm = new BlockAlarm();
		GameRegistry.registerBlock(Alarm, "alarm");
		Alarm.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "AlarmTE");
		
		EntityDetector = new BlockEntityDetector();
		GameRegistry.registerBlock(EntityDetector, "entitydetector");
		EntityDetector.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityEntityDetector.class, "EntityDetectorTE");

		// Register Items
		magCard = new ItemMagCard();
		GameRegistry.registerItem(magCard, "opensecurity.magCard");
		magCard.setCreativeTab(CreativeTab);

		rfidCard = new ItemRFIDCard();
		GameRegistry.registerItem(rfidCard, "opensecurity.rfidCard");
		rfidCard.setCreativeTab(CreativeTab);
		
		rfidReaderCard = new ItemRFIDReaderCard();
		GameRegistry.registerItem(rfidReaderCard, "opensecurity.rfidReaderCard");
		rfidReaderCard.setCreativeTab(CreativeTab);
		li.cil.oc.api.Driver.add(new RFIDReaderCardDriver());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		Callable<FileSystem> factory = new Callable<FileSystem>() {
			public FileSystem call() {
				return li.cil.oc.api.FileSystem.fromClass(OpenSecurity.class, OpenSecurity.MODID, "/lua/SecureOS/");
			}
		};
		secureOS_disk = li.cil.oc.api.Items.registerFloppy("SecureOS", 1, factory);
		
		//proxy.registerRenderers();
		ItemStack redstone      = new ItemStack(Items.redstone);
		ItemStack paper         = new ItemStack(Items.paper);
		ItemStack noteblock     = new ItemStack(Blocks.noteblock);
		ItemStack t2microchip   = li.cil.oc.api.Items.get("chip2").createItemStack(1);
		ItemStack t1microchip   = li.cil.oc.api.Items.get("chip1").createItemStack(1);
    	ItemStack t1ram    		= li.cil.oc.api.Items.get("ram1").createItemStack(1);
    	ItemStack pcb		   	= li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
    	ItemStack controlunit	= li.cil.oc.api.Items.get("cu").createItemStack(1);
    	ItemStack wlancard		= li.cil.oc.api.Items.get("wlanCard").createItemStack(1);
    	ItemStack cardbase		= li.cil.oc.api.Items.get("card").createItemStack(1);
    	ItemStack cable			= li.cil.oc.api.Items.get("cable").createItemStack(1);
    	ItemStack transistor	= li.cil.oc.api.Items.get("transistor").createItemStack(1);
    	ItemStack floppy		= li.cil.oc.api.Items.get("floppy").createItemStack(1);
    	
		GameRegistry.addRecipe( new ItemStack(rfidReaderCard, 1), 
				"MRM",
				" N ",
				"BC ",
				'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit);
		
		GameRegistry.addRecipe( new ItemStack(EntityDetector, 1), 
				"MRM",
				"   ",
				"BC ",
				'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit);
		
		GameRegistry.addRecipe( new ItemStack(rfidCardReader, 1),
				" R ",
				"PFT",
				" C ",
				'F', rfidReaderCard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);
		
		GameRegistry.addRecipe( new ItemStack(Alarm, 1),
				" R ",
				"PNC",
				" T ",
				'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);
		
		GameRegistry.addRecipe( new ItemStack(cardWriter, 1),
				"TRT",
				"SUS",
				"PC ",
				'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram);
		
		GameRegistry.addRecipe( new ItemStack(magCardReader, 1),
				"T T",
				"S S",
				"PC ",
				'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor);
		
		GameRegistry.addRecipe( new ItemStack(rfidCard, 6),
				"P P",
				" S ",
				"PTP",
				'P', paper, 'S', transistor, 'T', t1microchip);
		
		GameRegistry.addRecipe( new ItemStack(magCard, 6),
				"P P",
				" S ",
				"P P",
				'P', paper, 'S', transistor);
		
		GameRegistry.addShapelessRecipe( secureOS_disk, new Object[] { floppy, magCard });
	}
}