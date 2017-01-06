package pt.keep.dspace.chart.collection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class TopDownloadedViewedItemsChart extends AbstractCollectionChart {
    private static Logger log = Logger.getLogger(TopDownloadedViewedItemsChart.class);
	private class Data {
		public int count;
		public int itemid;
	}
	
	
	public List<Data> getDownloadedData (Collection c) throws SQLException {
		String query = "SELECT d.count as cnt, d.item_id FROM stats.download_count d " +
		" WHERE (d.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+c.getID()+")))"+		
		" ORDER BY cnt DESC LIMIT 10";
		
		TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
		List<Data> data = new ArrayList<Data>();
		while (iterator.hasNext()) {
			Data d = new Data();
			TableRow row = iterator.next();
			d.count = (int) row.getLongColumn("cnt");
			d.itemid = row.getIntColumn("item_id");
			data.add(d);
		}
		return data;
	}
	public List<Data> getViewedData (Collection c) throws SQLException {
		String query = "SELECT d.count as cnt, d.item_id FROM stats.view_count d " +
		"WHERE (d.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE tmp.collection_id IN ("+c.getID()+")))"+		
		" ORDER BY cnt DESC LIMIT 10";
		
		TableRowIterator iterator = DatabaseManager.query(super.getContext(), query);
		List<Data> data = new ArrayList<Data>();
		while (iterator.hasNext()) {
			Data d = new Data();
			TableRow row = iterator.next();
			d.count = (int) row.getLongColumn("cnt");
			d.itemid = row.getIntColumn("item_id");
			data.add(d);
		}
		return data;
	}
	
	@Override
	public String generateGraph(Collection c, PageContext page) {
		return 
			"$('#"+chartID()+"tabs').tabs(); ";
	}
	
	public String getTitle (Item item) throws SQLException {
		Metadatum[] values = item.getMetadata("dc", "title", Item.ANY, Item.ANY);
		if (values.length > 0) return values[0].value;
		else return "";
	}
	
	@Override
	public String generateContainer(Collection c, PageContext page) {
		int max = ConfigurationManager.getIntProperty("stats.chart.collection.tables.maxchars", 30);
		String result = "<div  id=\""+chartID()+"tabs\" style=\"width: "+this.getWidth()+"px;\"><ul>" +
			"<li><a id=\""+chartID()+"btnv\" href=\"#"+chartID()+"tv\">"+
				LocaleSupport.getLocalizedMessage(page, "stats.chart.views")
			+"</a></li> " +
			"<li><a id=\""+chartID()+"btnd\" href=\"#"+chartID()+"td\">"+
				LocaleSupport.getLocalizedMessage(page, "stats.chart.downloads")
			+"</a></li>"+
		"</ul><div id=\""+chartID()+"td\"><table id=\""+chartID()+"d\">";
		int p = 1;
		try {
			List<Data> data = this.getDownloadedData(c);
			for (Data d : data) {
				Item item = Item.find(super.getContext(), d.itemid);
				String title = this.getTitle(item);
				result += "<tr>" +
				"<td>"+p+"</td>"+
				"<td style=\"width: "+(Integer.parseInt(this.getWidth())-100)+"px\" class=\"value\"><a title=\""+title+"\" href=\""+((HttpServletRequest)page.getRequest()).getContextPath()+"/handle/"+item.getHandle()+"\">"+StringUtils.abbreviate(title,max) +"</a></td>"+
				"<td>"+d.count+"</td>"+
				"</tr>";
				p++;
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		result += "</table></div>";
		result += "<div id=\""+chartID()+"tv\"><table id=\""+chartID()+"v\">";
		try {
			p = 1;
			List<Data> data = this.getViewedData(c);
			for (Data d : data) {
				Item item = Item.find(super.getContext(), d.itemid);
				String title = this.getTitle(item);
				result += "<tr>" +
				"<td>"+p+"</td>"+
				"<td style=\"width: "+(Integer.parseInt(this.getWidth())-100)+"px\" class=\"value\"><a title=\""+title+"\" href=\""+((HttpServletRequest)page.getRequest()).getContextPath()+"/handle/"+item.getHandle()+"\">"+StringUtils.abbreviate(title,max) +"</a></td>"+
				"<td>"+d.count+"</td>"+
				"</tr>";
				p++;
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		result += "</table></div></div>";
		return result;
	}

	@Override
	public String chartID() {
		return "topviewdowncoll";
	}

}
