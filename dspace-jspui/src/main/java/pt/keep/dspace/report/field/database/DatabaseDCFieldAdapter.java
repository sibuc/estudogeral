package pt.keep.dspace.report.field.database;

import java.sql.SQLException;

import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.field.filter.AbstractFilter;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;
import pt.keep.dspace.report.util.TranslateManager;

public class DatabaseDCFieldAdapter extends AbstractDatabaseFieldAdapter {
	private int fieldID;
	private String classe;
	
	public DatabaseDCFieldAdapter () {
		super();
	}
	
	private AbstractFilter _filter = null;
	
	private AbstractFilter getFilter () {
		if (_filter == null) {
			try {
				String classe = ConfigurationManager.getProperty("stats.reports.field."+super.getID()+".filter.class");
				if (classe == null) throw new ReportConfigurationException("Invalid property report.field."+super.getID()+".filter.class");
				Class c = Class.forName(classe);
				Object obj = c.newInstance();
				if (obj instanceof AbstractFilter) {
					((AbstractFilter) obj).initialize(super.getContext(), super.getID());
					_filter = (AbstractFilter) obj;
				}
			} catch (Exception e) {
				return _filter;
			} 
		}
		return _filter;
	}

	@Override
	public String generateWhere(String itemtableid, String value)
			throws ReportGenerationException {
		if (this.getFilter() == null) {
			return "EXISTS (SELECT t"+super.getID()+".* FROM metadatavalue t"+super.getID()+" WHERE t"+super.getID()+".item_id="+itemtableid+".item_id AND t"+super.getID()+".metadata_field_id="+this.fieldID+" AND t"+super.getID()+".text_value='"+value+"')";
		} else {
			return "EXISTS (SELECT t"+super.getID()+".* FROM metadatavalue t"+super.getID()+" WHERE t"+super.getID()+".item_id="+itemtableid+".item_id AND t"+super.getID()+".metadata_field_id="+this.fieldID+" AND t"+super.getID()+".text_value LIKE '%"+this.getFilter().cleanData(value)+"%')";
		}
	}

	@Override
	public String generateSelect(String itemtableid)
			throws ReportGenerationException {
		return "coalesce((SELECT t"+super.getID()+".text_value FROM metadatavalue t"+super.getID()+" WHERE t"+super.getID()+".item_id="+itemtableid+".item_id AND metadata_field_id="+this.fieldID+" LIMIT 1),'"+TranslateManager.getInstance().translate("stats.reporting.field."+super.getID()+".omission")+"') as "+super.getID();
	}



	@Override
	public void init() throws ReportConfigurationException {
		try {
			fieldID = ReportDatabaseManager.getFieldID(super.getContext(), ConfigurationManager.getProperty("stats.reports.field."+super.getID()+".database.metadatafield"));
			this.classe = ConfigurationManager.getProperty("stats.reports.field."+super.getID()+".filter.class");
		} catch (SQLException e) {
			throw new ReportConfigurationException(e);
		} 
	}



	@Override
	public String generateSelect(String itemtableid, String extraname)
			throws ReportGenerationException {
		return "coalesce((SELECT t"+super.getID()+".text_value FROM metadatavalue t"+super.getID()+" WHERE t"+super.getID()+".item_id="+itemtableid+".item_id AND metadata_field_id="+this.fieldID+" LIMIT 1),'"+TranslateManager.getInstance().translate("stats.reporting.field."+super.getID()+".omission")+"') as "+super.getID()+extraname;
	}

}
