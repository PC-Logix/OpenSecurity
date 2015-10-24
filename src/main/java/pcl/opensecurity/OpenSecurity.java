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
	    ContentRegistry.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
	}
}