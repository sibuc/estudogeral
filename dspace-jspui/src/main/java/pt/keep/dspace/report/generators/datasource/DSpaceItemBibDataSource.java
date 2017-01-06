package pt.keep.dspace.report.generators.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.content.ItemIterator;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRowIterator;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.field.Field;
import pt.keep.dspace.report.rdbms.ReportDatabaseManager;

public class DSpaceItemBibDataSource {
    private static Logger log = Logger.getLogger(DSpaceItemBibDataSource.class);
	
	public static ItemIterator query (Context ct, ExportEntity entity, String entityID, List<Field> fields, String order) throws ReportGenerationException {
		// build query
		try {
			String query = "SELECT i.*";
			// SELECT FIELDS
			/*for (Field f : fields) {
				if (f.isShown())
					query += ", "+f.getDatabaseAdapter().generateSelect("i");
				
				if (f.isGroupChosen())
					query += ", "+f.getDatabaseAdapter().generateSelect("i", "group");
			}*/
			query += " FROM item i ";
			// WHERE => entity
			boolean whereStarted = false;
			switch (entity) {
				case AUTHOR:
					query += " WHERE (i.item_id IN (SELECT tmp.item_id FROM metadatavalue tmp WHERE tmp.metadata_field_id="+ ReportDatabaseManager.getFieldID(ct, "dc", "contributor", "author")
								+" AND tmp.text_value like '"+entityID+"'))";
					whereStarted = true;
					break;
				case COLLECTION:
					query += " WHERE ( i.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id="+entityID+") OR ( i.owning_collection="+entityID+"))";
					whereStarted = true;
					break;
				case COMMUNITY:
					query += " WHERE ( i.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id IN ("+(String) StringUtils.join(ReportDatabaseManager.getAllSubCollections(ct, Integer.parseInt(entityID)).iterator(), ",")+")) )";
					whereStarted = true;
					break;
			}
			// WHERE FILTERS
			for (Field f : fields) {
				if (f.isFiltering()) {
					if (whereStarted) query += " AND ";
					else {
						query += " WHERE ";
						whereStarted = true;
					}
					query += "(" + f.getDatabaseAdapter().generateWhere("i", f.getFilterValue()) + ")";
				}
			}
			// ORDER
			boolean orderStarted = false;
			for (Field f : fields) {
				if (f.isGroupChosen()) {
					if (!orderStarted) query += " ORDER BY ";
					else query += ", ";
					orderStarted = true;
					query += f.getID() + "group ASC ";
				}
			}
			for (Field f : fields) {
				if (f.isOrderChosen() && f.isShown()) {
					if (!orderStarted) query += " ORDER BY ";
					else query += ", ";
					orderStarted = true;
					query += f.getID() + " " + order.toUpperCase();
				}
			}
			log.debug("SQL QUERY: "+query);
			try {
				TableRowIterator iterator = DatabaseManager.query(ct, query);
				return new ItemIterator(ct, iterator);
			} catch (SQLException e) {
				throw new ReportGenerationException(e);
			}
			
		} catch (ReportConfigurationException e) {
			throw new ReportGenerationException(e);
		} catch (SQLException e) {
			throw new ReportGenerationException(e);
		}
	}
}
