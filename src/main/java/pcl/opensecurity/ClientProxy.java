package pcl.opensecurity;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.opensecurity.CommonProxy;
import pcl.opensecurity.containers.MagCardContainer;
import pcl.opensecurity.renderers.MagCardComponentRenderer;
import pcl.opensecurity.tileentity.MagComponent;
import pcl.opensecurity.tileentity.RFIDComponent;
import pcl.opensecurity.gui.SecurityGUIHandler;
import pcl.opensecurity.renderers.ItemMagComponentRenderer;
import pcl.opensecurity.renderers.ItemRFIDComponentRenderer;
import pcl.opensecurity.blocks.BaseMagReaderBlock;

public class ClientProxy extends CommonProxy {
	
	public void registerRenderers()
	{
		if (OpenSecurity.render3D) {
			TileEntitySpecialRenderer MagCardRender = new MagCardComponentRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(MagComponent.class, MagCardRender);
			MinecraftForgeClient.registerItemRenderer(OpenSecurity.cfg.magCardBlockID, new ItemMagComponentRenderer(MagCardRender, new MagComponent()));
			
			TileEntitySpecialRenderer RFIDCardRender = new MagCardComponentRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(RFIDComponent.class, RFIDCardRender);
			MinecraftForgeClient.registerItemRenderer(OpenSecurity.cfg.magCardBlockID, new ItemMagComponentRenderer(RFIDCardRender, new MagComponent()));
		}
	}
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof MagComponent)
        {
        	MagComponent icte = (MagComponent) te;
            return new MagCardContainer(player.inventory, icte);
        } else if (te != null && te instanceof RFIDComponent)
        {
        	MagComponent icte = (MagComponent) te;
            return new MagCardContainer(player.inventory, icte);
        }
        else
        {
            return null;
        }
    }
	
}