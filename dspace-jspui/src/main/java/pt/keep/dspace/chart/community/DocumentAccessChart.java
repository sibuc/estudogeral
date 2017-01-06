package pt.keep.dspace.chart.community;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class DocumentAccessChart extends AbstractCommunityChart {
	private class Data {
		public int count;
		public String access;
	}
	private static String sanitize (String param) {
		return param.toLowerCase().replaceAll("[^a-z0-9]", "");
		//.replaceAll("1", "one").replaceAll("2", "two").replaceAll("3", "three").replaceAll("4", "four").replaceAll("5", "five").replaceAll("6", "six").replaceAll("7", "seven").replaceAll("8", "eight").replaceAll("9", "nine").replaceAll("0", "zero");
	}

	
    private static Logger log = Logger.getLogger(DocumentAccessChart.class);
	private String query (Community c) {
		try {
			return "SELECT COUNT(t.item_id) as cnt, t.typea as typea "+
			"FROM (SELECT i.item_id, (SELECT m.text_value FROM metadatavalue m WHERE m.metadata_field_id="+ReportDatabaseManager.getFieldID(super.getContext(),"dc.rights")+" AND m.item_id=i.item_id) as typea "+ 
			"FROM item i " +
			"WHERE (" +
			"(i.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+StringUtils.join(ReportDatabaseManager.getAllSubCollections(super.getContext(),c.getID()).listIterator(),',')+")))" +
			")) t "+
			"WHERE t.typea <> '' GROUP BY typea";
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		} catch (ReportConfigurationException e) {
			log.debug(e.getMessage(), e);
		}
		return "";
	}
	

	private List<Data> retrieve (Community c) {
		List<Data> result = new ArrayList<Data>();
		try {
			String query = this.query(c);
			log.debug("[QUERY]: "+query);
			TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
			while (iterator.hasNext()) {
				TableRow row = iterator.next();
				Data nod = new Data();
				nod.count = (int) row.getLongColumn("cnt");//rs.getInt("cnt");
				nod.access = sanitize(row.getStringColumn("typea")); //rs.getString("typea");
				result.add(nod);
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		return result;
	}
	

	private String getData (String varname, Community c, PageContext page) {
		List<Data> list = this.retrieve(c);
		List<String> join = new ArrayList<String>();
		for (Data nod : list) {
			String t = LocaleSupport.getLocalizedMessage(page, "stats.reporting.field.access."+nod.access);
			if (t.contains("???"))
				join.add("{ label: \""+nod.access+"\", data: "+nod.count+" }");
			else
				join.add("{ label: \""+t+"\", data: "+nod.count+" }");
		}
		return "var "+varname+" = ["+StringUtils.join(join.listIterator(), ",") + "];";
	}
	
	private String getOperations (String varname) {
		return "$.plot($(\"#"+this.chartID()+"\"), "+varname+", { " +
					"series: {	" +
						"pie: { " +
							"show: true, " +
							"radius: 1, "+
							/*"label: { " +
								"show: true, " +
								"radius: 1, " +
								"formatter: function(label, series){ "+
									"return '<div style=\"font-size:8pt;text-align:center;padding:2px;color:white;\">'+label+'<br/>'+Math.round(series.percent)+'%</div>'; "+
								"}, " +
								"background: { opacity: 0.8 } " +
							"} " +*/
						"}" +
					"}"+ //", " +
					// "legend: { show: false }" +
				"});";
	}
	
	@Override
	public String generateGraph(Community c, PageContext page) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(out);
		String varname = "data"+chartID();
		print.println(this.getData(varname, c, page));
		print.println(this.getOperations(varname));
		return out.toString();
	}

	@Override
	public String chartID() {
		return "dacomm";
	}

	@Override
	public String generateContainer(Community c, PageContext page) {
		return "<div id=\""+chartID()+"\" style=\"width:"+getWidth()+"px;height:"+getHeight()+"px;\"></div>";
	}
}
