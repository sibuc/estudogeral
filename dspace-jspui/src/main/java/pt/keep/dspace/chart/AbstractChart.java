package pt.keep.dspace.chart;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;



public abstract class AbstractChart {

    private static Logger log = Logger.getLogger(AbstractChart.class);
	public abstract String chartID ();

	private String _width;
	private String _height;

	public String getWidth () {
		if (_width == null) _width = ConfigurationManager.getProperty("stats.chart."+chartID()+".width");
		if (_width == null) _width = "300";
		return _width;
	}
	
	public String getHeight () {
		if (_height == null) _height = ConfigurationManager.getProperty("stats.chart."+chartID()+".height");
		if (_height == null) _height = "200";
		return _height;
	}
	private Context _context = null;
	public Context getContext () {
		if (_context == null)
			try {
				_context = new Context();
			} catch (SQLException e) {
				log.debug(e.getMessage(), e);
			}
		return _context;
	}
}
