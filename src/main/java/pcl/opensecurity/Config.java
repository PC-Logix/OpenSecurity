package pcl.opensecurity;
/**
 * @author ben_mkiv, based on MinecraftByExample Templates
 */
import java.io.File;
import java.util.HashMap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;


//todo: check what config values have to be sent by a server so that client methods are aware of server configuration
public class Config extends PermissionAPI {
	private static Configuration config = null;

	static HashMap<String, Property> configOptions = new HashMap<>();

	public static void preInit(){
		File configFile = new File(Loader.instance().getConfigDir(), OpenSecurity.MODID + ".cfg");
		config = new Configuration(configFile);

		syncConfig(true);
	}

	public static void clientPreInit() {
		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}

	public static Configuration getConfig() {
		return config;
	}

	private static void syncConfig(boolean loadConfigFromFile) {
		if (loadConfigFromFile)
			config.load();

		boolean isClient = FMLCommonHandler.instance().getEffectiveSide().isClient();

		Property enableplaySoundAt = config.get("general", "enableplaySoundAt", false);
		enableplaySoundAt.setLanguageKey("gui.config.general.enableplaySoundAt");
		enableplaySoundAt.setComment("Enable/Disable the playSoundAt feature of alarm blocks, this allows any user to play any sound at any location in a world, and is exploitable, disabled by default.");

		Property enableDebugMessages= config.get("general", "enableDebugMessages", false);
		enableDebugMessages.setLanguageKey("gui.config.general.enableDebugMessages");
		enableDebugMessages.setComment("Enable/Disable debug messages in the log");
		enableDebugMessages.setRequiresMcRestart(true);

		Property ignoreUUIDs = config.get("general", "ignoreUUIDs", false);
		ignoreUUIDs.setLanguageKey("gui.config.general.ignoreUUIDs");
		ignoreUUIDs.setComment("RFID and Mag cards will return '-1' for UUIDs.  Allows for less secure security.");
		
		Property registerBlockBreak = config.get("general", "registerBlockBreak", true);
		registerBlockBreak.setLanguageKey("gui.config.general.registerBlockBreak");
		registerBlockBreak.setComment("If false the block break event will not be registered, which will leave Door Controllers and Security Doors able to be broken.");
		registerBlockBreak.setRequiresMcRestart(true);

		Property turretReverseRotation = config.get("general", "turretReverseRotation", true);
		turretReverseRotation.setLanguageKey("gui.config.general.turretReverseRotation");
		turretReverseRotation.setComment("If true - turrets should rotate as in old versions.");

		Property biggerEEPROM = config.get("general", "biggerEEPROM", false);
		biggerEEPROM.setLanguageKey("gui.config.general.biggerEEPROM");
		biggerEEPROM.setComment("if true allows EEPROMS written with the card writer to be twice the configured size default is 4KB * 2");

		Property rfidMaxRange = config.get("general", "rfidMaxRange", 16);
		rfidMaxRange.setMinValue(1);
		rfidMaxRange.setMaxValue(64);
		rfidMaxRange.setLanguageKey("gui.config.general.rfidMaxRange");
		rfidMaxRange.setComment("The maximum range of the RFID Reader in blocks");
		rfidMaxRange.setRequiresMcRestart(true);
		if(isClient)
			rfidMaxRange.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

		Property entityDetectorMaxRange = config.get("general", "entityDetectorMaxRange", 16);
		entityDetectorMaxRange.setMinValue(1);
		entityDetectorMaxRange.setMaxValue(64);
		entityDetectorMaxRange.setLanguageKey("gui.config.general.entityDetectorMaxRange");
		entityDetectorMaxRange.setComment("The maximum range of the Entity Detector in blocks");
		entityDetectorMaxRange.setRequiresMcRestart(true);
		if(isClient)
			entityDetectorMaxRange.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

		Property instantNanoFog = config.get("general", "instantNanoFog", false);
		instantNanoFog.setLanguageKey("gui.config.general.instantNanoFog");
		instantNanoFog.setComment("if enabled NanoFog blocks will spawn instant and no swarm will be spawned to assemble them");

		Property nanoFogSwarmResolution = config.get("client", "nanoFogSwarmResolution", 8);
		nanoFogSwarmResolution.setMinValue(2);
		nanoFogSwarmResolution.setMaxValue(16);
		nanoFogSwarmResolution.setLanguageKey("gui.config.client.nanoFogSwarmResolution");
		nanoFogSwarmResolution.setComment("The resolution used to render Nanofog Swarms");
		nanoFogSwarmResolution.setRequiresMcRestart(true);
		if(isClient)
			nanoFogSwarmResolution.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);

		if (config.hasChanged())
			config.save();
	}

	public static class ConfigEventHandler{
		@SubscribeEvent(priority = EventPriority.NORMAL)
		public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event){
			if (!event.getModID().equals(OpenSecurity.MODID))
				return;

			syncConfig(false);
		}
	}
}