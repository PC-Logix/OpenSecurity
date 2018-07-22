package pcl.opensecurity;

/**
 * This file is automatically updated by Jenkins as part of the CI build script
 * in Ant. Don't put any pre-set values here.
 * 
 * @author AfterLifeLochie, stolen from LanteaCraft, another fine PC-Logix
 *         Minecraft mod.
 */
public class BuildInfo {
	public static final String modName = "OpenSecurity";
	public static final String modID = "opensecurity";

	public static final String versionNumber = "@VERSION@";
	public static final String buildNumber = "@BUILD@";

	public static int getBuildNumber() {
		if (buildNumber.equals("@" + "BUILD" + "@"))
			return 0;
		return new Double(buildNumber).intValue();
	}
	
	public static int getVersionNumber() {
		if (versionNumber.equals("@" + "VERSION" + "@"))
			return 0;
		return new Double(versionNumber).intValue();
	}

	public static boolean isDevelopmentEnvironment() {
		return getBuildNumber() == 0;
	}
}
