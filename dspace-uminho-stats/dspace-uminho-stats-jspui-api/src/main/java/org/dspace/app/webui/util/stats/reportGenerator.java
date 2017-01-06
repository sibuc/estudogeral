/*
 * Created on 21/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.dspace.app.webui.util.stats;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.dspace.app.webui.exceptions.LackOfParametersException;
import org.dspace.app.webui.jsptag.stats.DataTag;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import java.util.Calendar;
/**
 * @author u4187959
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class reportGenerator {
    private static Logger log = Logger.getLogger(reportGenerator.class);
	
	private Context context = null;
	private Node nodePageTab;
	
    private String statName = "";
    private String styleSheet = "";
    
    private String blockName = "";
    private String blockTitle ="";
    private String blockType = "";
    private String srcName = "";
    
    private Document queriesDoc = null;
    private Document chartsDoc = null;
    private Locale locale;
 
 	public reportGenerator(Context context, Locale locale, Node nodePageTab) {
            this.context = context;
            this.locale = locale;
            this.nodePageTab = nodePageTab;

			if (nodePageTab != null) {
    			statName = ((Element)nodePageTab).getAttribute("name");
                styleSheet = ((Element)nodePageTab).getAttribute("stylesheet");
			}
	}
	public void createReport(PrintWriter out, HttpSession session, Map inParams) {

		try {
			Document doc = null;
			DocumentBuilderFactory dbf;
			DocumentBuilder db;
			Element eRoot = null;
			
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
			
			eRoot = doc.createElement("statistic");

			setRootAttributes(eRoot, inParams);
			
			// Blocks
			log.debug("NodePageTab: "+nodePageTab);
			NodeList paramDefs = XPathAPI.selectNodeList(nodePageTab, "param");
			NodeList blockList = XPathAPI.selectNodeList(nodePageTab, "block");
			
			for (int i = 0; i < blockList.getLength(); i++) {
				
				blockName = ((Element)blockList.item(i)).getAttribute("name");
				Node nodeTitle = XPathAPI.selectSingleNode(blockList.item(i), "title");
				if (nodeTitle != null) {
					blockTitle = nodeTitle.getFirstChild().getNodeValue();
				}
				blockType = ((Element)blockList.item(i)).getAttribute("type");
								
				Element eBlock = null;
				eBlock = doc.createElement("block");
				eBlock.setAttribute("name", blockName);
	            if (nodeTitle != null)
	            	eBlock.setAttribute("title", blockTitle);
				eBlock.setAttribute("type", blockType);
				eBlock.setAttribute("number", i+"");
				
				srcName = ((Element)blockList.item(i)).getAttribute("src");
				Document queries = getQueriesDoc();
				if (blockType.equals("query")) { 


	    			Element eQuery = (Element) XPathAPI.selectSingleNode(queries,
							"querylist/query[@name='" + srcName + "']");
	        		
	        		if (eQuery == null) {
	        			out.println("report query " + srcName + " doesn't exist");
	        		} else {
	        			try {
						String exportUrl = "statsexp?"+this.getParams(inParams)+"number="+i;
	        				eBlock.setAttribute("export-url",exportUrl);
	
	        				String sql = (String) XPathAPI.selectSingleNode(eQuery, "sql")
	        						.getFirstChild().getNodeValue();
	        				
	        				if (paramDefs != null) {
	        					sql = processParams(sql, inParams, paramDefs, out);
	        				}
	        				
	        				Statement statement = context.getDBConnection().createStatement();
	        				
	        				log.debug("Query: "+sql);
	        				
	        				ResultSet rs = statement.executeQuery(sql);

	        				eBlock.appendChild(resultsetToDocument(doc, eQuery, rs));
	        			

	        			} catch (SQLException e) {
	        				log.debug(e.getMessage(), e);
	        			}
	        		}
					
				} else if (blockType.equals("chart")) {
					//TESTE
					 ChartGenerator cg = new ChartGenerator(context, session, new PrintWriter(out), getChartsDoc(), queries, locale);
					String chartcode = cg.getChartCode(srcName, inParams, paramDefs);
					eBlock.appendChild(doc.createTextNode(chartcode));
					//TESTE
	            	/*Element eChart = null;
	    			eChart = doc.createElement("chart");
	    			
	    			ChartGenerator cg = new ChartGenerator(context, session, new PrintWriter(out), getChartsDoc(), queries, locale);
	    			String chartfilename = cg.getChartFileName(srcName, inParams, paramDefs);
	    			
	    			eChart.setAttribute("chart-url", "DisplayChart?filename=" + chartfilename);
	    			eBlock.appendChild(eChart);*/

				} else if (blockType.equals("html")) {

					String html = blockList.item(i).getFirstChild().getNodeValue();
					eBlock.appendChild(doc.createTextNode(html));
					
				} else if (blockType.equals("free")) {
					
				}
				eRoot.appendChild(eBlock);
			}
			
			doc.appendChild(eRoot);
			
			PrintResult(doc, styleSheet, out);
			
		} catch (TransformerException e) {
			log.debug(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			log.debug(e.getMessage(), e);
		} catch (DOMException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private String getParams (Map<String, String> par) {
		String params = "";
		try {
			for (String s : par.keySet())
				params += s + "=" + par.get(s) + "&"; 
		} catch(java.util.NoSuchElementException e) {
			e.printStackTrace();
		}
		return params;
	}
	
	private String processParams(String sql, Map inParams, NodeList paramDef,
			PrintWriter out) throws LackOfParametersException {
		String newSql = sql;
                //String[] obj = (String []) inParams.get("object");
                //String object = null;
                //if(obj == null || obj.length==0)
                //    out.print("\n Value for parameter 'object' invalid \n");
                //else 
                //    object = new String(obj[0]);
                
		for (int i = 0; i < paramDef.getLength(); i++) {
			Node param = paramDef.item(i);
			
			log.debug("Nome Name: "+param.getNodeName());
			
			String id = param.getAttributes().getNamedItem("id").getFirstChild().getNodeValue();
			String value [] = {(String) inParams.get(id)};
			String values = "";
			if (value == null || value[0] == null || value[0].length() == 0) {
				throw new LackOfParametersException();
				/*String name = param
					.getAttributes()
					.getNamedItem("name")
					.getFirstChild()
					.getNodeValue();*/
				//out.print("Value for parameter : " + id + ": (" + name
				//		+ ") is not specified\n");
			} else {
				for(int j = 0 ; j < value.length - 1; j++){
				values += value[j] +",";
				}
				values += value[value.length - 1];
                                //validate view autorization
                                //if(obj != null && obj.length!=0 && !StatsUtil.isAutorized(mycontext, object, id, values))
                                //    out.print("\n Dont have autorization for  : " + object + " whith (" + id
				//		+ "=" + values + ")\n");
				newSql = newSql.replaceAll(id, values);
			}
		}
		return newSql;
	}



	private void PrintResult(Document doc, String pStylesheet, PrintWriter out) {
		String xslDir = StatsUtil.getStatsXslPath();
		
		String outPutDir = StatsUtil.getStatsXslPath() + "/out";
		OutputStream os = null;
		try {
			StreamSource xslSource = new StreamSource(xslDir + File.separator + pStylesheet);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(xslSource);
			Source src = new DOMSource(doc);
			StreamResult dest = new StreamResult(out);
			transformer.transform(src, dest);
                        try {
                            FileOutputStream aaa = new FileOutputStream(outPutDir + "/orig.xml");
                            PrintWriter xmlGraph = new PrintWriter(aaa);
                            TransformerFactory tf = TransformerFactory.newInstance();
                            Transformer serializer = tf.newTransformer();
                            StreamResult grapf = new StreamResult(xmlGraph);
                            serializer.transform(src, grapf);
                            xmlGraph.close();

                            //FileOutputStream fich = new FileOutputStream("/dspace_inst/cocoon-2.1.7/build/webapp/charts/data/graph.xml");
                            FileOutputStream fich = new FileOutputStream(outPutDir + "/out.xml");
                            StreamResult result = new StreamResult(fich);
                            transformer.transform(src, result);
                            fich.close();
                        } catch (IOException e) {
	        				log.debug(e.getMessage(), e);
                        }
                        //
		} catch (TransformerException e) {
			log.debug(e.getMessage(), e);
		} catch (TransformerFactoryConfigurationError e) {
			log.debug(e.getMessage(), e);
		}
	}

	private Element resultsetToDocument(Document doc, Element eQuery, ResultSet results) throws SQLException {

		Element eResultset = null;
		Element eResult = null;
		Element eData = null;
		Element eTotal = null;
		
		int columns;
		int recordNumber = 0;
		
		String calculateTotals = null;
		NodeList listColumns = null;
		Column[] columnsArray = null;
		 
		
		
		try {
						
			listColumns = XPathAPI.selectNodeList(eQuery, "column");
			columnsArray = new Column[listColumns.getLength()];
			for (int i = 0; i < listColumns.getLength(); i++) {
				Element c = (Element)listColumns.item(i);
				columnsArray[i] = new Column();
				columnsArray[i].setLabel(c.getAttribute("label")); 
				columnsArray[i].setAlign(c.getAttribute("align"));
				columnsArray[i].setFormat(c.getAttribute("format"));
				columnsArray[i].setCalcTotal(c.getAttribute("calc-total"));
			}
		} catch (TransformerException e) {
			log.debug(e.getMessage(), e);
		}
		
		ResultSetMetaData meta = results.getMetaData();
		try {
			columns = meta.getColumnCount() + 1;
			eResultset = doc.createElement("resultset");
			//eResultset.setAttribute("EpersonName", c.getCurrentUser().getFullName());

			while (results.next()) {
				recordNumber++;
				eResult = doc.createElement("result");
				for (int i = 1; i < columns; i++) {
					
					String colValue = "";
					String colName = "";
					String colAlign = "";
					if (i <= listColumns.getLength()) {
						colName = columnsArray[i-1].getLabel(); 
						colAlign = columnsArray[i-1].getAlign();
					} else {
						colName = meta.getColumnName(i);
						colAlign="left";
					}
					int jdbctype = meta.getColumnType(i);

					if (jdbctype == Types.BIT) {
						colValue = new Boolean(results.getBoolean(i)).toString();
					} else if (jdbctype == Types.INTEGER || jdbctype == Types.BIGINT) {
						if (i <= listColumns.getLength()) {
							NumberFormat formatter = new DecimalFormat(columnsArray[i-1].getFormat());
							colValue = formatter.format(new Integer(results.getInt(i)));
							if (columnsArray[i-1].getCalcTotal().equals("yes")) {
								columnsArray[i-1].sumTotal(results.getFloat(i));
							}
						} else {
							colValue = new Integer(results.getInt(i)).toString();
						}
					} else if ( jdbctype == Types.NUMERIC ||
								jdbctype == Types.DECIMAL ||
								jdbctype == Types.REAL ||
								jdbctype == Types.FLOAT ||
								jdbctype == Types.DOUBLE) {
						
						if (i <= listColumns.getLength()) {
							NumberFormat formatter = new DecimalFormat(columnsArray[i-1].getFormat());
							colValue = formatter.format(new Float(results.getFloat(i)));
							if (columnsArray[i-1].getCalcTotal().equals("yes")) {
								columnsArray[i-1].sumTotal(results.getFloat(i));
							}							
						} else {
							colValue = new Float(results.getFloat(i)).toString();
						}
					} else if (jdbctype == Types.VARCHAR) {
						try {
							byte[] bytes = results.getBytes(i);

							if (bytes != null) {
								colValue = new String(results.getBytes(i),"UTF-8");
							} else {
								colValue = results.getString(i);
							}
						} catch (UnsupportedEncodingException e) {
							// do nothing, UTF-8 is built in!
						}
					} else if (jdbctype == Types.DATE) {
						if (i <= listColumns.getLength()) {
							SimpleDateFormat formatter = new SimpleDateFormat(columnsArray[i-1].getFormat(), locale);
							colValue = formatter.format(results.getDate(i));
						} else {
							colValue = results.getDate(i).toString();
						}
						
					} else if (jdbctype == Types.TIME) {
						if (i <= listColumns.getLength()) {
							SimpleDateFormat formatter = new SimpleDateFormat(columnsArray[i-1].getFormat(), locale);
							colValue = formatter.format(results.getTime(i));
						} else {
							colValue = results.getTime(i).toString();
						}

						//colValue = results.getTime(i).toString();

					} else if (jdbctype == Types.TIMESTAMP) {

						colValue = results.getTimestamp(i).toString();

					} else {
						throw new IllegalArgumentException(
								"Unsupported JDBC type: " + jdbctype + " " + meta.getColumnTypeName(i));
					}

					eData = doc.createElement("column");
					eData.setAttribute("name", colName);
                    eData.setAttribute("align", colAlign);

					eData.appendChild(doc.createTextNode(colValue));
					eResult.appendChild(eData);					
                   
				}
				eResultset.appendChild(eResult);
			}			

			boolean showTotals = false;
			for (int i = 0; i < columnsArray.length; i++) {
				if (columnsArray[i].getCalcTotal().equals("yes")) {
					showTotals = true;
					break;
				}
			}
			if (showTotals && recordNumber > 1) {
				eTotal = doc.createElement("total");
				for (int i = 1; i < columns; i++) {
					eData = doc.createElement("column");
					if (i <= listColumns.getLength()) {
						eData.setAttribute("align", columnsArray[i-1].getAlign());
						if (columnsArray[i-1].getCalcTotal().equals("yes")) {
							NumberFormat formatter = new DecimalFormat(columnsArray[i-1].getFormat());
							String value = formatter.format(new Float(columnsArray[i-1].getTotal()));
							eData.appendChild(doc.createTextNode(value));
						} else {
							eData.appendChild(doc.createTextNode(""));
						}
					} else {
						eData.setAttribute("align", "right");
					}
					eTotal.appendChild(eData);
				}
				eResultset.appendChild(eTotal);
			}
		} catch (DOMException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return eResultset;
	}

	private void setRootAttributes(Element root, Map inParams) {

		Date today = new Date();
		
		int mapsize = inParams.size();
		Iterator keyValuePairs = inParams.entrySet().iterator();
		
		for (int m = 0; m < mapsize; m++)
		{
		  Map.Entry entry = (Map.Entry) keyValuePairs.next();
		  String key = (String)entry.getKey();
		  String[] value = {(String)entry.getValue()};
		  String values = "";
		  for(int j = 0 ; j < value.length - 1; j++){
			values += value[j] +",";
			}
			values += value[value.length - 1];
			root.setAttribute(key, values);
		}
		root.setAttribute("date", today.toString());		
	}

	private static String getScaleByAttribute(Date startDate, Date finishDate, boolean allowHours) {

		long ONE_DAY = 24 * 60 * 60 * 1000;
		String scaleBy = "year";
		long thisDate = startDate.getTime();
		long otherDate = finishDate.getTime();

		long diff = otherDate - thisDate;
		long days = diff / ONE_DAY;

        if(allowHours && days < 3)
        	scaleBy = "hour";
        else if(days < 20)
        	scaleBy = "day";
        else if(days < 70)
        	scaleBy = "week";
        else if(days < 400)
        	scaleBy = "month";
 
		return scaleBy;
	}
		
	private Document getQueriesDoc() {

		if (queriesDoc == null) {
			
    		String filePath = StatsUtil.getLocalizedFileName(locale, StatsUtil.getStatsPath() + "stats-queries", ".xml");
    		
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
	
	private Document getChartsDoc() {

		if (chartsDoc == null) {
			
    		String filePath = StatsUtil.getLocalizedFileName(locale, StatsUtil.getStatsPath() + "stats-charts", ".xml");
    		
			try {
				
				DOMParser parser = new DOMParser();
				parser.parse(filePath);
				chartsDoc = parser.getDocument();
				
			} catch (SAXException e) {
				log.debug(e.getMessage(), e);
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
			
		}
		
		return chartsDoc;

	}

	private class Column {
		private String label = null;
		private String align = null;
		private String format = null;
		private String calctotal = null;
		private float total = 0f;
		
		public String getLabel() {
			return this.label;
		}
		public String getAlign() {
			return this.align;
		}
		public String getFormat() {
			return this.format;
		}
		public String getCalcTotal() {
			return this.calctotal;
		}
		public float getTotal() {
			return this.total;
		}
		public void setLabel(String v) {
			this.label = v;
		}
		public void setAlign(String v) {
			this.align = v;
		}
		public void setFormat(String v) {
			this.format = v;
		}
		public void setCalcTotal(String v) {
			if (v == null) {
				this.calctotal = "no";
			} else {
				this.calctotal = v;
			}
		}
		public void sumTotal(float v) {
			this.total += v;
		}
	}
}
