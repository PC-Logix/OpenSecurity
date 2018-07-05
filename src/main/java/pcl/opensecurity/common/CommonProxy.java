/**
 *
 */
package pcl.opensecurity.common;


import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pcl.opensecurity.OpenSecurity;

/**
 * @author Caitlyn
 */
public class CommonProxy {

    public World getWorld(int dimId) {
        //overridden separately for client and server.
        return null;
    }

    public void registerSounds() {
        // TODO Auto-generated method stub
    }

    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(OpenSecurity.instance, new GuiHandler());
    }

    public void preinit() {
        registerRenderers();
    }

    protected void registerRenderers() {
        // TODO Auto-generated method stub

    }
	public void registerModels() {
    }

}
