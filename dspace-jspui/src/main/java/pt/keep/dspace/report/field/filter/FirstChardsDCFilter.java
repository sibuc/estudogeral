package pt.keep.dspace.report.field.filter;

import java.sql.SQLException;
import java.util.List;

import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class FirstChardsDCFilter extends AbstractFilter {
	private String dcfield;
	private int length;

	@Override
	public void init() throws ReportConfigurationException {
		this.dcfield = ConfigurationManager.getProperty("stats.reports.field."+super.getID()+".database.metadatafield");
		this.length = ConfigurationManager.getIntProperty("stats.reports.field."+super.getID()+".group.length");
	}

	@Override
	public List<String> getPossibleValues(ExportEntity entity, Object entityID)
			throws ReportGenerationException {
		try {
			return ReportDatabaseManager.getDistinctValues(super.getContext(), this.dcfield, entity, entityID, this.length);
		} catch (SQLException e) {
			throw new ReportGenerationException(e);
		} catch (ReportConfigurationException e) {
			throw new ReportGenerationException(e);
		}
	}

	@Override
	public String cleanData(String data) {
		return data.substring(0, this.length);
	}

}
