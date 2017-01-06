package pt.keep.dspace.sharing.configuration;

import org.dspace.core.ConfigurationManager;

public class SharingConfigurationManager {
	public static String getProperty (String key, String onNull) {
		String prop = ConfigurationManager.getProperty("sharing."+key);
		if (prop == null) prop = onNull;
		return prop;
	}
	
	public static boolean getProperty (String key, boolean onNull) {
		return ConfigurationManager.getBooleanProperty("sharing."+key, onNull);
	}
	
	public static String getPrefix () {
		return "sharing.";
	}
}
