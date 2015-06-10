/**
 * 
 */
package pcl.opensecurity;

import net.minecraftforge.common.config.Configuration;

/**
 * @author Caitlyn
 *
 */
public class Config
{
    
    public final boolean render3D;
    
	private boolean defaultEnableMUD = true;
	public final boolean enableMUD;

    public Config(Configuration config)
    {
        config.load();
        render3D = config.get("options", "Render3D", true, "Should we use 3D Models, or a block").getBoolean(true);
        enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
