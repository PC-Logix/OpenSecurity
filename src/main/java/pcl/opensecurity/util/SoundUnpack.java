package pcl.opensecurity.util;

import java.net.URL;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class SoundUnpack implements IFMLLoadingPlugin, IFMLCallHook {
	
	@SuppressWarnings("static-access")
	public void load() {
		new SoundUnpack().getClass().getClassLoader().getSystemResource(this.getClass().getName() + ".class");
		
		new ExtractDirectory("jar:/assets/opensecurity/sounds/alarms/", "mods/OpenSecurity/sounds/alarms");
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
