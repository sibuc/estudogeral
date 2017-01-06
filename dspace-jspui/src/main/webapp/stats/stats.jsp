<%--
  - stats.jsp
  -
  - Version: $$
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
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.xerces.parsers.DOMParser" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
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

<script language="JavaScript" src="<%= request.getContextPath() %>/stats/calendar1.js"></script>

<%
String paramLevel = request.getParameter("level");
String paramType = request.getParameter("type");
String paramPage = request.getParameter("page");
String paramTab = request.getParameter("tab");

String noForm = request.getParameter("no-form");
String noMenu = request.getParameter("no-menu");

String object = request.getParameter("object");
String objectID = request.getParameter("object-id");

Map parameters = new HashMap();
Map newParameters = new HashMap();
String params = "";

//parameters
try {
	java.util.Enumeration enu = request.getParameterNames();
	while (enu.hasMoreElements()) {    
        String param = (String) enu.nextElement();
        String value = request.getParameter(param);
        if (!param.equals("object") && !param.equals("object-id") && !param.equals("no-form")) {
            parameters.put(param,value);
            newParameters.put(param,value);
            if(!param.equals("level") && !param.equals("type") && !param.equals("page") && !param.equals("menu") && !param.equals("tab")) {
                params += "&" + param + "=" + value;
            }
        }
    }
} catch(java.util.NoSuchElementException e) {
    System.out.println ("" + e.getMessage() + "\n");
}

//Determinate the correct value for objectID by Value of objectID
//value of objectID: "general", "community", "collection", "item"; null
//if null: objectID is correct for level
Context c = UIUtil.obtainContext(request);
objectID = StatsUtil.getIdFrom(c, object, objectID, paramLevel);
object = paramLevel;

// set value of objectID TODO: (REVER)
if (object != null && objectID!= null && !objectID.equals("") && !objectID.equals("-1")) {
    params += "&" + "object" + "=" + object;
    params += "&" + "object-id" + "=" + objectID;
    parameters.put("object", object);
    parameters.put("object-id", objectID);
    newParameters.put("object", object);
    newParameters.put("object-id", objectID);
}

// set default dates if do not exist
if (parameters.containsKey("page")) {
	if (!parameters.containsKey("start")) {
		Date date = new Date();
		date.setDate(1);
		String start =new SimpleDateFormat("dd-MM-yyyy").format(date);
	    params += "&" + "start" + "=" + start;
	    parameters.put("start", start);
	    newParameters.put("start", start);
	}
	if (!parameters.containsKey("end")) {
	    Date today = new Date();
	    String end = new SimpleDateFormat("dd-MM-yyyy").format(today);
	    params += "&" + "end" + "=" + end;
	    parameters.put("end", end);
	    newParameters.put("end", end);
	}
	if (!parameters.containsKey("pyear")) {
	    Date today = new Date();
	    String year = new SimpleDateFormat("yyyy").format(today);
	    params += "&" + "pyear" + "=" + year;
	    parameters.put("pyear", year);
	    newParameters.put("pyear", year);
	}
	if (!parameters.containsKey("pmonth")) {
	    Date today = new Date();
	    String year = new SimpleDateFormat("MM").format(today);
	    params += "&" + "pmonth" + "=" + year;
	    parameters.put("pmonth", year);
	    newParameters.put("pmonth", year);
	}
	if (!parameters.containsKey("anoinicio")) {
	    Date today = new Date();
	    String year = new SimpleDateFormat("yyyy").format(today);
	    params += "&" + "anoinicio" + "=" + year;
	    parameters.put("anoinicio", year);
	    newParameters.put("anoinicio", year);
	}
	if (!parameters.containsKey("anofim")) {
	    Date today = new Date();
	    String year = new SimpleDateFormat("yyyy").format(today);
	    params += "&" + "anofim" + "=" + year;
	    parameters.put("anofim", year);
	    newParameters.put("anofim", year);
	}
	if (!parameters.containsKey("mesinicio")) {
	    Date today = new Date();
	    String year = "01";
	    params += "&" + "mesinicio" + "=" + year;
	    parameters.put("mesinicio", year);
	    newParameters.put("mesinicio", year);
	}
	if (!parameters.containsKey("mesfim")) {
	    Date today = new Date();
	    String year = new SimpleDateFormat("MM").format(today);
	    params += "&" + "mesfim" + "=" + year;
	    parameters.put("mesfim", year);
	    newParameters.put("mesfim", year);
	}
	
}

StatsReader reader = new StatsReader(request, c);

String pageTitle="";
String pageDescription="";


Node nodePage = null;
Node nodePageTab = null;
	
Document statsDoc = reader.getDocument();


    
    String xpath = null;
    
    if (parameters.containsKey("type")) {
    	xpath = "/statistics/level[@id='" + paramLevel + "']/type[@id='" + paramType + "']//page[@id='" + paramPage + "']";
    } else if (parameters.containsKey("level")) {
    	xpath = "/statistics/level[@id='" + paramLevel + "']//page[@id='" + paramPage + "']";
    } else {
    	xpath = "/statistics//page";
    }
    
	nodePage = XPathAPI.selectSingleNode(statsDoc, xpath);

	if (nodePage != null) {
		String pageAcessGroups = ((Element)nodePage).getAttribute("access-group");
		Node nodeTitle = XPathAPI.selectSingleNode(nodePage, "title");
		if (nodeTitle != null) {
			pageTitle = nodeTitle.getFirstChild().getNodeValue();
		}
		//pageTitle = (String) XPathAPI.selectSingleNode(nodePage, "title")
		//						.getFirstChild().getNodeValue();
		Node nodeDescription = XPathAPI.selectSingleNode(nodePage, "description");
		if (nodeDescription != null) {
			pageDescription = nodeDescription.getFirstChild().getNodeValue();
		}
		//pageDescription = (String) XPathAPI.selectSingleNode(nodePage, "description")
		//									.getFirstChild().getNodeValue(); 

		if (parameters.containsKey("tab")) {
			nodePageTab = XPathAPI.selectSingleNode(nodePage, "tab[@id='" + paramTab + "']");
		} else {
			nodePageTab = XPathAPI.selectSingleNode(nodePage, "tab");
		}
		if (nodePageTab == null) {
			nodePageTab = nodePage;
			paramTab="";
		} else {
			paramTab = ((Element)nodePageTab).getAttribute("id");
		}
	}  

%>
<dspace:layout style="stats" titlekey="jsp.home.title" locbar="off">

<% if (nodePageTab != null) 
{ %>
<div class="panel panel-primary">
<div class="panel-heading"><b><%=pageTitle%></b></div>
<br/>
	<div class="container">
      <div class="row">
         <div class="col-md-12">
				<%=pageDescription%>
         </div>
      </div>
		<% 
		if (!paramTab.equals(""))
		{
			String tabTitle = ((Element)nodePage).getAttribute("tab-title");
			NodeList listTabs = XPathAPI.selectNodeList(nodePage,"tab");
			if (listTabs != null && listTabs.getLength()>0 && tabTitle!="") 
			{
				out.println("<hr class='divider' />");
				out.println("<div class=\"row\">");
				out.println("<div class=\"col-md-1\">");
				out.println(tabTitle);
				 out.println("</div>");
				out.println("<div class=\"col-md-11\">");
				for (int i = 0; i < listTabs.getLength(); i++){
					String tabID = ((Element)listTabs.item(i)).getAttribute("id");
              	String tabLabel = ((Element)listTabs.item(i)).getAttribute("label");

					if (tabID.equals(paramTab)) 
					{
						out.println("<span class='btn btn-danger active'>" + tabLabel + "</span>");
					} 
					else 
					{
						out.print("<a class='btn btn-default' href=\"" + request.getContextPath() + "/stats");
						out.print("?level=" + paramLevel + "&type=" + paramType);
						out.print("&page=" + paramPage + "&tab=" + tabID + params + "\">");
						out.println(tabLabel + "</a>");
					}
				}
				out.println("</div>");
				out.println("</div>");
				out.println("<hr class='divider' />");
			}
			else
			{
				out.println("<br/>");
			}
		}
		if (!"true".equals(noForm)) {
			request.setAttribute("parameters", parameters);
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-md-12\">");
			%>
			<stats:parameters pageTab="<%= nodePageTab %>"/>
			<%
			out.println("</div>");
			out.println("</div>");
			request.removeAttribute("parameters");
		}
		%>
		<%
		if(object != null && 
			objectID !=null && 
			!objectID.equals("") &&
			!StatsUtil.isAuthorized(c, object, objectID))
		{
			out.println("<div class=\"row\">");
			out.println("<div class=\"col-md-12\">");
			out.println("You do not have authorization to view statistics for this " + object + ".");
			out.println("</div>");
			out.println("</div>");
		}
		else
		{
			request.setAttribute("parameters", newParameters);
			%>
			<stats:data pageTab="<%= nodePageTab %>"/>
			<%
			request.removeAttribute("parameters");
		}
		%>
	</div>
<%
}
%>
</div>
</dspace:layout>

