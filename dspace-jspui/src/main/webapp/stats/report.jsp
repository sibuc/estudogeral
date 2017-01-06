<%@ page import="java.util.List" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.PrintStream" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="javax.servlet.jsp.jstl.core.Config" %>
<%@ page import="javax.xml.transform.TransformerException" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.Node" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
 
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.content.Collection" %>
<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.core.Context" %>


<%@ page import="pt.keep.dspace.report.ReportGenerator" %>
<%@ page import="pt.keep.dspace.report.data.ExportFormat" %>
<%@ page import="pt.keep.dspace.report.data.ExportEntity" %>
<%@ page import="pt.keep.dspace.report.field.FieldManager" %>
<%@ page import="pt.keep.dspace.report.field.Field" %>
<%@ page import="pt.keep.dspace.report.field.show.FieldDefaults" %>
<%@ page import="pt.keep.dspace.report.field.show.FieldStyle" %>
<%@ page import="pt.keep.dspace.report.field.filter.AbstractFilter" %>
<%@ page import="pt.keep.dspace.report.exceptions.ReportGenerationException" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>


<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%
	String name = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.title.page");
	String type = request.getParameter("type");
	String bibtex = request.getParameter("bibtex");
	String month = "";
	int i;
	
	Context ct = new Context();
	ExportEntity entity = ExportEntity.valueOf(type.toUpperCase());
	
	String entityName = "";
	switch (entity) {
		case AUTHOR:
			entityName = request.getParameter("id");
			break;
		case COLLECTION:
			Collection c = Collection.find(ct, Integer.parseInt(request.getParameter("id")));
			entityName = c.getMetadata("name");
			break;
		case COMMUNITY:
			Community cm = Community.find(ct, Integer.parseInt(request.getParameter("id")));
			entityName = cm.getMetadata("name");
			break;
	}
	
	List<Field> fields = FieldManager.getInstance().getFields();
%>

<dspace:layout locbar="commLink" title="<%= name %>">
    <%--<script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery-1.7.1.min.js"> </script> --%>
   <!-- Reporting addon -->
   <script type='text/javascript' src='<%= request.getContextPath() %>/stats/js/jquery-1.7.1.min.js'></script>
   <script type='text/javascript' src='<%= request.getContextPath() %>/stats/js/jquery-ui-1.8.16.custom.min.js'></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.colorhelpers.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.crosshair.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.fillbetween.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.image.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.navigate.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.pie.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.resize.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.selection.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.stack.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.symbol.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/jquery.flot.threshold.min.js"></script>
   <script type="text/javascript" src="<%= request.getContextPath() %>/stats/js/excanvas.min.js"></script>
   <!-- End reporting addon includes -->
	<div class="reporting">
		<% if (bibtex != null) { %>
		<h1><fmt:message key="stats.reporting.bib"/></h1>
		<% } else { %>
		<h1><fmt:message key="stats.reporting.info"/></h1>
		<% } %>
		<% if (type == null || type.toLowerCase().equals("author")) { %>
		<h2><fmt:message key="stats.reporting.author"/> <%=new String(Base64.decodeBase64(entityName.getBytes()))%></h2>
		<% } else if (type.toLowerCase().equals("community")) { %>
		<h2><fmt:message key="stats.reporting.community"/> <%=entityName%></h2>
		<% } else { %>
		<h2><fmt:message key="stats.reporting.collection"/> <%=entityName%></h2>
		<% } %>
		<form target="_blank" action="<%=request.getContextPath()%>/reporting" method="GET">
			<input type="hidden" name="type" value="<%=request.getParameter("type")%>" />
			<input type="hidden" name="id" value="<%=request.getParameter("id")%>" />
			<% if (bibtex != null) { %>
			<input type="hidden" name="bibtex" value="yes" />
			<input type="hidden" name="format" value="bibtex" />
			<% } %>
			
			<% if (bibtex == null) { %>
			<h3><fmt:message key="stats.reporting.showcolumns"/></h3>
			<div class="show">
				<% for (Field f : fields) { %>
				<% if (f.isShowable() && f.appears(entity)) { %>
				<div class="showfield" id="<%=f.getID()%>">
					<%
					String colname = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.field."+f.getID()+".column.name");
					%>
					<label for="<%=f.getID()%>"><%=colname%></label>
					<input 
						type="checkbox" 
						name="<%=f.getID()%>"
						value="true"
					<% if (f.getFieldDefaults().isInitialChecked()) { %>
						checked="checked"
					<% } %>
						/>
				</div>
				<% } %>
				<% } %>
			</div>
			<% } %>
	
				
			<% if (bibtex == null) { %>
			<h3><fmt:message key="stats.reporting.groupcolumns"/></h3>
			<div class="group">
				<div class="groupfield" id="none">
					<label for="none"><fmt:message key="stats.reporting.order.field.none"/></label>
					<input 
						type="radio" 
						name="groupfield"
						value="none"
						checked="checked"
						/>
				</div>
				<% for (Field f : fields) { %>
				<% if (f.isGroupable() && f.appears(entity)) { %>
				<div class="groupfield" id="<%=f.getID()%>">
					<%
					String colname = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.field."+f.getID()+".column.name");
					%>
					<label for="groupfield"><%=colname%></label>
					<input 
						type="radio" 
						name="groupfield"
						value="<%=f.getID()%>"
						/>
				</div>
				<% } %>
				<% } %>
			</div>
			<% } %>
			
			<h3><fmt:message key="stats.reporting.ordercolumns"/></h3>
			<div class="group">
				<div class="fields">
					<label><fmt:message key="stats.reporting.order.field"/></label>
					<select name="orderfield">
						<option value="none" selected="selected">---</option>
					<% for (Field f : fields) { %>
					<% if (f.isOrderable() && f.appears(entity)) { %>
						<%
						String colname = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.field."+f.getID()+".column.name");
						%>
						<option value="<%=f.getID()%>"><%=colname%></option>
					<% } %>
					<% } %>
					</select>
				</div>
				<div class="order">
					<label><fmt:message key="stats.reporting.order"/></label>
					<select name="order">
						<option value="ASC"><fmt:message key="stats.reporting.order.asc"/></option>
						<option value="DESC"><fmt:message key="stats.reporting.order.desc"/></option>
					</select>
				</div>
			</div>
			
			<% boolean hasFilter = false; %>
			
			<h3><fmt:message key="stats.reporting.filtercolumns"/></h3>
			<div class="filters">
				<% for (Field f : fields) { %>
				<% if (f.isFilterable() && f.appears(entity)) { %>
				<div class="filterfield" id="<%=f.getID()%>">
					<%
					String colname = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.field."+f.getID()+".column.name");
					try {
						List<String> list = f.getFilter().getPossibleValues(entity, request.getParameter("id"));
						if (list.size()>0) {
						hasFilter = true;
						%>
					<label for="<%=f.getID()%>"><%=colname%></label>: 
					<select name="<%=f.getID()%>filter">
						<option value="none">---</value>
						<% for (String v : list) { %>
						<% String n = (f.getFieldStyle().hasTranslation()) ? LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.field."+f.getID()+"."+v) : v; %> 
						<option value="<%=v%>"><%=n%></value>
						<% } %>
					</select>
					<% }} catch (Throwable ex) { %> 
					<% ex.printStackTrace(new PrintStream(System.out)); %>
					<% } %>
				</div>
				<% } %>
				<% } %>
				
				<% if (!hasFilter) { %>
				<p class="nofilter"><fmt:message key="stats.reporting.filter.nofilter"/></p>
				<% } %>
			</div>
			
			<div class="buttons">
				<% if (bibtex == null) { %>
				<label><fmt:message key="stats.reporting.format"/></label>
				<select name="format">
					<% for (ExportFormat format : ExportFormat.values()) { %>
					<% String na = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.format."+format.name().toLowerCase()); %>
					<option value="<%=format.name()%>"><%=na%></option>
					<% } %>
				</select>
				<% } %>
				<input type="submit" name="submit" value="<fmt:message key="stats.reporting.button.generate"/>"/>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		$(function(){
			// Accordion
			$(".reporting").accordion({ header: "h3" });
		});
	</script>
</dspace:layout>
