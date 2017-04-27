package pcl.opensecurity.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;

public class ClientProxy extends CommonProxy {
	public static File alarmSounds;
	public static List<String> alarmList = new ArrayList<String>();

	public void listFilesForFolder(final File folder) {
		AlarmResource r = new AlarmResource();
		int i = 1;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	r.addSoundReferenceMapping(i, fileEntry.getName()); //add map soundlocation -> recordX
	        	i++;
	        }
	    }
	    
	    r.registerAsResourceLocation(); //finalise IResourcePack
	}
	
    @Override
    public void registerSounds () {
		File[] listOfFiles;
		File alarmSounds = new File("./mods/OpenSecurity/assets/opensecurity/sounds/alarms");
		if (alarmSounds.exists()) {
			listOfFiles = alarmSounds.listFiles();
			
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
				alarmList.add(listOfFiles[i].getName());
		      }
		    }
		}
        listFilesForFolder(alarmSounds);
    }

}