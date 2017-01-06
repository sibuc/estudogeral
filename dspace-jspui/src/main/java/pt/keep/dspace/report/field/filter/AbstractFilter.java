package pt.keep.dspace.report.field.filter;

import java.util.List;

import org.dspace.core.Context;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;

public abstract class AbstractFilter {
	
	private String id;
	private Context _ct;
	
	public void initialize (Context ct, String id) throws ReportConfigurationException {
		this.id = id;
		_ct = ct;
		this.init();
	}
	
	public String getID() {
		return this.id;
	}
	
	public Context getContext () {
		return _ct;
	}
	
	public abstract void init () throws ReportConfigurationException;
	public abstract List<String> getPossibleValues (ExportEntity entity, Object entityID) throws ReportGenerationException;
	public abstract String cleanData (String data);
}
