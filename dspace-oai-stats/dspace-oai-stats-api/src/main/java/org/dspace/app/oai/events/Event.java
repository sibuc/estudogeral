package org.dspace.app.oai.events;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dspace.app.oai.DSpaceOAICatalog;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public abstract class Event {
	private static Logger logger = Logger.getLogger(Event.class);

	private String table;
	private Context dspaceContext;
	private boolean partialEventFromSelect;

	public static final String SCHEMA = "stats";
	public static final Pattern datePattern = Pattern.compile("([0-9\\-]+)T([0-9]+:[0-9]+:[0-9]+)Z");
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final SimpleDateFormat simpleDateFormatUntilDay = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat simpleDateFormatFromHourOn = new SimpleDateFormat("HH:mm:ss");
	public static final String salt = "iamjustasalt";
	public static final String IP_PREFIX = "data:,";
	public static final String SESSION_PREFIX = "session:,";

	public Event(String table) {
		this.table = table;
	}

	public Context getDspaceContext() {
		return dspaceContext;
	}

	public boolean isPartialEventFromSelect() {
		return partialEventFromSelect;
	}

	public void setPartialEventFromSelect(boolean partialEventFromSelect) {
		this.partialEventFromSelect = partialEventFromSelect;
	}

	public void setDspaceContext(Context dspaceContext) {
		this.dspaceContext = dspaceContext;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public static ItemIterator findByMetadataFieldDate(Context context, String schema, String element, String qualifier, String from, String until,
			int limit, int offset) throws SQLException, AuthorizeException, IOException {
		MetadataSchema mds = MetadataSchema.find(context, schema);
		if (mds == null)
			throw new IllegalArgumentException("No such metadata schema: " + schema);
		MetadataField mdf = MetadataField.findByElement(context, mds.getSchemaID(), element, qualifier);
		if (mdf == null)
			throw new IllegalArgumentException("No such metadata field: schema=" + schema + ", element=" + element + ", qualifier=" + qualifier);

		String query = "SELECT item.* FROM metadatavalue,item WHERE item.in_archive='1' "
				+ "AND item.item_id = metadatavalue.item_id AND metadata_field_id = ?";
		TableRowIterator rows = null;
		if (from != null) {
			query += " AND metadatavalue.text_value >= '" + from + "'";
		}
		if (until != null) {
			query += " AND metadatavalue.text_value <= '" + until + "'";
		}
		query += " limit " + limit + " offset " + offset;
		rows = DatabaseManager.queryTable(context, "item", query, mdf.getFieldID());
		return new ItemIterator(context, rows);
	}

	public static List<Object> getEvents(Context context, String from, String until, int previous, String event, int offset) {
		List<Object> result = new ArrayList<Object>();
		int nextPrevious = 0, i = 0;
		String eventToProcess = null;
		boolean exit = false;
		ArrayList<Event> events = new ArrayList<Event>();
		String fromDate = null, fromTime = null, untilDate = null, untilTime = null;
		try {
			if (!from.equals("0001-01-01T00:00:00Z")) {
				Matcher mat = datePattern.matcher(from);
				if (mat.matches()) {
					fromDate = mat.group(1);
					fromTime = mat.group(2);
				}
			}
			if (!until.equals("9999-12-31T23:59:59Z")) {
				Matcher mat = datePattern.matcher(until);
				if (mat.matches()) {
					untilDate = mat.group(1);
					untilTime = mat.group(2);
				}
			}
		} catch (Throwable e) {
			logger.error(e);
		}
		if (previous == 0 && event == null) {
			String sql = "SELECT stats.download.download_id as id, stats.download.date as date, stats.download.time as time, text 'download' as table,"
					+ " stats.download.bitstream_id,stats.download.item_id,stats.download.ip,stats.download.country_code, stats.download.session_id from stats.download where (stats.download.spider = '0'";
			if (fromDate != null && fromTime != null) {
				sql += " and ((stats.download.date >= (DATE '" + fromDate + "') and stats.download.time >= (TIME '" + fromTime
						+ "')) or (stats.download.date > (DATE '" + fromDate + "')))";
			}
			if (untilDate != null && untilTime != null) {
				sql += " and ((stats.download.date <= (DATE '" + untilDate + "') and stats.download.time <= (TIME '" + untilTime
						+ "')) or (stats.download.date < (DATE '" + untilDate + "')))";
			}
			sql += ")";
			sql += " union select stats.view.view_id as id,stats.view.date as date,stats.view.time as time,text 'view' as table,"
					+ " -1 as dumb,stats.view.item_id,stats.view.ip,stats.view.country_code,stats.view.session_id FROM stats.view where (stats.view.spider = '0'";
			if (fromDate != null && fromTime != null) {
				sql += " and ((stats.view.date >= (DATE '" + fromDate + "') and stats.view.time >= (TIME '" + fromTime
						+ "')) or (stats.view.date > (DATE '" + fromDate + "')))";
			}
			if (untilDate != null && untilTime != null) {
				sql += " and ((stats.view.date <= (DATE '" + untilDate + "') and stats.view.time <= (TIME '" + untilTime
						+ "')) or (stats.view.date < (DATE '" + untilDate + "')))";
			}
			sql += ")";
			sql += " limit " + DSpaceOAICatalog.MAX_RECORDS;
			if (offset != 0) {
				sql += " offset " + offset;
			}
			try {
				TableRowIterator tableRowIterator = DatabaseManager.query(context, sql, new Object[] {});
				TableRow tableRow = null;
				String table = null;
				while (tableRowIterator.hasNext()) {
					tableRow = tableRowIterator.next();
					table = tableRow.getStringColumn("table");
					if (table.equals("view")) {
						events.add(new View(tableRow, true, context));
					} else if (table.equals("download")) {
						events.add(new Download(tableRow, true, context));
					}
				}
			} catch (SQLException e) {
				// Nothing here - return iterator from empty array
			}
		}
		if (events.size() != DSpaceOAICatalog.MAX_RECORDS) {
			int amountOfEventsToObtain = DSpaceOAICatalog.MAX_RECORDS - events.size();
			int eventOffset = previous;
			if (event != null) {
				eventToProcess = event;
			} else {
				eventToProcess = "deposit";
			}
			while (!exit) {
				try {
					if (eventToProcess.equals("deposit") || eventToProcess.equals("publication")) {
						Item item = null;
						ItemIterator itemIterator = findByMetadataFieldDate(context, "dc", "date", (eventToProcess.equals("deposit") ? "available"
								: "issued"), from, until, amountOfEventsToObtain, eventOffset);
						while (itemIterator.hasNext()) {
							item = itemIterator.next();
							if (eventToProcess.equals("deposit")) {
								events.add(new Deposit(item));
							} else {
								events.add(new Publication(item));
							}
							i++;
						}
						if (amountOfEventsToObtain == i) {
							exit = true;
							nextPrevious = previous + i;
						} else {
							if (eventToProcess.equals("deposit")) {
								eventToProcess = "publication";
							} else {
								eventToProcess = "itemCount";
							}
							amountOfEventsToObtain -= i;
							i = 0;
							eventOffset = 0;
							nextPrevious = 0;
							previous = 0;
						}
					} else {
						if (amountOfEventsToObtain > 0) {
							events.add(new ItemCount("itemCount", context));
						}
						exit = true;
					}
				} catch (SQLException e) {
					logger.error(e);
				} catch (AuthorizeException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		result.add(events.iterator());
		result.add(nextPrevious);
		result.add(eventToProcess);
		return result;
	}

	public static Event getEvent(Context context, String table, String identifier) {
		Event event = null;
		String handlePrefix = ConfigurationManager.getProperty("handle.canonical.prefix");
		if (handlePrefix == null) {
			handlePrefix = "http://hdl.handle.net/";
		}
		if (table.equals("deposit") || table.equals("publication")) {
			try {
				ItemIterator itemIterator = Item.findByMetadataField(context, "dc", "identifier", "uri", handlePrefix + identifier);
				if (itemIterator.hasNext()) {
					if (table.equals("deposit")) {
						event = new Deposit(itemIterator.next());
					} else if (table.equals("publication")) {
						event = new Publication(itemIterator.next());
					}
					event.setDspaceContext(context);
				}
			} catch (SQLException e) {
				logger.error(e);
			} catch (AuthorizeException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
		} else if (table.equals("itemCount")) {
			event = new ItemCount("itemCount", context);
		} else {
			try {
				TableRow resultRow = DatabaseManager.find(context, SCHEMA + "." + table, Integer.parseInt(identifier));
				if (table.equals("view")) {
					if (resultRow != null) {
						event = new View(resultRow);
						event.setDspaceContext(context);
					}
				} else if (table.equals("download")) {
					if (resultRow != null) {
						event = new Download(resultRow);
						event.setDspaceContext(context);
					}
				}
			} catch (SQLException e) {
				// Nothing here - return null event
			}
		}
		return event;
	}

	public abstract String serialize2XML();
}
