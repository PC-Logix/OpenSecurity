package pcl.opensecurity;


import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.opensecurity.CommonProxy;
import pcl.opensecurity.containers.MagCardContainer;
import pcl.opensecurity.renderers.BlockMagCardRenderer;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import pcl.opensecurity.renderers.ItemMagComponentRenderer;

public class ClientProxy extends CommonProxy {
	
	public void registerRenderers()
	{
		if (OpenSecurity.render3D) {
			TileEntitySpecialRenderer MagCardRender = new BlockMagCardRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagReader.class, MagCardRender);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenSecurity.magCardReader), new ItemMagComponentRenderer(MagCardRender, new TileEntityMagReader()));
			
			TileEntitySpecialRenderer RFIDCardRender = new BlockMagCardRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRFIDReader.class, RFIDCardRender);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenSecurity.rfidCardReader), new ItemMagComponentRenderer(RFIDCardRender, new TileEntityMagReader()));
		}
	}
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMagReader)
        {
        	TileEntityMagReader icte = (TileEntityMagReader) te;
            return new MagCardContainer(player.inventory, icte);
        } else if (te != null && te instanceof TileEntityRFIDReader)
        {
        	TileEntityMagReader icte = (TileEntityMagReader) te;
            return new MagCardContainer(player.inventory, icte);
        }
        else
        {
            return null;
        }
    }    
    
}