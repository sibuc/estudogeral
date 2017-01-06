<%--
  - migraProvas-form.jsp
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

<%@page import="org.dspace.core.ConfigurationManager"%>
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

<%@ page import="pt.ucoimbra.sibuc.dspace.servlet.FinalizaProvasServlet"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="java.util.ArrayList" %>

<%
    request.setCharacterEncoding("UTF-8");
	
    String collection_name = (String) request.getAttribute("collection_name");
    String handle = (String) request.getAttribute("handle");
    String itemID = (String) request.getAttribute("itemID");    
    String thesis_degree_name = (String) request.getAttribute("thesis_degree_name");
    String thesis_renates_id = (String) request.getAttribute("thesis_renates_id");
    String thesis_author_name = (String) request.getAttribute("thesis_author_name");
    if (thesis_renates_id == null)
        thesis_renates_id = "";
    String thesis_degree_discipline_id = (String) request.getAttribute("thesis_degree_discipline_id");
    if (thesis_degree_discipline_id == null)
        thesis_degree_discipline_id = "";		
    String thesis_author_id = (String) request.getAttribute("thesis_author_id");
    if (thesis_author_id == null)
        thesis_author_id = "";
    String thesis_date_embargo = (String) request.getAttribute("thesis_date_embargo");
    if (thesis_date_embargo == null)
        thesis_date_embargo = "";
    String[] embargos= ConfigurationManager.getProperty("embargo.terms.days").split(", ");
    int selected =0;
    for (int i=0; i < embargos.length; i++)
    {
        embargos[i] = embargos[i].substring(0, embargos[i].indexOf(":"));
        if (embargos[i].trim().equals(thesis_date_embargo.trim())) selected = i;
    }


%>

<dspace:layout locbar="off" navbar="off" titlekey="jsp.pedido.copia.migraProvas-form.title" >

<br />
<p><fmt:message key="jsp.pedido.copia.migraProvas-form.info">
</fmt:message><br/>
<%= collection_name %><br/>
<%= thesis_degree_name %><br/>
<br />
<p>
</p>

    <form name="form1" action="<%= request.getContextPath() %>/finalizaprovas/<%= handle %>" method="POST">
        <center>
            <table>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.migraProvas-form.renates_id"/></td>
                    <td><input type="TEXT" name="thesis_renates_id" size="50" value="<%= thesis_renates_id %>"></td>
                </tr>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.migraProvas-form.discipline_id"/></td>
                    <td><input type="TEXT" name="thesis_degree_discipline_id" size="50" value="<%= thesis_degree_discipline_id %>"></td>
                </tr>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.migraProvas-form.author_id"/></td>
                    <td><input type="TEXT" name="thesis_author_id" size="50" value="<%= thesis_author_id %>"></td>
                </tr>
                <tr>
                    <td class="submitFormLabel"><fmt:message key="jsp.pedido.copia.migraProvas-form.date_embargo"/></td>
                    <td><select name="thesis_date_embargo" size="8">
                        <% if (thesis_date_embargo == null) { %>
                        <option value="Acesso Aberto" selected>Acesso Aberto</option>
                        <% } else { %>
                        <option value="Acesso Aberto">Acesso Aberto</option>
                        <% } %>
                        
                        <% for (int i=0; i < embargos.length; i ++) {
                        if (i==selected) { %>
                        <option value="<%=embargos[i] %>" selected><%=embargos[i]%></option>
                        <%  } else { %>
                        <option value="<%=embargos[i] %>"><%=embargos[i]%></option>
                        <% }
                        } %>
                        </select>
                    </td>
                </tr>   
                
                <tr>
                    <td colspan="2" align="center">
                    <input type="hidden" name="handle" value="<%= handle %>">
                    <input type="hidden" name="itemID" value="<%= itemID %>">
                    <input type="hidden" name="collection_name" value="<%= collection_name %>">
                    <input type="hidden" name="thesis_degree_name" value="<%= thesis_degree_name %>">
                    <input type="hidden" name="thesis_author_name" value="<%= thesis_author_name %>">
                    <input type="SUBMIT" name="submit" value="<fmt:message key="pedido.copia.migraProvas-form.go"/>" >
                    </td>
                </tr>
            </table>
        </center>
    </form>

</dspace:layout>
