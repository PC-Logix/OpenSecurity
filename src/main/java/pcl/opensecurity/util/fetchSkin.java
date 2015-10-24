/**
 * 
 */
package pcl.opensecurity.util;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import pcl.opensecurity.OpenSecurity;

/**
 * @author Caitlyn
 *
 */
public class fetchSkin {
	private void downloadSkin(String username)
    {
        HttpURLConnection httpurlconnection = null;

        OpenSecurity.logger.debug("Downloading "+username+"'s skin");
        try
        {
            httpurlconnection = (HttpURLConnection)(new URL("http://skins.minecraft.net/MinecraftSkins/"+username+".png")).openConnection(Minecraft.getMinecraft().getProxy());
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();
 
            if (httpurlconnection.getResponseCode() / 100 != 2)
            {
            	OpenSecurity.logger.error("Server response code did not return 200, skin servers might be down.");
                //At this point I'll change the GUI to a backup one in case of servers being down
            }
 
            BufferedImage bufferedimage;
            bufferedimage = ImageIO.read(httpurlconnection.getInputStream());
            int i = bufferedimage.getWidth();
            int j = bufferedimage.getHeight();
            int[] aint = new int[i * j];
            bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
            int[] imagePixelData = aint;
        }
        catch (Exception exception)
        {
        	OpenSecurity.logger.error("Error occurred when downloading skin, however, skin servers seem to be up.", exception);
            //Once again, backup code will initiate once the finally ends
        }
        finally
        {
            if (httpurlconnection != null)
            {
                httpurlconnection.disconnect();
            }
        }
    }
}
