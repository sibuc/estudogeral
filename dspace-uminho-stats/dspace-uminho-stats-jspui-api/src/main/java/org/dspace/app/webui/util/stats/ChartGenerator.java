package org.dspace.app.webui.util.stats;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.dspace.core.Context;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.data.jdbc.JDBCPieDataset;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ChartGenerator {
	private static Logger log = LogManager.getLogger(ChartGenerator.class);

	private Context context = null;
	private HttpSession session = null;
	private PrintWriter pw = null;
	private Document doc = null;
	private Document queries = null;
	private Locale locale = null;
	
	String width = null;
	String height = null;
	String sql = null;
	String tickUnit = null;
	String tickCount = null;
	String tickFormat = null;
	String orientation = null;
	
	Node nChart = null;
	
		
	public ChartGenerator(Context context, HttpSession session, PrintWriter pw, Document doc, Document queries, Locale locale) {
		this.context = context;
		this.session = session;
		this.pw = pw;
		this.doc = doc;
		this.queries = queries;
		this.locale = locale;
	}

	public String getChartCode(String chartName, Map inParams, NodeList paramDefs) 
	{
		String html = "";
		String chartType = null;

		try
		{
			nChart = XPathAPI.selectSingleNode(doc,"/charts/chart[@name='" + chartName + "']");

			if (nChart != null)
			{
				chartType = ((Element)nChart).getAttribute("type");
				Node eSize = XPathAPI.selectSingleNode(nChart, "size");
				width = ((Element)eSize).getAttribute("width");
				height = ((Element)eSize).getAttribute("height");

				html = html + "<div style=\"width:100%\">\n";
				html = html + "<div>\n";
				html = html + "<canvas id=\"canvas-" + chartName + "\" height=\"" + height + "\"></canvas>\n";
				html = html + "<div align=\"center\" id=\"legenda-" + chartName + "\"></div>";
				html = html + "</div>\n";
				html = html + "</div>\n";
html = html + "<script>\n";
html = html + "function formata(teste) {\n";
html = html + "var a = teste.toString().replace(\".\", \",\");\n";
html = html + "return a.toString().replace(/\\B(?=(\\d{3})+(?!\\d))/g, \".\");\n";
html = html + "}\n";
html = html + "</script>\n";
				//TODO: tratar width
				Node nQuery = XPathAPI.selectSingleNode(nChart, "query[@src]");

				if (nQuery == null) 
				{
					// doesnt have a src attribute
					 sql = (String) XPathAPI.selectSingleNode(nChart, "query").getFirstChild().getNodeValue();
				}
				else
				{
					// fetch sql from querylist
					nQuery = XPathAPI.selectSingleNode(nChart, "query");
					String srcQuery = ((Element)nQuery).getAttribute("src");
					Node nSrc = XPathAPI.selectSingleNode(queries,
							"/querylist/query[@name='" + srcQuery + "']/sql");
					sql = nSrc.getFirstChild().getNodeValue();
				}
				if (paramDefs != null) 
				{
					sql = processParams(sql, inParams, paramDefs);
				}

				try
				{						Statement statement = context.getDBConnection().createStatement();
					ResultSet rs = statement.executeQuery(sql);
					if (chartType.equals("time"))
					{
						Node eTick = XPathAPI.selectSingleNode(nChart, "tick");
						tickUnit = ((Element)eTick).getAttribute("unit");
						tickCount = ((Element)eTick).getAttribute("count");
						tickFormat = ((Element)eTick).getAttribute("format");
						html = html + TempTimeChart(chartName, rs);				
					}
					else if (chartType.equals("bar"))
					{
						html = html + TempBarChart(chartName, rs);
					}
					else
					{
						html = "UNDEFINED CHART: " + chartType;
					}
				}
				catch (SQLException e)
				{
					log.info(e.getMessage(), e);
				}
			}
		}
		catch (TransformerException e) 
		{
			log.info(e.getMessage(), e);
		}

		return html;
		
	}
	
	public String TempTimeChart(String chartName, ResultSet results)
	{
		String html="";

		String labels = "";
		String[] datasets = null;
		String[] datasetsNames = null;
		Color[] colors = null;

		html = html + "<script>\n";
		try
		{
			ResultSetMetaData meta = results.getMetaData();

			int columns = meta.getColumnCount();
			datasets = new String[columns - 1];
			datasetsNames = new String[columns - 1];	
			colors = new Color[columns - 1];

			for (int i=0; i<columns-1; i++)
				datasets[i] = "";

			for (int i=0; i<columns-1; i++)
				datasetsNames[i] = meta.getColumnName(i+2);

			try
			{
				NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");

				for (int i = 0; i < seriesList.getLength(); i++)
				{
					String color = ((Element)seriesList.item(i)).getAttribute("color");
					if (i <= columns -1)
					{
						colors[i] = hex2Rgb(color);
					}
				}
			}
			catch (TransformerException e)
			{
				log.info(e.getMessage(), e);
			}

			while (results.next())
			{
				for(int i = 0; i < columns; i++)
				{
					String colValue = "";
					int jdbctype = meta.getColumnType(i+1);
					if (i == 0)
					{
						labels = labels + "\"" + JdbcTypeToString(results, jdbctype, i+1) + "\",";
					}
					else
					{
						datasets[i-1] = datasets[i-1] + JdbcTypeToString(results, jdbctype, i+1) + ",";
					}
				}
			}
		}
		catch (SQLException e)
		{
			log.info(e.getMessage());
		}

		if (labels.length() > 0)
		{
			labels = labels.substring(0, labels.length()-1);
		}

		for (int i=0; i<datasets.length; i++)
		{
			if (datasets[i].length() > 0)
			{
				datasets[i] = datasets[i].substring(0, datasets[i].length()-1);
			}
		}

		html = html + "var lineChartData = {\n";
		html = html + "labels: [" + labels + "],\n";
		html = html + "datasets: [\n";

		for (int i=0; i<datasets.length; i++)
		{
			if (colors[i] == null)
			{
				colors[i] = new Color(220,220,220);
			}

			html = html + "{\n";
			html = html + "label: \"" + datasetsNames[i] + "\",\n";
			html = html + "fillColor: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",0.2)\",\n";
			html = html + "strokeColor: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",1)\",\n";
			html = html + "pointColor: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",1)\",\n";
			html = html + "pointStrokeColor: \"#fff\",\n";
			html = html + "pointHighlightFill: \"#fff\",\n";
			html = html + "pointHighlightStroke: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",1)\",\n";
			html = html + "data: [" + datasets[i] + "]\n";
			if (i==datasets.length-1)
				html = html + "}\n";
			else
				html = html + "},\n";
		}

		html = html + "]\n";
		html = html + "}\n";

		html = html + "var ctx = document.getElementById(\"canvas-" + chartName + "\").getContext(\"2d\");\n";
		html = html + "var myLine = new Chart(ctx).Line(lineChartData, {\n";
		html = html + "responsive: true, maintainAspectRatio: false, scaleLabel: \"<%=formata(value)%>\", multiTooltipTemplate: \"<%= formata(value) %>\", tooltipTemplate: \"<%= formata(value) %>\"\n";
		if (datasets.length > 1)
			html = html + ",legendTemplate: \"<table><tr><% for (var i=0; i<datasets.length; i++){%><td><div style=\\\"width:15px;height:15px;border:1px solid <%=datasets[i].lineColor%>;background:<%=datasets[i].fillColor%>;\\\"></div></td><td> <%if(datasets[i].label){%><%=datasets[i].label%><%}%></td><td>&nbsp;</td><%}%></tr></table>\"\n";

		html = html + "});\n";
		if (datasets.length > 1)
		{
			html = html + "var ctxLegend = document.getElementById(\"legenda-" + chartName + "\");\n";
			html = html + "ctxLegend.innerHTML = myLine.generateLegend();\n";
                }

		html = html + "</script>\n";

		return html;
	}

	public String TempBarChart(String chartName, ResultSet results)
	{
		String html="";

		String labels = "";
		String[] datasets = null;
		String[] datasetsNames = null;
		Color[] colors = null;

		html = html + "<script>\n";
		try
		{
			ResultSetMetaData meta = results.getMetaData();
			
			int columns = meta.getColumnCount();
			datasets = new String[columns - 1];
			datasetsNames = new String[columns - 1];
			colors = new Color[columns - 1];

			for (int i=0; i<columns-1; i++)
				datasets[i] = "";

			for (int i=0; i<columns-1; i++)
				datasetsNames[i] = meta.getColumnName(i+2);

			try
			{
				NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");
				
				for (int i = 0; i < seriesList.getLength(); i++) 
				{
                        		String color = ((Element)seriesList.item(i)).getAttribute("color");
					if (i <= columns -1)
					{
						colors[i] = hex2Rgb(color);
					}
				}
			}
			catch (TransformerException e)
			{
				log.info(e.getMessage(), e);
			}


			while (results.next())
			{
				for(int i = 0; i < columns; i++)
				{
					String colValue = "";
					int jdbctype = meta.getColumnType(i+1);
					if (i == 0)
					{

						labels = labels + "\"" + JdbcTypeToString(results, jdbctype, i+1) + "\",";
					}
					else
					{
						datasets[i-1] = datasets[i-1] + JdbcTypeToString(results, jdbctype, i+1) + ",";
					}
				}
			}
		} 
		catch (SQLException e) 
		{
			log.info(e.getMessage());
		}
		if (labels.length() > 0)
		{
			labels = labels.substring(0, labels.length()-1);
		}
		for (int i=0; i<datasets.length; i++)
		{
			if (datasets[i].length() > 0)
			{
				datasets[i] = datasets[i].substring(0, datasets[i].length()-1);
			}
		}

		html = html + "var barChartData = {\n";
		html = html + "labels: [" + labels + "],\n";
		html = html + "datasets: [\n";

		for (int i=0; i<datasets.length; i++)
		{
			if (colors[i] == null)
			{
				colors[i] = new Color(220,220,220);
			}

			html = html + "{\n";
			html = html + "label: \"" + datasetsNames[i] + "\",\n";
			html = html + "fillColor: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",0.5)\",\n";
			html = html + "strokeColor: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",0.8)\",\n";
			html = html + "highlightFill: \"rgba(" + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",0.75)\",\n";
			html = html + "highlightStroke: \"rgba(" + + colors[i].getRed() + "," + colors[i].getGreen() + "," + colors[i].getBlue() + ",1)\",\n";
			html = html + "data: [" + datasets[i] + "]\n";
			if (i==datasets.length-1)
				html = html + "}\n";
			else
				html = html + "},\n";
		}
		html = html + "]\n";
		html = html + "}\n";

		html = html + "var ctx = document.getElementById(\"canvas-" + chartName + "\").getContext(\"2d\");\n";
		html = html + "var myBar = new Chart(ctx).Bar(barChartData, {\n";
		html = html + "responsive: true, maintainAspectRatio: false, scaleLabel: \"<%=formata(value)%>\", multiTooltipTemplate: \"<%= formata(value) %>\", tooltipTemplate: \"<%= formata(value) %>\"\n";
		if (datasets.length > 1)
			html = html + ",legendTemplate: \"<table><tr><% for (var i=0; i<datasets.length; i++){%><td><div style=\\\"width:15px;height:15px;border:1px solid <%=datasets[i].lineColor%>;background:<%=datasets[i].fillColor%>;\\\"></div></td><td> <%if(datasets[i].label){%><%=datasets[i].label%><%}%></td><td>&nbsp;</td><%}%></tr></table>\"\n";
		html = html + "});\n";
		if (datasets.length > 1)
		{
			html = html + "var ctxLegend = document.getElementById(\"legenda-" + chartName + "\");\n";
			html = html + "ctxLegend.innerHTML = myBar.generateLegend();\n";
		}
		html = html + "</script>\n";
      return html;
	}

public static Color hex2Rgb(String colorStr) {
    return new Color(
            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
}

	public String JdbcTypeToString(ResultSet results, int jdbctype, int i) throws SQLException
	{
		String colValue = null;

		if (jdbctype == Types.BIT) {
			colValue = new Boolean(results.getBoolean(i)).toString();
		} else if (jdbctype == Types.INTEGER || jdbctype == Types.BIGINT) {
                	colValue = new Integer(results.getInt(i)).toString();
		} else if ( jdbctype == Types.NUMERIC ||
                  		jdbctype == Types.DECIMAL ||
				jdbctype == Types.REAL ||
				jdbctype == Types.FLOAT ||
				jdbctype == Types.DOUBLE) {
			colValue = new Float(results.getFloat(i)).toString();
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
			if (tickFormat != null && !tickFormat.equals(""))
			{
				java.util.Date newDate = new java.util.Date(results.getDate(i).getTime());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tickFormat, locale);
			
				colValue = simpleDateFormat.format(newDate);
			}
			else
			{
				colValue = results.getDate(i).toString();
			}
		} else if (jdbctype == Types.TIME) {
			colValue = results.getTime(i).toString();
		} else if (jdbctype == Types.TIMESTAMP) {
			colValue = results.getTimestamp(i).toString();
		} else {
			throw new IllegalArgumentException("Unsupported JDBC type: " + jdbctype);
		}
		return colValue;
	}

	public String getChartFileName(String chartName, Map inParams, NodeList paramDefs) {
		
		String filename = null;
		String chartType = null;

		try {
			
			nChart = XPathAPI.selectSingleNode(doc,
					"/charts/chart[@name='" + chartName + "']");
			
			if (nChart != null) {
				chartType = ((Element)nChart).getAttribute("type");
				Node eSize = XPathAPI.selectSingleNode(nChart, "size");
				width = ((Element)eSize).getAttribute("width");
				height = ((Element)eSize).getAttribute("height"); 
				
				Node nQuery = XPathAPI.selectSingleNode(nChart, "query[@src]");
				
				if (nQuery == null) {
					// doesnt have a src attribute
					sql = (String) XPathAPI.selectSingleNode(nChart, "query").getFirstChild().getNodeValue();
				} else {
					// fetch sql from querylist
					nQuery = XPathAPI.selectSingleNode(nChart, "query");
					String srcQuery = ((Element)nQuery).getAttribute("src");
					
					
					Node nSrc = XPathAPI.selectSingleNode(queries,
							"/querylist/query[@name='" + srcQuery + "']/sql");
					
					sql = nSrc.getFirstChild().getNodeValue();
				}
				if (paramDefs != null) {
					sql = processParams(sql, inParams, paramDefs);
				}				
			}

			if (chartType.equals("pie")) {
				filename = generatePieChart();
			} else if (chartType.equals("bar")) {
				orientation = (String) XPathAPI.selectSingleNode(nChart, "orientation").getFirstChild().getNodeValue();
				filename = generateBarChart();
			} else if (chartType.equals("time")) {
				Node eTick = XPathAPI.selectSingleNode(nChart, "tick");
				tickUnit = ((Element)eTick).getAttribute("unit");
				tickCount = ((Element)eTick).getAttribute("count");
				tickFormat = ((Element)eTick).getAttribute("format");
				
				filename = generateTimeChart();
			}
		} catch (TransformerException e) {
		     	log.debug(e.getMessage(), e);
		}
		
		return filename;
	}

	public String generateBarChart() {
		String filename = null;
		
		PlotOrientation po = null;
		boolean showLegend = false;
		
		try {
			
			CategoryDataset dataset = readCategoryData();

			if (orientation.equals("vertical")) {
				po = PlotOrientation.VERTICAL;
			} else {
				po = PlotOrientation.HORIZONTAL;
			}
			
			if (dataset.getRowCount() > 1) {
				showLegend = true;
			} else {
				showLegend = false;
			}

			
	        // create the chart...
	        JFreeChart chart = ChartFactory.createBarChart(
	        	null,       // chart title
	            null,               // domain axis label
	            null,                  // range axis label
	            dataset,                  // data
	            po, 			// orientation
	            showLegend,                    // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	        // set the background color for the chart...
	        chart.setBackgroundPaint(Color.white);

	        // get a reference to the plot for further customisation...
	        CategoryPlot plot = chart.getCategoryPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        RectangleInsets r = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
	        plot.setAxisOffset(r);
	        plot.setRangeCrosshairVisible(true);
	        
	        NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");
	        for (int i = 0; i < seriesList.getLength(); i++) {
	        	String index = ((Element)seriesList.item(i)).getAttribute("index");
	        	String color = ((Element)seriesList.item(i)).getAttribute("color");
	        	plot.getRenderer().setSeriesPaint(Integer.parseInt(index), Color.decode(color));
	        }
	        
	        // set the range axis to display integers only...
	/*        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setUpperMargin(0.15);
	*/        
	        // disable bar outlines...
	        CategoryItemRenderer renderer = plot.getRenderer();        
	        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
	        
	        CategoryAxis domainAxis = plot.getDomainAxis();
	        if (orientation.equals("vertical")) {
	        	domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	        } else {
	        	domainAxis.setMaximumCategoryLabelWidthRatio(0.4f);
	        	domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
	        }

			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	        
		} catch (Exception e) {
			log.debug(e.getMessage(), e);		
		}
		return filename;
	}
	public String generatePieChart() {
		String filename = null;
		try {

			PieDataset data = readPieData();
			
			PiePlot plot = new PiePlot(data);
			plot.setInsets(new RectangleInsets(0, 5, 5, 5));
			
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} - ({2})"));
			plot.setLabelBackgroundPaint(java.awt.Color.white);
			plot.setLabelOutlinePaint(java.awt.Color.white);
			plot.setLabelShadowPaint(null);
			//plot.setOutlinePaint(null);
			//plot.setInteriorGap(0.25);

			JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			
			chart.setBackgroundPaint(java.awt.Color.white);
			
			//JFreeChart chart = ChartFactory.createPieChart(null, //title 
			//		data, true, true, true);
	
			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	        
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return filename;
	}
	public String generateTimeChart() {
		String filename = null;
		boolean showLegend = false;
		
		try {

			XYDataset dataset = readXYData();

			if (dataset.getSeriesCount() > 1) {
				showLegend = true;
			} else {
				showLegend = false;
			}

	        JFreeChart chart = ChartFactory.createTimeSeriesChart(
	                null, // Title
	                null, // x-axis title
	                null, // y-axis title
	                dataset,
	                showLegend,
	                true,
	                true
	            );
	        
	        chart.setBackgroundPaint(Color.white);
	        
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            
            RectangleInsets r = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
            plot.setAxisOffset(r);
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);
            
	        NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");
	        for (int i = 0; i < seriesList.getLength(); i++) {
	        	String index = ((Element)seriesList.item(i)).getAttribute("index");
	        	String color = ((Element)seriesList.item(i)).getAttribute("color");
	        	plot.getRenderer().setSeriesPaint(Integer.parseInt(index), Color.decode(color));
	        }
            
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            if (tickUnit.equals("year")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.YEAR, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            } else if (tickUnit.equals("month")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));            	
            } else if (tickUnit.equals("day")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            } else if (tickUnit.equals("hour")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            }
            
            axis.setVerticalTickLabels(true);        
            
            //StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
            
            //renderer.setPlotImages(true);
            //renderer.setPlotShapes(true);
            //renderer.setSeriesShapesFilled(0, Boolean.TRUE);
            //renderer.setSeriesShapesFilled(1, Boolean.FALSE);
	
			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	 
		} catch (Exception e) {
			log.debug(e.getMessage(), e);		
		}
		return filename;
	}
	private PieDataset readPieData() {
		JDBCPieDataset data = null;
		try {
			data = new JDBCPieDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("SQLException: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.print("Exception: ");
			System.err.println(e.getMessage());
		}
		return data;
	}
	private CategoryDataset readCategoryData() {
		JDBCCategoryDataset data = null;
		try {
			data = new JDBCCategoryDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("SQLException: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.print("Exception: ");
			System.err.println(e.getMessage());
		}
		return data;
	}
	private XYDataset readXYData() {
		JDBCXYDataset data = null;
		try {
			data = new JDBCXYDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			log.debug(e.getMessage(), e);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		return data;
	}

	private String processParams(String sql, Map inParams, NodeList paramDef) {
		String newSql = sql;
                
		for (int i = 0; i < paramDef.getLength(); i++) {
			Node param = paramDef.item(i);
			String id = param.getAttributes().getNamedItem("id")
					.getFirstChild().getNodeValue();
			String value [] = {(String) inParams.get(id)};
			String values = "";
			if (value == null || value[0] == null || value[0].length() == 0) {
				String name = param.getAttributes().getNamedItem("name")
				.getFirstChild().getNodeValue();
			} else {
				for(int j = 0 ; j < value.length - 1; j++){
				values += value[j] +",";
				}
				values += value[value.length - 1];
				newSql = newSql.replaceAll(id, values);
			}
		}
		return newSql;
	}

}
