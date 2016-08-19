package pcl.opensecurity.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class SoundUnpack implements IFMLLoadingPlugin, IFMLCallHook {
	public void load() throws IOException {
		File f = new File("mods/OpenSecurity/sounds/alarms/");
		f.mkdirs();
		final String path = "assets/opensecurity/sounds/alarms";
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		if(jarFile.isFile()) {  // Run with JAR file
		    JarFile jar = null;
			jar = new JarFile(jarFile);
		    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
		    while(entries.hasMoreElements()) {
		        final String name = entries.nextElement().getName();
		        if (name.startsWith(path + "/") && name.endsWith(".ogg")) { //filter according to the path
		        	InputStream ddlStream = SoundUnpack.class.getClassLoader().getResourceAsStream(name);
		        	Path p = Paths.get(name);
		        	String file = p.getFileName().toString();
		        		try (FileOutputStream fos = new FileOutputStream(f + "/" + file);){
		        		    byte[] buf = new byte[2048];
		        		    int r;
		        		    while(-1 != (r = ddlStream.read(buf))) {
		        		        fos.write(buf, 0, r);
		        		    }
		        		}
		        	
		        }
		    }
			jar.close();
		}
/*		File jar = null;
		File f = new File("mods/OpenSecurity/sounds/");
		if (!f.exists()) {
			f.mkdirs();
			try {
				jar = new File(SoundUnpack.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
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
		}*/
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
		try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
