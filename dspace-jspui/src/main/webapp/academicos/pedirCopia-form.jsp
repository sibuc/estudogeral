<%--
  - pedirCopia-form.jsp
  -
  - Version: $Revision: 1.0 $
  -
  - Date: $Date: 2004/12/29 19:51:49 $
  -
  - Copyright (c) 2004, University of Minho
  -   All rights reserved.
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
  - Sugest form JSP
  -
  - Attributes:
  -    requestItem.problem  - if present, report that all fields weren't filled out
  -    authenticated.email - email of authenticated user, if any
  -	   handle - URL of handle item
  --%>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="pt.ucoimbra.sibuc.dspace.servlet.PedirCopiaPapelServlet"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%
	request.setCharacterEncoding("UTF-8");
	
    String collection_name = (String) request.getAttribute("collection_name");
    if (collection_name == null)
        collection_name = "";

    String membrojuri = (String) request.getAttribute("membrojuri");
    if (membrojuri == null)
        membrojuri = "";

    String emailmembrojuri = (String) request.getAttribute("emailmembrojuri");
    if (emailmembrojuri == null)
        emailmembrojuri = "";

    String handle = (String) request.getAttribute("item-handle");
    if (handle == null )
        handle = "";
	
    String title = (String) request.getAttribute("title");
    if (title == null)
        title = "";
		
    String authEmail = (String) request.getAttribute("authEmail");
    if (authEmail == null)
        authEmail = "";

    String userName = (String) request.getAttribute("userName");
    if (userName == null)
        userName = "";

%>

<dspace:layout locbar="off" navbar="off" titlekey="jsp.pedido.copia.pedidoCopia-form.title" >

<br />
<p><fmt:message key="jsp.pedido.copia.pedidoCopia-form.info">
</fmt:message>
</p>
<br />
<p><fmt:message key="jsp.pedido.copia.pedidoCopia-form.info2">
</fmt:message>
    <br /><%= collection_name %>
</p>

    <form name="form1" action="<%= request.getContextPath() %>/pedir-copia" method="POST">
        <center>
            <table>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.pedidoCopia-form.membrojuri"/></td>
                    <td><input type="TEXT" name="membrojuri" size="50" value="<%= membrojuri %>"></td>
                </tr>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.pedidoCopia-form.emailmembrojuri"/></td>
                    <td><input type="TEXT" name="emailmembrojuri" size="50" value="<%= emailmembrojuri %>"></td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                    <input type="HIDDEN" name="item-handle" value='<%= handle %>'>
                    <input type="HIDDEN" name="title" value='<%= title %>'>
                    <input type="HIDDEN" name="authEmail" value="<%= authEmail %>">
                    <input type="HIDDEN" name="userName" value="<%= userName %>">
                    <input type="HIDDEN" name="step" value="<%= PedirCopiaPapelServlet.ENTER_FORM_PAGE %>">
                    <input type="HIDDEN" name="collection_name" value="<%= collection_name %>">
                    <input type="SUBMIT" name="submit" value="<fmt:message key="pedido.copia.pedidoCopia-form.go"/>" >
                    </td>
                </tr>
            </table>
        </center>
    </form>

</dspace:layout>
