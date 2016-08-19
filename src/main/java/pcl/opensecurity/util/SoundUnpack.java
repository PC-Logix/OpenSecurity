package pcl.opensecurity.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import pcl.opensecurity.BuildInfo;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class SoundUnpack implements IFMLLoadingPlugin, IFMLCallHook {
	public void load() {
		File jar = null;
		File f = new File("mods/OpenSecurity/sounds/");
		//if (!f.exists()) {
			f.mkdirs();
			try {
				Path path = Paths.get(SoundUnpack.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				jar = new File(path);
				//jar = new File(SoundUnpack.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				System.out.println("Selected Jar");
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			try {
				FileUtils.copyResourcesRecursively(new URL("file://" + jar  + "/assets/opensecurity/sounds/alarms/"), f);
				System.out.println("Exctracted sounds");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return getClass().getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public Void call() {
		load();

		return null;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
