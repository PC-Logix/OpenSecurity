package pcl.opensecurity.client.sounds;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import org.apache.commons.io.input.NullInputStream;

import pcl.opensecurity.OpenSecurity;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class AlarmResource implements IResourcePack {
   
    public static final String PACK_NAME = "opensecurity";
    public static Map<String,String> sound_map = new HashMap<String,String>();
    private static File mc_dir = Minecraft.getMinecraft().mcDataDir;

    public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException {
        if (resourceLocation.getResourcePath().equals("sounds.json")) return generateSoundsJSON();
        return getResourceStream(resourceLocation);
    }
    
    public boolean resourceExists(ResourceLocation l) {
        return isResourceFromThisPack(l) && (l.getResourcePath().equals("sounds.json") || isResourceFromThisPack(l) && getRealPath(l.getResourcePath()).exists());
    }
    
    public Set<String> getResourceDomains() {
        return Sets.newHashSet(getPackName());
    }
    
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
    public BufferedImage getPackImage() throws IOException {
        return null; //don't need no image for this shit
    }
    
    public String getPackName() {
        return PACK_NAME;
    }

   /** HELPERS */

    private InputStream getResourceStream(ResourceLocation l)
    {
    	try {
			return new FileInputStream(mc_dir+"/mods/OpenSecurity/assets/opensecurity/sounds/"+l.getResourcePath().substring(7));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    
    }
    
    private boolean isResourceFromThisPack (ResourceLocation l) {
        return l.getResourceDomain().equals(getPackName());
    }
    
    private File getRealPath (String file) {
    	return new File(mc_dir.getAbsolutePath(),"/mods/OpenSecurity/assets/opensecurity/sounds/"+file.substring(7));
    }
    
    private static InputStream generateSoundsJSON () throws IOException {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, String> entry : sound_map.entrySet()) {
            JsonObject event = new JsonObject();
            event.addProperty("category", "records"); // put under the "blocks" category for sound options
            JsonArray sounds = new JsonArray(); // array of sounds (will only ever be one)
            JsonObject sound = new JsonObject(); // sound object (instead of primitive to use 'stream' flag)
            //"klaxon1":"opensecurity:/alarms/klaxon1","stream:false"
            sound.addProperty("name", PACK_NAME + ":alarms/" + entry.getValue().substring(0, entry.getValue().lastIndexOf('.'))); // path to file
            sound.addProperty("stream", false); // streaming seems to break the alarm... why?
            sounds.add(sound);
            event.add("sounds", sounds);
            root.add(entry.getValue().substring(0, entry.getValue().lastIndexOf('.')), event); // event name (same as name sent to ItemCustomRecord)
        }
        return new ByteArrayInputStream(new Gson().toJson(root).getBytes());
    }

    public void addSoundReferenceMapping (int recordNum, String pathToSound) {
        sound_map.put(PACK_NAME + recordNum,pathToSound);
    }

    public void registerAsResourceLocation () {
     	List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");
    	defaultResourcePacks.add(this);
    }
   
}