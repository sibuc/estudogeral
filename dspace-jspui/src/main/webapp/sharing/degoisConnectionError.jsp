<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<dspace:layout titlekey="sharing.degois.title" locbar="off" navbar="off" nocache="true">
	<div id="degoisContent">
			<div class="connectionTitle">
				<fmt:message key="sharing.degois.error.connection.title"/>
			</div>
			<div class="connectionTitle">
				<fmt:message key="sharing.degois.error.connection.content"/>
			</div>
			<div class="goback">
				<form id="sendDocumentToDeGois" action="<%= request.getContextPath() %>/sharing" method="post">
						<input type="hidden" name="id" value="<%= request.getParameter("id") %>" />
						<input type="hidden" name="handler" value="<%= request.getParameter("handler") %>" />
						<input type="hidden" name="step" value="0" />
						<input type="submit" value="<fmt:message key="sharing.degois.error.goback"/>"/>
				</form>
			</div>
	</div>
</dspace:layout>
