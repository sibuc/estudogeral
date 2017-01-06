package pt.keep.dspace.chart;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.chart.collection.AbstractCollectionChart;
import pt.keep.dspace.chart.community.AbstractCommunityChart;

public class ChartManager {
    private static Logger log = Logger.getLogger(ChartManager.class);
	private static ChartManager _instance = null;
	
	
	public static ChartManager getInstance () {
		if (_instance == null) _instance = new ChartManager();
		log.debug("Called");
		return _instance;
	}
	

	private List<AbstractCommunityChart> _comm;
	private List<AbstractCollectionChart> _coll;
	
	public ChartManager () {
		// Commumity
		_comm = new ArrayList<AbstractCommunityChart>();
		_comm.add(new pt.keep.dspace.chart.community.NumberOfDocumentsChart());
		_comm.add(new pt.keep.dspace.chart.community.NumberOfDepositsChart());
		_comm.add(new pt.keep.dspace.chart.community.DocumentTypeChart());
		_comm.add(new pt.keep.dspace.chart.community.DocumentAccessChart());
		_comm.add(new pt.keep.dspace.chart.community.TopDownloadedViewedItemsChart());
		_comm.add(new pt.keep.dspace.chart.community.TopDownloadViewItemAuthorChart());
		
		_coll = new ArrayList<AbstractCollectionChart>();
		_coll.add(new pt.keep.dspace.chart.collection.NumberOfDocumentsChart());
		_coll.add(new pt.keep.dspace.chart.collection.NumberOfDepositsChart());
		_coll.add(new pt.keep.dspace.chart.collection.DocumentTypeChart());
		_coll.add(new pt.keep.dspace.chart.collection.DocumentAccessChart());
		_coll.add(new pt.keep.dspace.chart.collection.TopDownloadedViewedItemsChart());
		_coll.add(new pt.keep.dspace.chart.collection.TopDownloadViewItemAuthorChart());
	}
	

	public String containers (PageContext page, Collection comm) {
		if (!ConfigurationManager.getBooleanProperty("stats.chart.collection")) return "";
		try {
			int maxColumns = ConfigurationManager.getIntProperty("stats.chart.collection.columns", 3);
			int globali = 0;
			String result = "<table class=\"graphs\">";
			while (globali < _coll.size()) {
				result += "<tr>";
				int i = 0;
				for (i=0;i<maxColumns && globali + i < _coll.size();i++) {
					result += "<td ";
					if (globali + i + 1 == _coll.size())
						result += "colspan=\""+(maxColumns - i)+"\"";
					result += " class=\"label\">" + LocaleSupport.getLocalizedMessage(page, "stats.chart."+_coll.get(globali+i).chartID()) + "</td>";
				}
				result += "</tr>";
				result += "<tr>";
				for (i=0;i<maxColumns && globali + i < _coll.size();i++) {
					result += "<td ";
					if (globali + i + 1 == _coll.size())
						result += "colspan=\""+(maxColumns - i)+"\"";
					result += " class=\"chart\">"+_coll.get(globali+i).generateContainer(comm, page)+"</td>";
				}
				result += "</tr>";
				globali += i;
			}
			result += "</table>";
			return result;
		} catch (Throwable t) {
			log.debug(t.getMessage(), t);
			return "";
		}
	}

	public String script (PageContext page, Collection comm) {
		if (!ConfigurationManager.getBooleanProperty("stats.chart.collection")) return "";
		try {
			String result = "<script type=\"text/javascript\">$(function () {";
			for (AbstractCollectionChart chart : _coll) 
				result += chart.generateGraph(comm, page);
			result += "});</script>";
			return result;
		} catch (Throwable t) {
			log.debug(t.getMessage(), t);
			return "";
		}
	}
	public String containers (PageContext page, Community comm) {
		if (!ConfigurationManager.getBooleanProperty("stats.chart.community")) return "";
		try {
			int maxColumns = ConfigurationManager.getIntProperty("stats.chart.community.columns", 3);
			int globali = 0;
			String result = "<table class=\"graphs\">";
			while (globali < _comm.size()) {
				result += "<tr>";
				int i = 0;
				for (i=0;i<maxColumns && globali + i < _comm.size();i++) {
					result += "<td ";
					if (globali + i + 1 == _comm.size())
						result += "colspan=\""+(maxColumns - i)+"\"";
					result += " class=\"label\">" + LocaleSupport.getLocalizedMessage(page, "stats.chart."+_comm.get(globali+i).chartID()) + "</td>";
				}
				result += "</tr>";
				result += "<tr>";
				for (i=0;i<maxColumns && globali + i < _comm.size();i++) {
					result += "<td ";
					if (globali + i + 1 == _comm.size())
						result += "colspan=\""+(maxColumns - i)+"\"";
					result += " class=\"chart\">"+_comm.get(globali+i).generateContainer(comm, page)+"</td>";
				}
				result += "</tr>";
				globali += i;
			}
			result += "</table>";
			return result;
		} catch (Throwable t) {
			log.debug(t.getMessage(), t);
			return "";
		}
	}
	
	public String script (PageContext page, Community comm) {
		if (!ConfigurationManager.getBooleanProperty("stats.chart.community")) return "";
		try {
			String result = "<script type=\"text/javascript\">$(function () {";
			for (AbstractCommunityChart chart : _comm) 
				result += chart.generateGraph(comm, page);
			result += "});</script>";
			return result;
		} catch (Throwable t) {
			log.debug(t.getMessage(), t);
			return "";
		}
	}
}
