/*
 * DSpaceOAICatalog.java
 *
 * Version: $Revision: 4865 $
 *
 * Date: $Date: 2010-04-09 09:00:25 +0000 (Fri, 09 Apr 2010) $
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.oai;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dspace.app.oai.events.Event;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.BadArgumentException;
import ORG.oclc.oai.server.verb.BadResumptionTokenException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.IdDoesNotExistException;
import ORG.oclc.oai.server.verb.NoItemsMatchException;
import ORG.oclc.oai.server.verb.NoMetadataFormatsException;
import ORG.oclc.oai.server.verb.NoSetHierarchyException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;

/**
 * This is class extends OAICat's AbstractCatalog base class to allow metadata
 * harvesting of the metadata in DSpace via OAI-PMH 2.0.
 * 
 * FIXME: Some CNRI Handle-specific stuff in here. Anyone wanting to use
 * something else will need to update this code too. Sorry about that.
 * 
 * @author Robert Tansley
 * @version $Revision: 4865 $
 */
public class DSpaceOAICatalog extends AbstractCatalog {
	/** log4j logger */
	private static Logger log = Logger.getLogger(DSpaceOAICatalog.class);

	/** Prefix that all our OAI identifiers have */
	public final static String OAI_ID_PREFIX = "oai:" + ConfigurationManager.getProperty("dspace.hostname") + ":";

	/** Maximum number of records returned by one request */
	public final static int MAX_RECORDS = 1000;

	public DSpaceOAICatalog(Properties properties) {
		// Don't need to do anything
	}

	/**
	 * Retrieve a list of schemaLocation values associated with the specified
	 * identifier.
	 * 
	 * @param identifier
	 *            the OAI identifier
	 * @return a Vector containing schemaLocation Strings
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 * @exception IdDoesNotExistException
	 *                the specified identifier can't be found
	 * @exception NoMetadataFormatsException
	 *                the specified identifier was found but the item is flagged
	 *                as deleted and thus no schemaLocations (i.e.
	 *                metadataFormats) can be produced.
	 */
	public Vector<Object> getSchemaLocations(String identifier) throws OAIInternalServerError, IdDoesNotExistException, NoMetadataFormatsException {
		log.info(LogManager.getHeader(null, "[STATS] oai_request", "verb=getSchemaLocations,identifier="
				+ ((identifier == null) ? "null" : identifier)));

		Context context = null;
		Event event = null;
		try {
			context = new Context();
			if (identifier.startsWith(OAI_ID_PREFIX)) {

				String id = identifier.substring(OAI_ID_PREFIX.length());
				String[] identifierParts = id.split("_");
				if (identifierParts.length != 2) {
					throw new IdDoesNotExistException(identifier);
				}
				// get event now
				event = Event.getEvent(context, identifierParts[0], identifierParts[1]);
			}
		} catch (SQLException e) {
			log.warn(LogManager.getHeader(context, "database_error", ""), e);
			throw new OAIInternalServerError(e.toString());
		} finally {
			if (context != null) {
				context.abort();
			}
		}
		if (event != null) {
			return getRecordFactory().getSchemaLocations(event);
		} else {
			throw new IdDoesNotExistException(identifier);
		}
	}

	/**
	 * Retrieve a list of identifiers that satisfy the specified criteria
	 * 
	 * @param from
	 *            beginning date using the proper granularity
	 * @param until
	 *            ending date using the proper granularity
	 * @param set
	 *            the set name or null if no such limit is requested
	 * @param metadataPrefix
	 *            the OAI metadataPrefix or null if no such limit is requested
	 * @return a Map object containing entries for "headers" and "identifiers"
	 *         Iterators (both containing Strings) as well as an optional
	 *         "resumptionMap" Map. It may seem strange for the map to include
	 *         both "headers" and "identifiers" since the identifiers can be
	 *         obtained from the headers. This may be true, but
	 *         AbstractCatalog.listRecords() can operate quicker if it doesn't
	 *         need to parse identifiers from the XML headers itself. Better
	 *         still, do like I do below and override
	 *         AbstractCatalog.listRecords(). AbstractCatalog.listRecords() is
	 *         relatively inefficient because given the list of identifiers, it
	 *         must call getRecord() individually for each as it constructs its
	 *         response. It's much more efficient to construct the entire
	 *         response in one fell swoop by overriding listRecords() as I've
	 *         done here.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 * @exception NoSetHierarchyException
	 *                the repository doesn't support sets.
	 * @exception CannotDisseminateFormatException
	 *                the metadata format specified is not supported by your
	 *                repository.
	 */
	public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws OAIInternalServerError, NoSetHierarchyException,
			NoItemsMatchException, CannotDisseminateFormatException, BadArgumentException {
		log.info(LogManager.getHeader(null, "oai_request", "verb=listIdentifiers,from=" + ((from == null) ? "null" : from) + ",until="
				+ ((until == null) ? "null" : until) + ",set=" + ((set == null) ? "null" : set) + ",metadataPrefix="
				+ ((metadataPrefix == null) ? "null" : metadataPrefix)));

		Map m = doRecordHeaderHarvest(from, until, 0, null, set, metadataPrefix, 0);

		// Null means bad metadata prefix was bad
		if (m == null) {
			log.info(LogManager.getHeader(null, "oai_error", "cannot_disseminate_format"));
			throw new CannotDisseminateFormatException(metadataPrefix);
		}

		// If there were zero results, return the appropriate error
		Iterator i = (Iterator) m.get("identifiers");

		if ((i == null) || !i.hasNext()) {
			log.info(LogManager.getHeader(null, "oai_error", "no_items_match"));
			throw new NoItemsMatchException();
		}
		return m;
	}

	/**
	 * Retrieve the next set of identifiers associated with the resumptionToken
	 * 
	 * @param resumptionToken
	 *            implementation-dependent format taken from the previous
	 *            listIdentifiers() Map result.
	 * @return a Map object containing entries for "headers" and "identifiers"
	 *         Iterators (both containing Strings) as well as an optional
	 *         "resumptionMap" Map.
	 * @exception BadResumptionTokenException
	 *                the value of the resumptionToken is invalid or expired.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 */
	public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {

		log.info(LogManager.getHeader(null, "oai_request", "verb=listIdentifiers,resumptionToken=" + resumptionToken));

		/*
		 * FIXME: This may return zero records if the previous harvest returned
		 * a number of records that's an exact multiple of MAX_RECORDS. I hope
		 * that's OK.
		 */
		Object[] params = decodeResumptionToken(resumptionToken);
		Integer previsou = (Integer) params[2];
		Integer offset = (Integer) params[6];

		Map m = null;

		/*
		 * We catch BadArgumentExceptions here, because doRecordHarvest() throws
		 * BadArgumentExcpetions when the set spec is bad. set spec bad == bad
		 * resumption token.
		 */
		try {
			m = doRecordHeaderHarvest((String) params[0], (String) params[1], previsou.intValue(), (String) params[3], (String) params[4],
					(String) params[5], offset.intValue());
		} catch (BadArgumentException bae) {
			m = null;
		}

		// null result means a problem -> bad resumption token
		if (m == null) {
			log.info(LogManager.getHeader(null, "oai_error", "bad_resumption_token"));
			throw new BadResumptionTokenException();
		}
		return m;
	}

	/**
	 * Retrieve the specified metadata for the specified identifier
	 * 
	 * @param identifier
	 *            the OAI identifier
	 * @param metadataPrefix
	 *            the OAI metadataPrefix
	 * @return the <record/>portion of the XML response.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 * @exception CannotDisseminateFormatException
	 *                the metadataPrefix is not supported by the item.
	 * @exception IdDoesNotExistException
	 *                the identifier wasn't found
	 */
	public String getRecord(String identifier, String metadataPrefix) throws OAIInternalServerError, CannotDisseminateFormatException,
			IdDoesNotExistException {
		log.info(LogManager.getHeader(null, "[STATS] oai_request", "verb=getRecord,identifier=" + ((identifier == null) ? "null" : identifier)
				+ ",metadataPrefix=" + ((metadataPrefix == null) ? "null" : metadataPrefix)));
		Context context = null;
		String record = null;
		Event event = null;
		try {
			if (identifier.startsWith(OAI_ID_PREFIX)) {
				context = new Context();
				// TODO - verify if substring goes ok!
				String id = identifier.substring(OAI_ID_PREFIX.length());
				String[] identifierParts = id.split("_");
				if (identifierParts.length != 2) {
					throw new IdDoesNotExistException(identifier);
				}
				String schemaURL;
				if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null) {
					log.info(LogManager.getHeader(null, "[STATS] oai_error", "cannot_disseminate_format"));
					throw new CannotDisseminateFormatException(metadataPrefix);
				}
				event = Event.getEvent(context, identifierParts[0], identifierParts[1]);
				if (event == null)
					throw new IdDoesNotExistException("Couldn't create an event with id=" + identifier);
				record = getRecordFactory().create(event, schemaURL, metadataPrefix);
			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			if (context != null) {
				context.abort();
			}
		}
		return record;
	}

	/**
	 * Retrieve a list of records that satisfy the specified criteria. Note,
	 * though, that unlike the other OAI verb type methods implemented here,
	 * both of the listRecords methods are already implemented in
	 * AbstractCatalog rather than abstracted. This is because it is possible to
	 * implement ListRecords as a combination of ListIdentifiers and GetRecord
	 * combinations. Nevertheless, I suggest that you override both the
	 * AbstractCatalog.listRecords methods here since it will probably improve
	 * the performance if you create the response in one fell swoop rather than
	 * construct it one GetRecord at a time.
	 * 
	 * @param from
	 *            beginning date using the proper granularity
	 * @param until
	 *            ending date using the proper granularity
	 * @param set
	 *            the set name or null if no such limit is requested
	 * @param metadataPrefix
	 *            the OAI metadataPrefix or null if no such limit is requested
	 * @return a Map object containing entries for a "records" Iterator object
	 *         (containing XML <record/>Strings) and an optional "resumptionMap"
	 *         Map.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 * @exception NoSetHierarchyException
	 *                The repository doesn't support sets.
	 * @exception CannotDisseminateFormatException
	 *                the metadataPrefix isn't supported by the item.
	 */
	public Map listRecords(String from, String until, String set, String metadataPrefix) throws OAIInternalServerError, NoSetHierarchyException,
			CannotDisseminateFormatException, NoItemsMatchException, BadArgumentException {
		log.info(LogManager.getHeader(null, "oai_request", "verb=listRecords,from=" + ((from == null) ? "null" : from) + ",until="
				+ ((until == null) ? "null" : until) + ",set=" + ((set == null) ? "null" : set) + ",metadataPrefix="
				+ ((metadataPrefix == null) ? "null" : metadataPrefix)));

		Map m = doRecordHarvest(from, until, 0, null, set, metadataPrefix, 0);

		// Null means bad metadata prefix was bad
		if (m == null) {
			log.info(LogManager.getHeader(null, "oai_error", "cannot_disseminate_format"));
			throw new CannotDisseminateFormatException(metadataPrefix);
		}

		// If there were zero results, return the appropriate error
		Iterator i = (Iterator) m.get("records");

		if ((i == null) || !i.hasNext()) {
			log.info(LogManager.getHeader(null, "oai_error", "no_items_match"));
			throw new NoItemsMatchException();
		}

		return m;
	}

	/**
	 * Retrieve the next set of records associated with the resumptionToken
	 * 
	 * @param resumptionToken
	 *            implementation-dependent format taken from the previous
	 *            listRecords() Map result.
	 * @return a Map object containing entries for "headers" and "identifiers"
	 *         Iterators (both containing Strings) as well as an optional
	 *         "resumptionMap" Map.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 * @exception BadResumptionTokenException
	 *                the value of the resumptionToken argument is invalid or
	 *                expired.
	 */
	public Map listRecords(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		log.info(LogManager.getHeader(null, "oai_request", "verb=listRecords,resumptionToken=" + resumptionToken));

		/*
		 * FIXME: This may return zero records if the previous harvest returned
		 * a number of records that's an exact multiple of MAX_RECORDS. I hope
		 * that's OK.
		 */
		Object[] params = decodeResumptionToken(resumptionToken);
		Integer previous = (Integer) params[2];
		Integer offset = (Integer) params[6];

		Map m = null;

		/*
		 * We catch BadArgumentExceptions here, because doRecordHarvest() throws
		 * BadArgumentExcpetions when the set spec is bad. set spec bad == bad
		 * resumption token.
		 */
		try {
			m = doRecordHarvest((String) params[0], (String) params[1], previous.intValue(), (String) params[3], (String) params[4],
					(String) params[5], offset.intValue());
		} catch (BadArgumentException bae) {
			m = null;
		}

		// null result means a problem -> bad resumption token
		if (m == null) {
			log.info(LogManager.getHeader(null, "oai_error", "bad_resumption_token"));
			throw new BadResumptionTokenException();
		}

		return m;
	}

	/**
	 * Method to do the actual harvest of records
	 * 
	 * @param from
	 *            OAI 'from' parameter
	 * @param until
	 *            OAI 'until' parameter
	 * @param set
	 *            OAI 'set' parameter
	 * @param metadataPrefix
	 *            OAI 'metadataPrefix' parameter
	 * @param offset
	 *            where to start this harvest
	 * 
	 * @return the Map for listRecords to return, or null if the metadataPrefix
	 *         is invalid
	 */
	private Map<String, Object> doRecordHeaderHarvest(String from, String until, int previous, String event, String set, String metadataPrefix,
			int offset) throws OAIInternalServerError, BadArgumentException {
		Context context = null;
		String schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);

		if (schemaURL == null) {
			return null;
		}

		log.info(">> will try to retrive events!");
		Map results = new HashMap();
		List headers = new LinkedList();
		List identifiers = new LinkedList();
		try {
			context = new Context();
			List<Object> res = Event.getEvents(context, from, until, previous, event, offset);
			Iterator<Event> eventIterator = (Iterator<Event>) res.get(0);
			int nextPrevious = (Integer) res.get(1);
			String nextEvent = (String) res.get(2);
			while (eventIterator.hasNext()) {
				log.info(">>has one event or more");
				try {
					String[] header = getRecordFactory().createHeader(eventIterator.next());
					headers.add(header[0]);
					identifiers.add(header[1]);
					// records.add(getRecordFactory().create(eventIterator.next(),schemaURL,
					// metadataPrefix));
				} catch (IllegalArgumentException e) {
					log.error(">>error creating xml(IllegalArgumentException):" + e.getMessage());
				}
			}
			// log.info(">>records size:"+records.size());
			if (identifiers.size() >= MAX_RECORDS) {
				results.put("resumptionMap",
						getResumptionMap(makeResumptionToken(from, until, nextPrevious, nextEvent, set, metadataPrefix, offset + MAX_RECORDS)));
			}
		} catch (SQLException e) {
			// TODO: handle exception
			log.error(">>sqlexception has occured");
		} finally {
			if (context != null) {
				context.abort();
			}
		}
		results.put("headers", headers.iterator());
		results.put("identifiers", identifiers.iterator());
		return results;
	}

	private Map<String, Object> doRecordHarvest(String from, String until, int previous, String event, String set, String metadataPrefix, int offset)
			throws OAIInternalServerError, BadArgumentException {
		Context context = null;
		String schemaURL = getCrosswalks().getSchemaURL(metadataPrefix);
		Map<String, Object> results = new HashMap<String, Object>();

		if (schemaURL == null) {
			return null;
		}

		// List to put results in
		List<String> records = new LinkedList<String>();
		log.info(">> will try to retrive events!");
		try {
			context = new Context();
			List<Object> res = Event.getEvents(context, from, until, previous, event, offset);
			Iterator<Event> eventIterator = (Iterator<Event>) res.get(0);
			int nextPrevious = (Integer) res.get(1);
			String nextEvent = (String) res.get(2);
			while (eventIterator.hasNext()) {
				// log.info(">>has one event or more");
				try {
					records.add(getRecordFactory().create(eventIterator.next(), schemaURL, metadataPrefix));
				} catch (IllegalArgumentException e) {
					log.error(">>error creating xml(IllegalArgumentException):" + e.getMessage());
				} catch (CannotDisseminateFormatException e) {
					// TODO - handle exception
					log.error(">>error creating xml(CannotDisseminateFormatException):" + e.getMessage());
				}
			}
			results.put("records", records.iterator());
			log.info(">>records size:" + records.size());
			if (records.size() >= MAX_RECORDS) {
				results.put("resumptionMap",
						getResumptionMap(makeResumptionToken(from, until, nextPrevious, nextEvent, set, metadataPrefix, offset + MAX_RECORDS)));
			}
		} catch (SQLException e) {
			// TODO: handle exception
			log.error(">>sqlexception has occured");
		} finally {
			if (context != null) {
				context.abort();
			}
		}
		return results;
	}

	/**
	 * Retrieve a list of sets that satisfy the specified criteria
	 * 
	 * @return a Map object containing "sets" Iterator object (contains
	 *         <setSpec/>XML Strings) as well as an optional resumptionMap Map.
	 * @exception OAIBadRequestException
	 *                signals an http status code 400 problem
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 */
	public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
		log.info(LogManager.getHeader(null, "oai_request", "verb=listSets"));
		throw new NoSetHierarchyException();
	}

	/**
	 * Retrieve the next set of sets associated with the resumptionToken
	 * 
	 * @param resumptionToken
	 *            implementation-dependent format taken from the previous
	 *            listSets() Map result.
	 * @return a Map object containing "sets" Iterator object (contains
	 *         <setSpec/>XML Strings) as well as an optional resumptionMap Map.
	 * @exception BadResumptionTokenException
	 *                the value of the resumptionToken is invalid or expired.
	 * @exception OAIInternalServerError
	 *                signals an http status code 500 problem
	 */
	public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		throw new BadResumptionTokenException();
	}

	/**
	 * close the repository
	 */
	public void close() {
	}

	/**
	 * Create a resumption token. The relevant parameters for the harvest are
	 * put in a
	 * 
	 * @param from
	 *            OAI 'from' parameter
	 * @param until
	 *            OAI 'until' parameter
	 * @param set
	 *            OAI 'set' parameter
	 * @param prefix
	 *            OAI 'metadataPrefix' parameter
	 * @param offset
	 *            where to start the next harvest
	 * 
	 * @return the appropriate resumption token
	 */
	private String makeResumptionToken(String from, String until, int previous, String event, String set, String prefix, int offset) {
		StringBuffer token = new StringBuffer();

		if (from != null) {
			token.append(from);
		}

		token.append("/");
		if (until != null) {
			token.append(until);
		}

		token.append("/");
		token.append(String.valueOf(previous));

		token.append("/");
		if (event != null) {
			token.append(event);
		}

		token.append("/");
		if (set != null) {
			token.append(set);
		}

		token.append("/");
		if (prefix != null) {
			token.append(prefix);
		}

		token.append("/");
		token.append(String.valueOf(offset));

		return (token.toString());
	}

	/**
	 * Get the information out of a resumption token
	 * 
	 * @param token
	 *            the resumption token
	 * @return a 7-long array of Objects; 5 Strings (from, until, event, set,
	 *         prefix) and 2 Integers (previous, offset)
	 */
	private Object[] decodeResumptionToken(String token) throws BadResumptionTokenException {
		Object[] obj = new Object[7];
		String[] splittedToken = token.split("/");
		if (splittedToken.length != 7)
			throw new BadResumptionTokenException();
		for (int i = 0; i < 7; i++) {
			if (i == 2 || i == 6) {
				try {
					obj[i] = new Integer(splittedToken[i]);
				} catch (NumberFormatException e) {
					throw new BadResumptionTokenException();
				}
			} else {
				if (splittedToken[i].equals(""))
					obj[i] = null;
				else
					obj[i] = splittedToken[i];
			}
		}
		return obj;
	}
}
