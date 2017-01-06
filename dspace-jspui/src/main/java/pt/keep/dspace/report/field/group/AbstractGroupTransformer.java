package pt.keep.dspace.report.field.group;


import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.report.exceptions.ReportGenerationException;

public abstract class AbstractGroupTransformer {
	
	private String id;
	
	public AbstractGroupTransformer () {}
	
	public String getID () {
		return this.id;
	}

	public String getProperty (String def, String defaul) {
		String data = ConfigurationManager.getProperty(def);
		if (data == null) return defaul;
		return data;
	}
	
	public void initialize (String id) throws ReportGenerationException {
		this.id = id;
		this.init();
	}
	
	public abstract void init () throws ReportGenerationException;
	public abstract String transform (String data) throws ReportGenerationException;
}
