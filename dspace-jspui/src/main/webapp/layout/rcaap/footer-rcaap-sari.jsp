<%--

    This is the footer default with the content specific for a SARI

--%>
<%@ page import="org.dspace.core.ConfigurationManager" %>

<div class="container">
	<div class="col-md-8">
	<ul class="list-inline copyright pull-left">
		<li>&copy; <a href="<%= request.getContextPath() %>"><%= ConfigurationManager.getProperty("dspace.name") %></a></li>
		<li><a href="<%= request.getContextPath() %>/statistics"><fmt:message key="jsp.statistics.title" /></a></li>
		<li><a href="<%= request.getContextPath() %>/feedback"><fmt:message key="jsp.layout.footer-default.feedback" /></a></li>
	</ul>
	</div>
	<div class="col-md-4">
	<ul class="list-inline pull-right">
		<li><a href="http://www.rcaap.pt" alt="RCAAP" target="_blank" title="RCAAP">
		   <img src="<%= request.getContextPath() %>/image/logos/rcaap3.jpg" alt="RCAAP" border="0" /></a></li>

	</ul>
</div>
</div>