package pt.keep.dspace.report.field.database;

import org.dspace.core.Context;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;

public abstract class AbstractDatabaseFieldAdapter {
	private String id;
	private Context _ct;
	
	public AbstractDatabaseFieldAdapter () {}
	
	public void initialize (Context ct, String id) throws ReportConfigurationException {
		this.id = id;
		_ct = ct;
		this.init();
	}
	
	public String getID () {
		return this.id;
	}

	public Context getContext () {
		return _ct;
	}
	
	public abstract void init () throws ReportConfigurationException;
	public abstract String generateWhere (String tableid, String value) throws ReportGenerationException;
	public abstract String generateSelect (String tableid) throws ReportGenerationException;
	public abstract String generateSelect (String tableid, String extraname) throws ReportGenerationException;
	
}
