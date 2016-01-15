package pcl.opensecurity;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pcl.opensecurity.OSPacketHandler.PacketHandler;
import pcl.opensecurity.gui.OSGUIHandler;
import pcl.opensecurity.networking.packet.PacketBoltFire;
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
import cpw.mods.fml.relauncher.Side;

@Mod(modid = OpenSecurity.MODID, name = "OpenSecurity", version = BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers")
public class OpenSecurity {

	public static final String MODID = "opensecurity";
	public static File alarmSounds;


	@Instance(value = MODID)
	public static OpenSecurity instance;

	@SidedProxy(clientSide = "pcl.opensecurity.ClientProxy", serverSide = "pcl.opensecurity.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	public static boolean debug = false;
	public static int rfidRange;
	public static boolean enableplaySoundAt = false;
	public static boolean ignoreUUIDs = false;

	public static final Logger logger = LogManager.getFormatterLogger(MODID);

	public static List<String> alarmList;

	
	
	public static SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		alarmSounds = new File("./mods/OpenSecurity/sounds/alarms/");
		System.out.println(alarmSounds);
		alarmList = cfg.alarmsConfigList;
		rfidRange = cfg.rfidMaxRange;
		enableplaySoundAt = cfg.enableplaySoundAt;
		ignoreUUIDs = cfg.ignoreUUIDs;

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
	    network.registerMessage(PacketBoltFire.class, PacketBoltFire.class, 0, Side.CLIENT);
	    ContentRegistry.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		proxy.registerSounds();
	}
}