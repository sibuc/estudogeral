package pt.keep.dspace.report.field;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;

public class FieldManager {
    private static Logger log = Logger.getLogger(FieldManager.class);
	public static FieldManager getInstance () throws ReportConfigurationException {
		return new FieldManager();
	}
	public static FieldManager getInstance (Context ct) throws ReportConfigurationException {
		return new FieldManager(ct);
	}
	
	
	private List<Field> _fields;
	private Context _ct;
	
	public FieldManager () throws ReportConfigurationException {
		_fields = new ArrayList<Field>();
		String conf = ConfigurationManager.getProperty("stats.reports.fields");
		if (conf == null) throw new ReportConfigurationException("There is no fields to export at the configuration file.");
		for (String id : conf.split(",")) {
			id = id.trim();
			if (!id.equals("")) {
				_fields.add(new Field(this.getContext(), id));
			}
		}
	}
	public FieldManager (Context ct) throws ReportConfigurationException {
		_ct = ct;
		_fields = new ArrayList<Field>();
		String conf = ConfigurationManager.getProperty("stats.reports.fields");
		if (conf == null) throw new ReportConfigurationException("There is no fields to export at the configuration file.");
		for (String id : conf.split(",")) {
			id = id.trim();
			if (!id.equals("")) {
				_fields.add(new Field(this.getContext(), id));
			}
		}
	}
	
	public FieldManager (Context ct, List<Field> fields) throws ReportConfigurationException {
		_ct = ct;
		_fields = fields;
	}
	
	public Context getContext () {
		if (_ct == null)
			try {
				_ct = new Context();
			} catch (SQLException e) {
				log.debug(e.getMessage(), e);
			}
		return _ct;
	}
	
	public List<Field> getFields () {
		return _fields;
	}
}
