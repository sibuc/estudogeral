<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace"%>

<%
	String errorCode = request.getParameter("code");
	try {
		if (errorCode != null) {
			int code = Integer.parseInt(errorCode);
			errorCode = "." + code;
		} else {
			errorCode = "";
		}
	} catch (NumberFormatException e) {
		errorCode = "";
	}
	pageContext.setAttribute("errorCode", errorCode);
%>

<dspace:layout titlekey="sharing.orcid.title" locbar="off" navbar="off"
	nocache="true">
	<div id="orcidContent">
		<div class="errorTitle">
			<fmt:message key="sharing.orcid.error.title" />
		</div>
		<div class="errorContent">
			<fmt:message key="sharing.orcid.error.content${errorCode}" />
		</div>
	</div>
</dspace:layout>
