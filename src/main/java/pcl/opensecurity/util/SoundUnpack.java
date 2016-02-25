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

	@SuppressWarnings("static-access")
	public void load() {
		File f = new File("\\mods\\");
		File jar = null;
		try {
			jar = new File(SoundUnpack.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			new ExtractDirectory(new URL("file:/" + jar  + "!/assets/opensecurity/sounds/alarms/"), "mods/OpenSecurity/sounds/alarms");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
