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
    private int defaultMagCardBlockID = 1980;
    public final int magCardBlockID;
    
    private int defaultRFIDCardBlockID = 1981;
    public final int rfidCardBlockID;
    
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
        magCardBlockID = config.get("blocks", "magCardBlockID", defaultMagCardBlockID).getInt(defaultMagCardBlockID);
        rfidCardBlockID = config.get("blocks", "rfidCardBlockID", defaultRFIDCardBlockID).getInt(defaultRFIDCardBlockID);
        magCardID = config.get("items", "magCardID", defaultMagCardID).getInt(defaultMagCardID);
        rfidCardID = config.get("items", "rfidCardID", defaultRFIDCardID).getInt(defaultRFIDCardID);
        render3D = config.get("options", "Render3D", true, "Should we use the 3D Model, or a block").getBoolean(true);
        enableMUD = config.get("options", "enableMUD", true, "Enable the Update Checker? Disabling this will remove all traces of the MUD.").getBoolean(true);
        if( config.hasChanged() )
        {
            config.save();
        }
    }
}
