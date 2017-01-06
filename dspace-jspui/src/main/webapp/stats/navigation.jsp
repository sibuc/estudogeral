<%--
  - navigation.jsp
  -
  - Version: $Revision: 1.0 $
  -
  - Date: $Date: 2003/02/21 19:51:49 $
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

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://www.dspace.org/stats-tags.tld" prefix="stats" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%--
Parameters of jsp parent
    level - level of current statistic
    type - type of current statistic
    page - statistic page
    tab - tab separator
    
TODO: Mostrar so niveis a que tem acesso
      Mostrar so as opcoes a que tem acesso
--%>
            
<table width="100%" border="0" cellspacing="1" cellpadding="1">
   <!-- Levels -->
   <%      
   NodeList listLevels = statsDoc.getElementsByTagName("level");
   if (listLevels.getLength()>0) {
	   Element stat = (Element)XPathAPI.selectSingleNode(statsDoc, "statistics");
	   String levelTitle = stat.getAttribute("level-title");
	   %>
	   <tr class="MenuTitle">
	      <td nowrap colspan="2" class="MenuTitle"><%= levelTitle %></td>
	   </tr>         	   
	   <%	   
   }
   for (int i = 0; i < listLevels.getLength(); i++) {
      Element elem = (Element)listLevels.item(i);
      //TODO: verificar se o user tem acesso a alguma estatistica dentro deste nivel
      // fazer funcao que retorne todos os menu-item e verifique se hÃƒÂƒÃ‚Â¡ algum com chekAccess a true
      // decidir em que elementos incluir accessGroups
      // substituir a abordagem firstmenu tanto aqui como no reports.jsp
      //if(firstMenu.contains(levels[i])) {
          %>
          <tr class="Menu1">
             <td nowrap class="Menu1">
                <a href="<%= request.getContextPath() %>/stats?level=<%=elem.getAttribute("id") + "&page=" + nextPage(listLevels.item(i))%><%=params%>">
                   <%= elem.getAttribute("id").equals(paramLevel)?"<b>":""%>
                   <%= elem.getAttribute("label") %>
                   <%= elem.getAttribute("id").equals(paramLevel)?"</b>":""%>
                </a>
             </td>
          </tr>
       <%
       //}	   
   }
   %>
   
   <!-- Menu -->
   <% 
   
   if (paramLevel != null || nodePageTab != null) {
	   NodeList listTypes = XPathAPI.selectNodeList(statsDoc, "statistics/level[@id='" + paramLevel + "']/type");
	   for (int i = 0; i < listTypes.getLength(); i++) {
		  Element elementNode = (Element)listTypes.item(i);
          %>
          <tr>
             <td colspan="2">&nbsp;</td>
          </tr>
          <tr class="MenuTitle">
             <td nowrap colspan="2" class="MenuTitle"> <%=elementNode.getAttribute("label")%></td>
          </tr>         
          <%
          list(listTypes.item(i),1, out, request, paramLevel, elementNode.getAttribute("id"), paramPage, params);
	   }
   }
   %>
   </table>

<%! 
public void list(Node node, int level, JspWriter out, HttpServletRequest request, String paramLevel, String paramType, String paramPage, String params) throws Exception {

	Element temp = (Element)node;
	String np = nextPage(node);
	
	//if (level == 1 || temp.getAttribute("id").equals(menuID) || isParent(node,menuID)) {
	if (level == 1 || np.equals(paramPage) || isParent(node,paramPage)) {
	    NodeList list = node.getChildNodes();
	    for (int i=0; i<list.getLength(); i++) {
	        // Get child node
	        Node childNode = list.item(i);
	
	        //Meu
	        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
		        Element element = (Element)list.item(i);
		        if (element.getTagName().equals("menu")) {
					String isActive = element.getAttribute("active");
					if (isActive == null || "".equals(isActive)) isActive="true";
					
					if (isActive.equals("true")) {
						out.println("<tr class='Menu1'>");
						out.println("<td nowrap class='Menu1'>");
						for (int j=1; j<level; j++) {
							out.print("&nbsp;&nbsp;");
						}
						//out.println(element.getAttribute("id").equals(menuID)?"<b>":"");
						out.println(pageID(childNode).equals(paramPage)?"<b>":"");
						out.print("<a href=" + request.getContextPath() + "/stats"); 
						out.print("?level=" + paramLevel + "&type=" + paramType);
						out.print("&page=" + nextPage(childNode));
						out.println(params + ">");
						out.println(element.getAttribute("label"));
						out.println("</a>");
						//out.println(element.getAttribute("id").equals(menuID)?"</b>":"");
						out.println(pageID(childNode).equals(paramPage)?"</b>":"");
						out.println("</td>");
						out.println("</tr>");
			
						// Visit child node
						list(childNode, level+1, out, request, paramLevel, paramType, paramPage, params);
					}
		        }
	    	}
	    }
	}
}
public boolean isParent(Node node, String paramPage) {
	boolean res = false;
	Element element = (Element)node;
	NodeList temp = element.getElementsByTagName("page");
	for (int i=0; i<temp.getLength(); i++) {
		Element e = (Element)temp.item(i);
		if (e.getAttribute("id").equals(paramPage)) {
			res = true;
		}
	}
	return res;
}
public String nextPage(Node node) {
	String res = null;
	
	Element elem = (Element)node;
	NodeList pageList = elem.getElementsByTagName("page");
	
	if (pageList.getLength()>0) {
		res = ((Element)pageList.item(0)).getAttribute("id");
	} else {
		res = "";
	}
	return res; 
}
public String pageID(Node node) {
	String res = null;
	try {
		Node n = XPathAPI.selectSingleNode(node,"page");
		if (n != null) {
			res = ((Element)n).getAttribute("id");
		} else {
			res = "";
		}
	} catch (TransformerException e) {
		System.err.println("TransformerException " + e.toString());
	}
	return res;
	
}

%>
