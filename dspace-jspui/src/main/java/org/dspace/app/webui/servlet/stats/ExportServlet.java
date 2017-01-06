package org.dspace.app.webui.servlet.stats;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.dspace.app.webui.exceptions.LackOfParametersException;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.stats.StatsUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportServlet extends DSpaceServlet {
	private static Logger log = Logger.getLogger(ExportServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6644419920295744056L;

	protected void doDSGet(Context context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			SQLException, AuthorizeException {

		try {
    		response.setHeader( "Content-Disposition", "attachment; filename=\"report.csv\"" );
			response.setContentType("text/csv");
			// feed in your array (or convert your data to an array)
			log.debug("Getting locale from session");

			locale = StatsUtil.getSessionLocale(request);

			Document queries = getQueriesDoc();
			Document statsDoc = getStatsDoc();

			String object = request.getParameter("object");
			String objectID = request.getParameter("object-id");
			String paramLevel = request.getParameter("level");
			String paramType = request.getParameter("type");
			String paramPage = request.getParameter("page");
			String paramTab = request.getParameter("tab");
			int number = Integer.parseInt(request.getParameter("number"));
			
			String params = "";
			Map parameters = request.getParameterMap();
			Map newParameters = new HashMap();

			objectID = StatsUtil.getIdFrom(context, object, objectID,
					paramLevel);
			object = paramLevel;

			// set value of objectID TODO: (REVER)
			if (object != null && objectID != null && !objectID.equals("")
					&& !objectID.equals("-1")) {
				params += "&" + "object" + "=" + object;
				params += "&" + "object-id" + "=" + objectID;
				newParameters.put("object", object);
				newParameters.put("object-id", objectID);
			}

			// set default dates if do not exist
			if (parameters.containsKey("page")) {
				if (!parameters.containsKey("start")) {
					Date date = new Date();
					date.setDate(1);
					String start = new SimpleDateFormat("dd-MM-yyyy")
							.format(date);
					params += "&" + "start" + "=" + start;
					newParameters.put("start", start);
				}
				if (!parameters.containsKey("end")) {
					Date today = new Date();
					String end = new SimpleDateFormat("dd-MM-yyyy")
							.format(today);
					params += "&" + "end" + "=" + end;
					newParameters.put("end", end);
				}
				if (!parameters.containsKey("pyear")) {
					Date today = new Date();
					String year = new SimpleDateFormat("yyyy").format(today);
					params += "&" + "pyear" + "=" + year;
					newParameters.put("pyear", year);
				}
				if (!parameters.containsKey("pmonth")) {
					Date today = new Date();
					String year = new SimpleDateFormat("MM").format(today);
					params += "&" + "pmonth" + "=" + year;
					newParameters.put("pmonth", year);
				}
				if (!parameters.containsKey("anoinicio")) {
					Date today = new Date();
					String year = new SimpleDateFormat("yyyy").format(today);
					params += "&" + "anoinicio" + "=" + year;
					newParameters.put("anoinicio", year);
				}
				if (!parameters.containsKey("anofim")) {
					Date today = new Date();
					String year = new SimpleDateFormat("yyyy").format(today);
					params += "&" + "anofim" + "=" + year;
					newParameters.put("anofim", year);
				}
				if (!parameters.containsKey("mesinicio")) {
					Date today = new Date();
					String year = "01";
					params += "&" + "mesinicio" + "=" + year;
					newParameters.put("mesinicio", year);
				}
				if (!parameters.containsKey("mesfim")) {
					Date today = new Date();
					String year = new SimpleDateFormat("MM").format(today);
					params += "&" + "mesfim" + "=" + year;
					newParameters.put("mesfim", year);
				}

			}

			Node nodePage = null;
			Node nodePageTab = null;

			String xpath = null;

			if (parameters.containsKey("type")) {
				xpath = "/statistics/level[@id='" + paramLevel
						+ "']/type[@id='" + paramType + "']//page[@id='"
						+ paramPage + "']";
			} else if (parameters.containsKey("level")) {
				xpath = "/statistics/level[@id='" + paramLevel
						+ "']//page[@id='" + paramPage + "']";
			} else {
				xpath = "/statistics//page";
			}

			nodePage = XPathAPI.selectSingleNode(statsDoc, xpath);

			if (nodePage != null) {
				String pageAcessGroups = ((Element) nodePage)
						.getAttribute("access-group");
				// pageDescription = (String)
				// XPathAPI.selectSingleNode(nodePage, "description")
				// .getFirstChild().getNodeValue();

				if (parameters.containsKey("tab")) {
					nodePageTab = XPathAPI.selectSingleNode(nodePage,
							"tab[@id='" + paramTab + "']");
				} else {
					nodePageTab = XPathAPI.selectSingleNode(nodePage, "tab");
				}
				if (nodePageTab == null) {
					nodePageTab = nodePage;
					paramTab = "";
				} else {
					paramTab = ((Element) nodePageTab).getAttribute("id");
				}
			}

			log.debug("Obtaining context");

			// PrintWriter out = response.getWriter();
			log.debug("Obtaining outputStream");

			log.debug("Instantiation of PrintWriter");

			PrintWriter out = response.getWriter();
			// Map inParams = request.getParameterMap();

			log.debug("Getting parameters from request");

			Map inParams = newParameters;


			log.debug("Instatiation of report generator");

			log.debug("Setting content type");

			response.setContentType("text/html");

			log.debug("Generating report");

			NodeList paramDefs = XPathAPI.selectNodeList(nodePageTab, "param");
			NodeList blockList = XPathAPI.selectNodeList(nodePageTab, "block");

			String blockType = ((Element) blockList.item(number))
					.getAttribute("type");
			String srcName = ((Element) blockList.item(number))
					.getAttribute("src");

			Element eQuery = (Element) XPathAPI.selectSingleNode(queries,
					"querylist/query[@name='" + srcName + "']");

			if (eQuery != null) {
				try {

					String sql = (String) XPathAPI
							.selectSingleNode(eQuery, "sql").getFirstChild()
							.getNodeValue();

					if (paramDefs != null) {
						sql = processParams(sql, inParams, paramDefs);
					}

					Statement statement = context.getDBConnection()
							.createStatement();

					log.debug("Query: " + sql);

					ResultSet rs = statement.executeQuery(sql);
					CSVWriter writer = new CSVWriter(out);
					writer.writeAll(rs, false);
					writer.flush();
					writer.close();
					out.close();
					
				} catch (SQLException e) {
					log.debug(e.getMessage(), e);
				} catch (LackOfParametersException e) {
					log.debug(e.getMessage(), e);
				}
			}
		} catch (TransformerException e1) {
			log.debug(e1.getMessage(), e1);
		}
	}

	private Document queriesDoc = null;
	private Document statsDoc = null;
	private Locale locale;

	private String processParams(String sql, Map inParams, NodeList paramDef)
			throws LackOfParametersException {
		String newSql = sql;
		// String[] obj = (String []) inParams.get("object");
		// String object = null;
		// if(obj == null || obj.length==0)
		// out.print("\n Value for parameter 'object' invalid \n");
		// else
		// object = new String(obj[0]);

		for (int i = 0; i < paramDef.getLength(); i++) {
			Node param = paramDef.item(i);

			log.debug("Nome Name: " + param.getNodeName());

			String id = param.getAttributes().getNamedItem("id")
					.getFirstChild().getNodeValue();
			String value[] = { (String) inParams.get(id) };
			String values = "";
			if (value == null || value[0] == null || value[0].length() == 0) {
				throw new LackOfParametersException();
				/*
				 * String name = param .getAttributes() .getNamedItem("name")
				 * .getFirstChild() .getNodeValue();
				 */
				// out.print("Value for parameter : " + id + ": (" + name
				// + ") is not specified\n");
			} else {
				for (int j = 0; j < value.length - 1; j++) {
					values += value[j] + ",";
				}
				values += value[value.length - 1];
				// validate view autorization
				// if(obj != null && obj.length!=0 &&
				// !StatsUtil.isAutorized(mycontext, object, id, values))
				// out.print("\n Dont have autorization for  : " + object +
				// " whith (" + id
				// + "=" + values + ")\n");
				newSql = newSql.replaceAll(id, values);
			}
		}
		return newSql;
	}

	private Document getQueriesDoc() {

		if (queriesDoc == null) {

			String filePath = StatsUtil.getLocalizedFileName(locale,
					StatsUtil.getStatsPath() + "stats-queries", ".xml");

			try {

				DOMParser parser = new DOMParser();
				parser.parse(filePath);
				queriesDoc = parser.getDocument();

			} catch (SAXException e) {
				log.debug(e.getMessage(), e);
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}

		return queriesDoc;

	}

	private Document getStatsDoc() {

		if (statsDoc == null) {

			String filePath = StatsUtil.getLocalizedFileName(locale,
					StatsUtil.getStatsPath() + "stats", ".xml");

			try {

				DOMParser parser = new DOMParser();
				parser.parse(filePath);
				statsDoc = parser.getDocument();

			} catch (SAXException e) {
				log.debug(e.getMessage(), e);
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}

		return statsDoc;

	}

	protected void doDSPost(Context context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			SQLException, AuthorizeException {
		this.doDSGet(context, request, response);
	}
}
