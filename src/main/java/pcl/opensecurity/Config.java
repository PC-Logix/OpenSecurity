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
	public final boolean render3D;
	public final boolean enableMUD;
	public final String alarms;
	public final int rfidMaxRange;

	public Config(Configuration config) {
		config.load();
		render3D = config.get("options", "Render3D", true, "Should we use 3D Models, or a block").getBoolean(true);
		enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
		alarms = config.get("options", "customAlarms", "klaxon1,klaxon2", "A comma seperated list of custom alarm sounds eg: \"alarm1,alarm2,alarm3\"").getString();
		alarmsConfigList = Arrays.asList(alarms.split("\\s*,\\s*"));
		rfidMaxRange = config.getInt("rfidMaxRange", "options", 16, 16, 64, "The maximum range of the RFID Reader in blocks");
		if (config.hasChanged()) {
			config.save();
		}
	}
}
