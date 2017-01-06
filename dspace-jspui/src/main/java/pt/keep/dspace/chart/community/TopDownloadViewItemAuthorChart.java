package pt.keep.dspace.chart.community;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Community;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class TopDownloadViewItemAuthorChart extends AbstractCommunityChart {
    private static Logger log = Logger.getLogger(TopDownloadViewItemAuthorChart.class);
	private class Data {
		public int count;
		public String author;
	}
	
	
	public List<Data> getDownloadedData (Community c) throws SQLException {
		String query;
			
			query = "SELECT SUM(dc.count) as cnt, d.author " +
			"FROM stats.author_item d, stats.download_count dc " +
			"WHERE " +
				"(d.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+StringUtils.join(ReportDatabaseManager.getAllSubCollections(super.getContext(),c.getID()).listIterator(),',')+"))) AND " +
				"d.item_id=dc.item_id " +
			"GROUP BY d.author " +
			"ORDER BY cnt DESC " +
			"LIMIT 10";
			
			TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
			List<Data> data = new ArrayList<Data>();
			while (iterator.hasNext()) {
				Data d = new Data();
				TableRow row = iterator.next();
				d.count = (int) row.getLongColumn("cnt");
				d.author = row.getStringColumn("author");
				data.add(d);
			}
			return data;
	}
	
	public List<Data> getViewedData (Community c) throws SQLException {
		String query;
			query = "SELECT SUM(dc.count) as cnt, d.author " +
			"FROM stats.author_item d, stats.view_count dc " +
			"WHERE " +
				"(d.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+StringUtils.join(ReportDatabaseManager.getAllSubCollections(super.getContext(),c.getID()).listIterator(),',')+"))) AND " +
				"d.item_id=dc.item_id " +
			"GROUP BY d.author " +
			"ORDER BY cnt DESC " +
			"LIMIT 10";
			
			TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
			List<Data> data = new ArrayList<Data>();
			while (iterator.hasNext()) {
				Data d = new Data();
				TableRow row = iterator.next();
				d.count = (int) row.getLongColumn("cnt");
				d.author = row.getStringColumn("author");
				data.add(d);
			}
			return data;
	}
	
	public List<Data> getItemData (Community c) throws SQLException {
		String query;
			query = "SELECT COUNT(d.item_id) as cnt, d.author " +
			"FROM stats.author_item d " +
			"WHERE (d.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+StringUtils.join(ReportDatabaseManager.getAllSubCollections(super.getContext(),c.getID()).listIterator(),',')+"))) " +
			"GROUP BY d.author " +
			"ORDER BY cnt DESC LIMIT 10";
			
			TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
			List<Data> data = new ArrayList<Data>();
			while (iterator.hasNext()) {
				Data d = new Data();
				TableRow row = iterator.next();
				d.count = (int) row.getLongColumn("cnt");
				d.author = row.getStringColumn("author");
				data.add(d);
			}
			return data;
	}
	
	@Override
	public String generateGraph(Community c, PageContext page) {
		return 
				"$('#"+chartID()+"tabs').tabs(); ";
	}
	
	
	public String getTitle (Item item) throws SQLException {
		Metadatum[] values = item.getMetadata("dc", "title", Item.ANY, Item.ANY);
		if (values.length > 0) return values[0].value;
		else return "";
	}
	
	@Override
	public String generateContainer(Community c, PageContext page) {
		int max = ConfigurationManager.getIntProperty("stats.chart.community.tables.maxchars", 30);
		String result = "<div  id=\""+chartID()+"tabs\" style=\"width: "+this.getWidth()+"px;\"><ul>" +
				"<li><a id=\""+chartID()+"btni\" href=\"#"+chartID()+"ti\">"+
				LocaleSupport.getLocalizedMessage(page, "stats.chart.item")
				+"</a></li> " +
				"<li><a id=\""+chartID()+"btnv\" href=\"#"+chartID()+"tv\">"+
					LocaleSupport.getLocalizedMessage(page, "stats.chart.views")
				+"</a></li> " +
				"<li><a id=\""+chartID()+"btnd\" href=\"#"+chartID()+"td\">"+
					LocaleSupport.getLocalizedMessage(page, "stats.chart.downloads")
				+"</a></li> "+
		"</ul>";
		result += "<div id=\""+chartID()+"ti\"><table id=\""+chartID()+"i\">";
		int p = 1;
		try {
			p = 1;
			List<Data> data = this.getItemData(c);
			for (Data d : data) {
				String title = d.author;
				result += "<tr>" +
				"<td>"+p+"</td>"+
				"<td style=\"width: "+(Integer.parseInt(this.getWidth())-100)+"px\" class=\"value\"><a title=\""+title+"\" href=\""+((HttpServletRequest)page.getRequest()).getContextPath()+"/browse?type=author&value="+URLEncoder.encode(title, "UTF-8")+"\">"+StringUtils.abbreviate(title,max) +"</a></td>"+
				"<td>"+d.count+"</td>"+
				"</tr>";
				p++;
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage(), e);
		}
		result += "</table></div>";
		result += "<div id=\""+chartID()+"tv\"><table id=\""+chartID()+"v\">";
		try {
			p = 1;
			List<Data> data = this.getViewedData(c);
			for (Data d : data) {
				String title = d.author;
				result += "<tr>" +
				"<td>"+p+"</td>"+
				"<td style=\"width: "+(Integer.parseInt(this.getWidth())-100)+"px\" class=\"value\"><a title=\""+title+"\" href=\""+((HttpServletRequest)page.getRequest()).getContextPath()+"/browse?type=author&value="+URLEncoder.encode(title, "UTF-8")+"\">"+StringUtils.abbreviate(title,max) +"</a></td>"+
				"<td>"+d.count+"</td>"+
				"</tr>";
				p++;
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage(), e);
		}
		result += "</table></div>";
		result += "<div id=\""+chartID()+"td\"><table id=\""+chartID()+"d\">";
		try {
			p = 1;
			List<Data> data = this.getDownloadedData(c);
			for (Data d : data) {
				String title = d.author;
				result += "<tr>" +
				"<td>"+p+"</td>"+
				"<td style=\"width: "+(Integer.parseInt(this.getWidth())-100)+"px\" class=\"value\"><a title=\""+title+"\" href=\""+((HttpServletRequest)page.getRequest()).getContextPath()+"/browse?type=author&value="+URLEncoder.encode(title, "UTF-8")+"\">"+StringUtils.abbreviate(title,max) +"</a></td>"+
				"<td>"+d.count+"</td>"+
				"</tr>";
				p++;
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage(), e);
		}
		result += "</table></div></div>";
		return result;
	}
	@Override
	public String chartID() {
		return "topviewdownitemauthorcomm";
	}

}
