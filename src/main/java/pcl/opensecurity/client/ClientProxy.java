package pcl.opensecurity.client;

import java.io.File;
import java.util.List;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;

public class ClientProxy extends CommonProxy {
	
	public void listFilesForFolder(final File folder) {
		AlarmResource r = new AlarmResource();
		int i = 1;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	r.addSoundReferenceMapping(i, fileEntry.getName()); //add map soundlocation -> recordX
	        	registerSound(fileEntry.getName());
	        	i++;
	        }
	    }
	    r.registerAsResourceLocation(); //finalise IResourcePack
	    Minecraft.getMinecraft().refreshResources();
	}
	
    @Override
    public void registerSounds () {
        listFilesForFolder(OpenSecurity.alarmSounds);
    	  //List<IResourcePack> defaultResourcePacks = null;
    	  
    	  //defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks");
    	  
    	  //defaultResourcePacks.add(new FolderResourcePack(new File( Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator+"mods"+File.separator+"OpenSecurityExternal")));
    	  
    }
    
    /**
     * Register a {@link SoundEvent}.
     *
     * @param soundName The SoundEvent's name without the testmod3 prefix
     * @return The SoundEvent
     */
    private static SoundEvent registerSound(String soundName)
    {
        final ResourceLocation soundID = new ResourceLocation(AlarmResource.PACK_NAME, soundName);
        return GameRegistry.register(new SoundEvent(soundID).setRegistryName(soundID));
    }
}