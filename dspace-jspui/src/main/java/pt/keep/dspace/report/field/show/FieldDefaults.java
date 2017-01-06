package pt.keep.dspace.report.field.show;

import org.dspace.core.ConfigurationManager;

public class FieldDefaults {
	private String _id;
	private boolean initialChecked;
	
	private String getProperty (String def, String defaul) {
		String data = ConfigurationManager.getProperty(def);
		if (data == null) return defaul;
		return data;
	}
	
	public FieldDefaults (String id) {
		this.initialChecked = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".default.checked");
	}

	public String getID() {
		return _id;
	}

	public boolean isInitialChecked() {
		return initialChecked;
	}
	
}
