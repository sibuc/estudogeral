<%-- 
    Document   : get-col-provas-success
    Created on : 3/Dez/2015, 15:59:26
    Author     : dspace
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%--
  - get-col-provas-success.jsp
  -
  - Version: $Revision: 3705 $
  -
  - Date: $Date: 2009-04-11 17:02:24 0000 (Sat, 11 Apr 2009) $
  -
  - Copyright (c) 2002, Hewlett-Packard Company and Massachusetts
  - Institute of Technology.  All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are
  - met:
  -
  - - Redistributions of source code must retain the above copyright
  - notice, this list of conditions and the following disclaimer.
  -
  - - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  -
  - - Neither the name of the Hewlett-Packard Company nor the name of the
  - Massachusetts Institute of Technology nor the names of their
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  - A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  - HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  - BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  - OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  - ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  - TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  - USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  - DAMAGE.
  --%>

<%--
  - Form requesting a Handle or internal item ID for item editing
  -
  - Attributes:
  -     invalid.id  - if this attribute is present, display error msg
  --%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.core.ConfigurationManager" %>

<dspace:layout titlekey="jsp.tools.get-col-provas.title"
               navbar="default"
               locbar="link">

	<%-- <h1>Cria Colecao para provas</h1> --%>
	<h1><fmt:message key="jsp.tools.get-col-provas.heading"/></h1>
    
    <%-- <p>Enter the Handle or internal item ID of the item you want to edit or
    delete.  <dspace:popup page="/help/site-admin.html#items">More help...</dspace:popup></p> --%>

<%--	<div><fmt:message key="jsp.tools.get-col-provas.info2"/><dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.site-admin\")  \"#items\"%>"><fmt:message key="jsp.morehelp"/></dspace:popup></div> --%>
    
    <form method="get" action="">
     <center>
        <table class="miscTable">
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">Candidato:</td> --%>
	      <td class="submitFormLabel">
                 Provas criadas com sucesso
              </td>
          </tr>
          <tr><td></td></tr>
          <tr><td></td></tr>
          <tr><td></td></tr>
             <tr class="oddRowEvenCol">
             <td>
             <input type="submit" name="submit" value="<fmt:message key="jsp.tools.get-col-provas.submitbutton"/>" />
             </td>
            </tr>
            </table>
        </center>
    </form>
</dspace:layout>

