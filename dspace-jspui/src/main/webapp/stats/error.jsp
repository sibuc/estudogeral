<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="pt.keep.dspace.report.ReportGenerator" %>
<%@ page import="pt.keep.dspace.report.ExportFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.core.Context" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.Node" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.transform.TransformerException" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
 
<%@ page import="org.dspace.app.webui.util.stats.StatsReader" %>
<%@ page import="org.dspace.app.webui.util.stats.StatsUtil" %>

<%@ page import="org.dspace.core.Context" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>

<%@ page import="javax.servlet.jsp.jstl.core.Config" %>
<%@ page import="java.util.*" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://www.dspace.org/stats-tags.tld" prefix="stats" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
	String name = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.title.errorpage");
	String type = request.getParameter("type");
	String month = "";
	int i;
%>

<dspace:layout locbar="commLink" title="<%= name %>">
	<div class="reporting">
		<div class="error">
			<h1><%=name %></h1>
			<p><fmt:message key="stats.reporting.error.message"/></p>
		</div>
	</div>
	
</dspace:layout>
