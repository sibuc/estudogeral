package pt.keep.dspace.chart.community;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class NumberOfDocumentsChart extends AbstractCommunityChart {
    private static Logger log = Logger.getLogger(NumberOfDocumentsChart.class);
	private class NODData {
		public int count;
		public int year;
		public int month;
	}
	
	private String query (Community c) {
		try {
			return "SELECT COUNT(t.item_id) as cnt, substring(t.datea from 1 for 4) as yeara, substring(t.datea from 6 for 2) as montha "+
				"FROM (SELECT i.item_id, (SELECT m.text_value FROM metadatavalue m WHERE m.metadata_field_id="+ReportDatabaseManager.getFieldID(super.getContext(),"dc.date.available")+" AND m.item_id=i.item_id) as datea "+ 
				"FROM item i " +
				"WHERE (" +
				"(i.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+StringUtils.join(ReportDatabaseManager.getAllSubCollections(super.getContext(),c.getID()).listIterator(),',')+")))" +
				")) t "+
				"WHERE t.datea <> '' GROUP BY yeara, montha ORDER BY yeara ASC, montha ASC";
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
			return "";
		} catch (ReportConfigurationException e) {
			log.debug(e.getMessage(), e);
			return "";
		}
	}
	
	private List<NODData> retrieve (Community c) {
		List<NODData> result = new ArrayList<NODData>();
		try {
			String query = this.query(c);
			log.debug("Query: "+query);
			int sum = 0;
			TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
			while (iterator.hasNext()) {
				TableRow row = iterator.next();
				NODData nod = new NODData();
				sum += (int) row.getLongColumn("cnt"); //rs.getInt("cnt");
				nod.count = sum;
				nod.year = Integer.parseInt(row.getStringColumn("yeara"));
				nod.month = Integer.parseInt(row.getStringColumn("montha"));
				result.add(nod);
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		return result;
	}
	
	private String getData (String varname, Community c) {
		List<NODData> list = this.retrieve(c);
		List<String> join = new ArrayList<String>();
		for (NODData nod : list) {
			Calendar cal = Calendar.getInstance();
			cal.set(nod.year, nod.month, 1);
			join.add("["+cal.getTimeInMillis()+","+nod.count+"]");
		}
		return "var "+varname+" = ["+StringUtils.join(join.listIterator(), ",") + "];";
	}
	
	private String getOperations (String varname) {
		return "$.plot($(\"#"+this.chartID()+"\"), ["+varname+"], { xaxis: { mode: \"time\" } });";
	}
	
	@Override
	public String chartID() {
		return "nodcommgraph";
	}

	@Override
	public String generateGraph(Community c, PageContext page) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(out);
		String varname = "data"+chartID();
		print.println(this.getData(varname, c));
		print.println(this.getOperations(varname));
		return out.toString();
	}
	@Override
	public String generateContainer(Community c, PageContext page) {
		return "<div id=\""+chartID()+"\" style=\"width:"+getWidth()+"px;height:"+getHeight()+"px;\"></div>";
	}
}
