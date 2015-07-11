package pcl.opensecurity;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import li.cil.oc.api.fs.FileSystem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pcl.opensecurity.OSPacketHandler.PacketHandler;
import pcl.opensecurity.blocks.BlockAlarm;
import pcl.opensecurity.blocks.BlockCardWriter;
import pcl.opensecurity.blocks.BlockData;
import pcl.opensecurity.blocks.BlockDoorController;
import pcl.opensecurity.blocks.BlockEntityDetector;
import pcl.opensecurity.blocks.BlockMagReader;
import pcl.opensecurity.blocks.BlockRFIDReader;
import pcl.opensecurity.blocks.BlockSecurityDoor;
import pcl.opensecurity.blocks.BlockSwitchableHub;
import pcl.opensecurity.blocks.BlockKVM;
import pcl.opensecurity.client.CreativeTab;
import pcl.opensecurity.drivers.RFIDReaderCardDriver;
import pcl.opensecurity.gui.OSGUIHandler;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;
import pcl.opensecurity.items.ItemRFIDReaderCard;
import pcl.opensecurity.items.ItemSecurityDoor;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import pcl.opensecurity.tileentity.TileEntityDataBlock;
import pcl.opensecurity.tileentity.TileEntityDoorController;
import pcl.opensecurity.tileentity.TileEntityEntityDetector;
import pcl.opensecurity.tileentity.TileEntityKVM;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import pcl.opensecurity.tileentity.TileEntitySwitchableHub;
import pcl.opensecurity.util.OSBreakEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = OpenSecurity.MODID, name = "OpenSecurity", version = BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers")
public class OpenSecurity {

	public static final String MODID = "opensecurity";

	public static Block magCardReader;
	public static Block rfidCardReader;
	public static Block cardWriter;
	public static Block Alarm;
	public static Block EntityDetector;
	public static Block SecurityDoor;
	public static Block DoorController;
	public static Block DataBlock;
	public static Block SwitchableHub;
	public static Block BlockKVM;
	public static Item magCard;
	public static Item rfidCard;
	public static Item securityDoor;
	public static Item rfidReaderCard;
	public static ItemBlock securityitemBlock;
	public static ItemStack secureOS_disk;

	@Instance(value = MODID)
	public static OpenSecurity instance;

	@SidedProxy(clientSide = "pcl.opensecurity.ClientProxy", serverSide = "pcl.opensecurity.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	public static boolean debug = false;
	public static int rfidRange;
	public static boolean enableplaySoundAt = false;

	public static final Logger logger = LogManager.getFormatterLogger(MODID);

	public static List<String> alarmList;

	public static CreativeTabs CreativeTab = new CreativeTab("OpenSecurity");
	
	public static SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));

		alarmList = cfg.alarmsConfigList;
		rfidRange = cfg.rfidMaxRange;
		enableplaySoundAt = cfg.enableplaySoundAt;

		if ((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD) {
			logger.info("Registering mod with OpenUpdater");
			try {
				Class.forName("pcl.mud.OpenUpdater").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null, FMLCommonHandler.instance().findContainerFor(this), new URL("http://PC-Logix.com/OpenSecurity/get_latest_build.php?mcver=1.7.10"), new URL("http://PC-Logix.com/OpenSecurity/changelog.php?mcver=1.7.10"));
			} catch (Throwable e) {
				logger.info("OpenUpdater is not installed, not registering.");
			}
		}
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new OSGUIHandler());
	    network = NetworkRegistry.INSTANCE.newSimpleChannel("OpenSecurity");
	    network.registerMessage(PacketHandler.class, OSPacketHandler.class, 0, Side.SERVER);
		registerBlocks();
		registerItems();
		registerEvents();
	}

	private void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new OSBreakEvent());
	}

	private void registerItems() {
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

		securityDoor = new ItemSecurityDoor(SecurityDoor);
		GameRegistry.registerItem(securityDoor, "opensecurity.securityDoor");
		securityDoor.setCreativeTab(CreativeTab);
	}

	private void registerBlocks() {
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

		DoorController = new BlockDoorController();
		GameRegistry.registerBlock(DoorController, "doorcontroller");
		DoorController.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityDoorController.class, "DoorControllerTE");

		SecurityDoor = new BlockSecurityDoor();
		GameRegistry.registerBlock(SecurityDoor, "SecurityDoor");
		GameRegistry.registerTileEntity(TileEntitySecureDoor.class, "SecureDoorTE");

		DataBlock = new BlockData();
		GameRegistry.registerBlock(DataBlock, MODID + ".DataBlock");
		DataBlock.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityDataBlock.class, MODID + ".DataBlockTE");

		SwitchableHub = new BlockSwitchableHub();
		GameRegistry.registerBlock(SwitchableHub, MODID + ".SwitchableHub");
		SwitchableHub.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntitySwitchableHub.class, MODID + ".SwitchableHubTE");
		
		BlockKVM = new BlockKVM();
		GameRegistry.registerBlock(BlockKVM, MODID + ".BlockKVM");
		BlockKVM.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityKVM.class, MODID + ".KVMTE");

		logger.info("Registered Blocks");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		registerRecipes();
	}

	private void registerRecipes() {
		Callable<FileSystem> factory = new Callable<FileSystem>() {
			@Override
			public FileSystem call() {
				return li.cil.oc.api.FileSystem.fromClass(OpenSecurity.class, OpenSecurity.MODID, "/lua/SecureOS/");
			}
		};
		secureOS_disk = li.cil.oc.api.Items.registerFloppy("SecureOS", 1, factory);

		ItemStack redstone = new ItemStack(Items.redstone);
		ItemStack paper = new ItemStack(Items.paper);
		ItemStack noteblock = new ItemStack(Blocks.noteblock);
		ItemStack door = new ItemStack(Items.iron_door);
		ItemStack obsidian = new ItemStack(Blocks.obsidian);
		ItemStack t2microchip = li.cil.oc.api.Items.get("chip2").createItemStack(1);
		ItemStack t1microchip = li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack t1ram = li.cil.oc.api.Items.get("ram1").createItemStack(1);
		ItemStack pcb = li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
		ItemStack controlunit = li.cil.oc.api.Items.get("cu").createItemStack(1);
		ItemStack wlancard = li.cil.oc.api.Items.get("wlanCard").createItemStack(1);
		ItemStack cardbase = li.cil.oc.api.Items.get("card").createItemStack(1);
		ItemStack cable = li.cil.oc.api.Items.get("cable").createItemStack(1);
		ItemStack transistor = li.cil.oc.api.Items.get("transistor").createItemStack(1);
		ItemStack floppy = li.cil.oc.api.Items.get("floppy").createItemStack(1);
		ItemStack datacard = li.cil.oc.api.Items.get("dataCard").createItemStack(1);
		ItemStack oc_switch = li.cil.oc.api.Items.get("switch").createItemStack(1);

		GameRegistry.addRecipe(new ItemStack(rfidReaderCard, 1), "MRM", " N ", "BC ", 'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit);

		GameRegistry.addRecipe(new ItemStack(EntityDetector, 1), "MRM", "   ", "BC ", 'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit);

		GameRegistry.addRecipe(new ItemStack(rfidCardReader, 1), " R ", "PFT", " C ", 'F', rfidReaderCard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(DataBlock, 1), " D ", "PFT", " C ", 'D', datacard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(Alarm, 1), " R ", "PNC", " T ", 'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(cardWriter, 1), "TRT", "SUS", "PC ", 'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram);

		GameRegistry.addRecipe(new ItemStack(magCardReader, 1), "T T", "S S", "PC ", 'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor);

		GameRegistry.addRecipe(new ItemStack(rfidCard, 6), "P P", " S ", "PTP", 'P', paper, 'S', transistor, 'T', t1microchip);

		GameRegistry.addRecipe(new ItemStack(magCard, 6), "P P", " S ", "P P", 'P', paper, 'S', transistor);

		GameRegistry.addRecipe(new ItemStack(securityDoor, 1), "TOT", "ODO", "SOS", 'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian);

		GameRegistry.addRecipe(new ItemStack(DoorController, 1), "TOT", "OCO", "SBS", 'B', cable, 'C', controlunit, 'S', transistor, 'T', t2microchip, 'O', obsidian);

		GameRegistry.addRecipe(new ItemStack(SwitchableHub, 1), "TBT", "BSB", "RBR", 'B', cable, 'S', oc_switch, 'R', transistor, 'T', t2microchip, 'O', obsidian);
		
		GameRegistry.addRecipe(new ItemStack(BlockKVM, 1), " B ", "BSB", "RBR", 'B', cable, 'S', oc_switch, 'R', transistor, 'T', t2microchip, 'O', obsidian);
		
		GameRegistry.addShapelessRecipe(secureOS_disk, new Object[] { floppy, magCard });
		logger.info("Registered Recipes");
	}
}