package pcl.opensecurity.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import pcl.opensecurity.OpenSecurity;

import java.io.File;

/* helper to create server resource packs, which will never be synced as they have to be hosted
*  on an external http server, still leaving these here as reference for future usage -.- */

// ServerResourcePackFactory.FACTORY.create("OpenSecuritySounds", "mods/OpenSecurity/assets/opensecurity/sounds", "assets/opensecurity/sounds");

public class ServerResourcePackFactory {
    public static ServerResourcePackFactory FACTORY = new ServerResourcePackFactory();

    public void create(String resourcePackName, String sourcePath, String targetPath){
        File input = new File(getDataDirectory() + sourcePath);

        File outputDirectory = new File(getDataDirectory() + "server-resource-packs");
        outputDirectory.mkdirs();

        File outputFile = new File(outputDirectory + "/" + resourcePackName + ".zip");

        try {
            ZipArchiveOutputStream zip = new ZipArchiveOutputStream(outputFile);
            for(File fileName : FileUtils.listFilesForPath(input.getPath())){
                String relativePath = FilenameUtils.getPath(fileName.getPath()).replaceFirst(input.getPath(), "");
                ZipArchiveEntry entry = new ZipArchiveEntry(targetPath + relativePath + fileName.getName());
                zip.putArchiveEntry(entry);
                zip.write(org.apache.commons.io.FileUtils.readFileToByteArray(fileName));
                zip.closeArchiveEntry();
            }

            ZipArchiveEntry entry = new ZipArchiveEntry("pack.mcmeta");
            zip.putArchiveEntry(entry);
            zip.write(createMcMeta());
            zip.closeArchiveEntry();

            zip.close();

        } catch(Exception e){
            OpenSecurity.logger.warn("couldnt create server resource pack");
        }
    }

    private String getDataDirectory(){
        if(FMLCommonHandler.instance().getEffectiveSide().isServer())
            return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory().getPath();

        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            Minecraft.getMinecraft().mcDataDir.getPath();

        return "";
    }

    private byte[] createMcMeta(){
        String data = "{ \"pack\": { \"pack_format\": 2, \"description\": \"OpenSecurity custom sounds\" } }";
        return data.getBytes();
    }

}
