/**
 * 
 */
package pcl.opensecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Caitlyn
 *
 */
public class Config {
	public List<String> alarmsConfigList = new ArrayList<String>();
	//public final boolean render3D;
	public final boolean enableplaySoundAt;
	public final boolean enableMUD;
	public final String alarms;
	public final int rfidMaxRange;
	public final boolean ignoreUUIDs;

	public Config(Configuration config) {
		config.load();
		enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
		alarms = config.get("options", "customAlarms", "klaxon1,klaxon2", "A comma separated list of custom alarm sounds eg: \"alarm1,alarm2,alarm3\"").getString();
		alarmsConfigList = Arrays.asList(alarms.split("\\s*,\\s*"));
		rfidMaxRange = config.getInt("rfidMaxRange", "options", 16, 1, 64, "The maximum range of the RFID Reader in blocks");
		enableplaySoundAt = config.get("options", "playSoundAt", false, "Enable/Disable the playSoundAt feature of alarm blocks, this allows any user to play any sound at any location in a world, and is exploitable, disabled by default.").getBoolean(false);
		ignoreUUIDs = config.getBoolean("ignoreUUIDs", "options", false, "RFID and Mag cards will return '-1' for UUIDs.  Allows for less secure security.");
		if (config.hasChanged()) {
			config.save();
		}
	}
}
