package pt.keep.dspace.report.field.show;

import org.dspace.core.ConfigurationManager;

public class FieldStyle {
	private String _id;
	private int _columnsize;
	private boolean _translate;
	
	private String getProperty (String def, String defaul) {
		String data = ConfigurationManager.getProperty(def);
		if (data == null) return defaul;
		return data;
	}
	
	public FieldStyle (String id) {
		_columnsize = Integer.parseInt(this.getProperty("stats.reports.field."+id+".style.columnsize", "0"));
		_translate = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".style.translate");
	}

	public int getColumnsize() {
		return _columnsize;
	}
	
	public boolean hasFixedSize () {
		return _columnsize != 0;
	}

	public boolean hasTranslation() {
		return _translate;
	}

	public String getID() {
		return _id;
	}
	
	
}
