/**
 * 
 */
package pcl.opensecurity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Caitlyn
 *
 */
public class Config
{
	public List<String> alarmsConfigList = new ArrayList<String>();
    public final boolean render3D;
	public final boolean enableMUD;
	public final String alarms;

    public Config(Configuration config)
    {
        config.load();
        render3D = config.get("options", "Render3D", true, "Should we use 3D Models, or a block").getBoolean(true);
        enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        alarms = config.get("options", "customAlarms", "klaxon1", "A comma seperated list of custom alarm sounds eg: \"alarm1,alarm2,alarm3\"").getString();
        alarmsConfigList = Arrays.asList(alarms.split("\\s*,\\s*"));
        //System.out.println(alarmsConfigList);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
