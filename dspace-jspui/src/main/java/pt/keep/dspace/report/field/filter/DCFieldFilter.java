package pt.keep.dspace.report.field.filter;

import java.sql.SQLException;
import java.util.List;

import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class DCFieldFilter extends AbstractFilter {
	private String dcfield;
	
	@Override
	public void init() throws ReportConfigurationException {
		this.dcfield = ConfigurationManager.getProperty("stats.reports.field."+super.getID()+".database.metadatafield");
	}

	@Override
	public List<String> getPossibleValues(ExportEntity entity, Object entityID)
			throws ReportGenerationException {
		try {
			return ReportDatabaseManager.getDistinctValues(super.getContext(), this.dcfield, entity, entityID);
		} catch (SQLException e) {
			throw new ReportGenerationException(e);
		} catch (ReportConfigurationException e) {
			throw new ReportGenerationException(e);
		}
	}

	@Override
	public String cleanData(String data) {
		return data;
	}


}
