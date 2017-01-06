package pt.keep.dspace.report.rdbms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;

public class ReportDatabaseManager {
    private static Logger log = Logger.getLogger(ReportDatabaseManager.class);
	private static Map<String, Integer> metadataValues = null;
	
	public static Integer getFieldID (Context ct, String field) throws SQLException, ReportConfigurationException {
		if (field == null) throw new ReportConfigurationException("DC field invalid: "+field);
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new ReportConfigurationException("DC field invalid: "+field);
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

	public static List<String> getDistinctValues (Context ct, String schema, String element, String qualifier, int length) throws SQLException {
		List<String> values = new ArrayList<String>();
		//Connection conn = null;
		//conn = DatabaseManager.getConnection();
		//Statement st = conn.createStatement();
		//ResultSet rs = st.executeQuery();
		TableRowIterator iterator = DatabaseManager.query(ct, "SELECT DISTINCT substring(text_value from 0 to "+length+") as value FROM metadatavalue WHERE metadata_field_id="+getFieldID(ct, schema, element, qualifier)+" ORDER BY value ASC");
		while (iterator.hasNext()) {
			values.add(iterator.next().getStringColumn("value"));
		}
		//rs.close();
		//DatabaseManager.freeConnection(conn);
		return values;
	}
	
	public static List<String> getDistinctValues (Context ct, String field, int length) throws SQLException, ReportConfigurationException {
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new ReportConfigurationException("DC field invalid: "+field);
		if (parts.length == 2) return getDistinctValues(ct, parts[0],parts[1], null, length);
		return getDistinctValues(ct, parts[0],parts[1], parts[2], length);
	}
	

	public static List<String> getDistinctValues (Context ct, String schema, String element, String qualifier, ExportEntity entity, Object data) throws SQLException {
		List<String> values = new ArrayList<String>();
		//Connection conn = null;
		//conn = DatabaseManager.getConnection();
		//Statement st = conn.createStatement();
		String query = "SELECT DISTINCT m.text_value as value FROM metadatavalue m WHERE m.metadata_field_id="+getFieldID(ct, schema, element, qualifier);
		switch (entity) {
			case AUTHOR:
				query += " AND EXISTS (SELECT tmp.* FROM metadatavalue tmp WHERE tmp.item_id=m.item_id AND tmp.metadata_field_id='"+getFieldID(ct, "dc", "contributor", "author")+"' AND tmp.text_value LIKE '"+((String)data)+"') ";
				break;
			case COLLECTION:
				query += " AND (";
				query += "(m.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id="+(String) data+")) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM item tmp WHERE tmp.owning_collection="+(String) data+"))";
				query += ")";
				break;
			case COMMUNITY:
				query += " AND (";
				query += "(m.item_id IN (SELECT tmp.item_id FROM community2item tmp WHERE community_id="+(String) data+")) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id IN ("+(String) StringUtils.join(getAllSubCollections(ct, Integer.parseInt((String) data)).toArray(), ",")+"))) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM item tmp WHERE tmp.owning_collection IN ("+(String) StringUtils.join(getAllSubCollections(ct, Integer.parseInt((String) data)).toArray(), ",")+")))";
				query += ")";
				break;
		}
		query+=" ORDER BY value ASC";
		log.debug("Query: "+query);
		TableRowIterator iterator = DatabaseManager.query(ct, query);
		while (iterator.hasNext()) {
				values.add(iterator.next().getStringColumn("value"));
		}
		return values;
	}
	

	public static List<String> getDistinctValues (Context ct, String schema, String element, String qualifier, ExportEntity entity, Object data, int length) throws SQLException {
		List<String> values = new ArrayList<String>();
		String query = "SELECT DISTINCT substring(m.text_value from 1 for "+length+") as value FROM metadatavalue m WHERE m.metadata_field_id="+getFieldID(ct, schema, element, qualifier);
		switch (entity) {
			case AUTHOR:
				query += " AND EXISTS (SELECT tmp.* FROM metadatavalue tmp WHERE tmp.item_id=m.item_id AND tmp.metadata_field_id='"+getFieldID(ct, "dc", "contributor", "author")+"' AND tmp.text_value LIKE '"+((String)data)+"') ";
				break;
			case COLLECTION:
				query += " AND (";
				query += "(m.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id="+(String) data+")) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM item tmp WHERE tmp.owning_collection="+(String) data+"))";
				query += ")";
				break;
			case COMMUNITY:
				query += " AND (";
				query += "(m.item_id IN (SELECT tmp.item_id FROM community2item tmp WHERE community_id="+(String) data+")) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id IN ("+(String) StringUtils.join(getAllSubCollections(ct, Integer.parseInt((String) data)).toArray(), ",")+"))) OR ";
				query += "(m.item_id IN (SELECT tmp.item_id FROM item tmp WHERE tmp.owning_collection IN ("+(String) StringUtils.join(getAllSubCollections(ct, Integer.parseInt((String) data)).toArray(), ",")+")))";
				query += ")";
				break;
		}
		query +=" ORDER BY value ASC";
		log.debug("Query: "+query);
		TableRowIterator iterator = DatabaseManager.query(ct, query);
		while (iterator.hasNext()) {
				values.add(iterator.next().getStringColumn("value"));
		}
		return values;
	}
	
	public static List<String> getDistinctValues (Context ct, String schema, String element, String qualifier) throws SQLException {
		List<String> values = new ArrayList<String>();
		String query = "SELECT DISTINCT text_value as value FROM metadatavalue WHERE metadata_field_id="+getFieldID(ct, schema, element, qualifier);
		
		query+=" ORDER BY value ASC";
		TableRowIterator iterator = DatabaseManager.query(ct, query);
		while (iterator.hasNext()) {
				values.add(iterator.next().getStringColumn("value"));
		}
		return values;
	}

	
	public static List<String> getDistinctValues (Context ct, String field, ExportEntity entity, Object data, int length) throws SQLException, ReportConfigurationException {
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new ReportConfigurationException("DC field invalid: "+field);
		if (parts.length == 2) return getDistinctValues(ct, parts[0],parts[1], null, entity, data, length);
		return getDistinctValues(ct, parts[0],parts[1], parts[2], entity, data, length);
	}
	public static List<String> getDistinctValues (Context ct, String field, ExportEntity entity, Object data) throws SQLException, ReportConfigurationException {
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new ReportConfigurationException("DC field invalid: "+field);
		if (parts.length == 2) return getDistinctValues(ct, parts[0],parts[1], null, entity, data);
		return getDistinctValues(ct, parts[0],parts[1], parts[2], entity, data);
	}
	public static List<String> getDistinctValues (Context ct, String field) throws SQLException, ReportConfigurationException {
		String[] parts = field.split(Pattern.quote("."));
		if (parts.length < 2) throw new ReportConfigurationException("DC field invalid: "+field);
		if (parts.length == 2) return getDistinctValues(ct, parts[0],parts[1], null);
		return getDistinctValues(ct, parts[0],parts[1], parts[2]);
	}
	/*
	public static List<Community> getSubAllCommunities (Community c) throws SQLException {
		if (c == null) throw new NullArgumentException("c");
		List<Community> list = new ArrayList<Community>();
		list.add(c);
		for (Community s : c.getSubcommunities()) {
			list.addAll(getSubAllCommunities(s));
		}
		return list;
	}*/
	
	public static List<String> getAllSubCollections (Context ct, int i) throws SQLException {
		Queue<Community> comqueue = new LinkedList<Community>();
		List<String> list = new ArrayList<String>();
		comqueue.add(Community.find(ct, i));
		while (!comqueue.isEmpty()) {
			Community c = comqueue.poll();
			for (Community sub : c.getSubcommunities()) {
				comqueue.add(sub);
			}
			for (Collection col : c.getCollections())
				if (!list.contains(col))
					list.add(col.getID()+"");
		}
		return list;
	}
}
