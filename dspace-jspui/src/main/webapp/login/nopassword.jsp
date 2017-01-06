<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Page that displays the netid/password login form
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<style>
footer.navbar.navbar-bottom
{
	
   position: fixed;

}
</style>

<dspace:layout navbar="off"
		locbar="off"
		titlekey="jsp.components.ldap-form.login.readonly">
		
   <dspace:include page="/components/no-ldap-form.jsp" />
</dspace:layout>
