/**
 * 
 */
package pcl.opensecurity;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

/**
 * @author Caitlyn
 *
 */
public class Config
{
    private int defaultsecurityBlockID = 1980;
    public final int securityBlockID;
    
    private int defaultMagCardID = 19800;
    public final int magCardID;
    
    private int defaultRFIDCardID = 19801;
    public final int rfidCardID;
    
    public final boolean render3D;
    
	private boolean defaultEnableMUD = true;
	public final boolean enableMUD;

    public Config(Configuration config)
    {
        config.load();
        securityBlockID = config.get("blocks", "baseSecutyID", defaultsecurityBlockID).getInt(defaultsecurityBlockID);
        magCardID = config.get("items", "PrintedPageID", defaultMagCardID).getInt(defaultMagCardID);
        rfidCardID = config.get("items", "PrintedPageID", defaultRFIDCardID).getInt(defaultRFIDCardID);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
        enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
