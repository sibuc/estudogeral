package org.dspace.app.stats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;


public class DownloadViewCounterAgreggateManager {
	private class Data {
		public String author;
		public int itemid;
	}
	private class CounterIngestor<T> {
		private Map<T, Integer> _counters;
		public CounterIngestor () {
			_counters = new HashMap<T, Integer>();
		}
		
		public void add (T id, int value) {
			_counters.put(id, value);
		}
		
		public void ingest (String table, boolean reset) {
			for (T id : _counters.keySet()) {
				if (id instanceof Integer)
					this.insertOrUpdateIncrement(table, (Integer) id, _counters.get(id), reset);
				//else
				//	this.insertOrUpdateIncrement(table, (String) id, _counters.get(id), reset);
			}
		}
		

		private void insertOrUpdateIncrement(String table, int id, int value, boolean reset) {
			String query = "SELECT COUNT(count) as n FROM stats."+table+"_count WHERE item_id='"+id+"'";
			DownloadViewCounterAgreggateManager.log.debug("Counting "+id+" : "+value);
			try {
				if (DatabaseManager.query(_cont, query).next().getLongColumn("n") > 0) {
					// UPDATE
					if (!reset)
						DatabaseManager.updateQuery(_cont, "UPDATE stats."+table+"_count SET count=count+"+(value)+" WHERE item_id='"+id+"'");
					else
						DatabaseManager.updateQuery(_cont, "UPDATE stats."+table+"_count SET count='"+(value)+"' WHERE item_id='"+id+"'");
				} else {
					// INSERT
					DatabaseManager.updateQuery(_cont, "INSERT INTO stats."+table+"_count VALUES ('"+id+"','"+value+"')");
				}
			} catch (SQLException e) {
				DownloadViewCounterAgreggateManager.log.debug(e.getMessage(), e);
			}
		}
	}
	
    private static Logger log = Logger.getLogger(DownloadViewCounterAgreggateManager.class);
	private static Map<String, Integer> metadataValues = null;
	
	public static Integer getFieldID (Context ct, String field) throws SQLException {
		if (field == null) throw new NullArgumentException("DC field invalid: "+field);
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new NullArgumentException("DC field invalid: "+field);
		if (parts.length == 2) return getFieldID(ct, parts[0],parts[1], null);
		return getFieldID(ct, parts[0],parts[1], parts[2]);
	}
	
	public static Integer getFieldID (Context ct, String schema, String element, String qualifier) throws SQLException {
    	String q = "";
    	if (qualifier != null) q = "." + qualifier;
		if (metadataValues == null) metadataValues = new HashMap<String, Integer>();
		if (!metadataValues.containsKey(schema + "." + element + q)) {
			String query = "SELECT m.metadata_field_id as id FROM metadatafieldregistry m, metadataschemaregistry ms WHERE"
					+ " m.element='" + element + "'";
			if (qualifier == null)
				query += " AND m.qualifier is NULL ";
			else
				query += " AND m.qualifier='" + qualifier + "'";
			query += " AND m.metadata_schema_id=ms.metadata_schema_id AND ms.short_id='"
					+ schema + "'";

			TableRowIterator iterator = DatabaseManager.query(ct, query);
			if (iterator.hasNext()) {
				TableRow row = iterator.next();
				metadataValues.put(schema + "." + element + q,
						row.getIntColumn("id"));
			}
			if (!metadataValues.containsKey(schema + "." + element + q)) {
				metadataValues.put(schema + "." + element + q,
						new Integer(0));
			}
		}
		return metadataValues.get(schema + "." + element + q);
	}
    
    
	private Context _cont;
	private boolean _all;
	
	public DownloadViewCounterAgreggateManager (String c, boolean all) throws SQLException {
		this._all = all;
		this._cont = new Context();
		log.debug("Starting counters aggregation");
		
		if (c == null || c.toLowerCase().equals("all")) {
			CounterIngestor<Integer> down = this.aggregate("download");
			CounterIngestor<Integer> views = this.aggregate("view");
			//CounterIngestor<String> items = 
			this.aggregateItems();
			//CounterIngestor<String> aview = this.aggregateAuthor("view");
			//CounterIngestor<String> adown = this.aggregateAuthor("download");
			
			down.ingest("download", all);
			views.ingest("view", all);
			//items.ingest("item", all);
			//aview.ingest("view", all);
			//adown.ingest("download", all);
		} else if (c.toLowerCase().equals("down")) {
			CounterIngestor<Integer> down = this.aggregate("download");
			down.ingest("download", all);
		} else if (c.toLowerCase().equals("view")) {
			CounterIngestor<Integer> views = this.aggregate("view");
			views.ingest("view", all);
		} else if (c.toLowerCase().equals("item")) {
			this.aggregateItems();
		}
		
		_cont.complete();
	}
	
	private CounterIngestor<String> aggregateItems() {
		CounterIngestor<String> counter = new CounterIngestor<String>();
		try {
			List<Data> data = new ArrayList<Data>();
			String query = "SELECT i.item_id, m.text_value FROM item i, metadatavalue m WHERE m.item_id=i.item_id AND m.metadata_field_id="+getFieldID(_cont, "dc.contributor.author")+" AND i.item_id NOT IN (SELECT item_id FROM stats.author_item)";
				//SELECT COUNT(i.item_id) as cnt, m.text_value FROM item i, metadatavalue m WHERE i.item_id=m.item_id AND m.metadata_field_id="+getFieldID("dc.contributor.author")+" GROUP BY m.text_value HAVING NOT m.text_value ISNULL";
			TableRowIterator it;
			
				it = DatabaseManager.query(_cont, query);
				while (it.hasNext()) {
					TableRow row = it.next();
					log.debug("Aggretating author "+row.getStringColumn("text_value"));
					Data d = new Data();
					d.author = row.getStringColumn("text_value");
					d.itemid = row.getIntColumn("item_id");
					data.add(d);
				}
				
			for (Data d : data) {
				if (!DatabaseManager.query(_cont, "SELECT * FROM stats.author_item WHERE item_id=? AND author=?", d.itemid, d.author).hasNext())
					DatabaseManager.updateQuery(_cont, "INSERT INTO stats.author_item VALUES (?,?)", d.author, d.itemid);
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		return counter;
	}

	public CounterIngestor<Integer> aggregate(String table) {
		CounterIngestor<Integer> counter = new CounterIngestor<Integer>();
		String query = "SELECT COUNT(d."+table+"_id) as cnt, d.item_id as item_id FROM stats."+table+" d GROUP BY d.item_id HAVING NOT d.item_id ISNULL";
		if (!_all) query = "SELECT COUNT(d."+table+"_id) as cnt, d.item_id  as item_id FROM stats."+table+" d GROUP BY d.item_id HAVING NOT d.item_id ISNULL AND d.aggregated=FALSE";
		TableRowIterator it;
		try {
			it = DatabaseManager.query(_cont, query);
			while (it.hasNext()) {
				TableRow row = it.next();
				log.debug("Aggretating "+table+" for "+row.getIntColumn("item_id"));
				counter.add(row.getIntColumn("item_id"), (int) row.getLongColumn("cnt"));
			}
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		}
		return counter;
	}
	
	
}
