<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Default navigation bar
--%>

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/dspace-tags.tld" prefix="dspace" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.dspace.core.Context" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.content.Collection" %>
<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.eperson.EPerson" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.browse.BrowseIndex" %>
<%@ page import="org.dspace.browse.BrowseInfo" %>
<%@ page import="java.util.Map" %>
<%-- SDUM language switch --%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Enumeration"%>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>
<%@ page import="org.dspace.core.I18nUtil" %>
<%-- SDUM language switch --%>

<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Element" %>
<%@ page import="org.w3c.dom.Node" %>
<%@ page import="org.w3c.dom.NodeList" %>
<%@ page import="org.xml.sax.SAXException" %>
<%@ page import="javax.xml.transform.TransformerException" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.dspace.app.webui.util.stats.StatsReader" %>

<%
    // Is anyone logged in?
    EPerson user = (EPerson) request.getAttribute("dspace.current.user");

    // Is the logged in user an admin
    Boolean admin = (Boolean)request.getAttribute("is.admin");
    boolean isAdmin = (admin == null ? false : admin.booleanValue());

    // Get the current page, minus query string
    String currentPage = UIUtil.getOriginalURL(request);
    int c = currentPage.indexOf( '?' );
    if( c > -1 )
    {
        currentPage = currentPage.substring( 0, c );
    }

    // E-mail may have to be truncated
    String navbarEmail = null;

    if (user != null)
    {
        navbarEmail = user.getEmail();
    }
    
    Locale[] supportedLocales = I18nUtil.getSupportedLocales();
    Locale sessionLocale = UIUtil.getSessionLocale(request);
    Config.set(request.getSession(), Config.FMT_LOCALE, sessionLocale);
    Enumeration enParams = request.getParameterNames();

	String paramLevel = request.getParameter("level");
	String paramType = request.getParameter("type");
	String paramPage = request.getParameter("page");
	String paramTab = request.getParameter("tab");

	Map parameters = new HashMap();
	Map newParameters = new HashMap();
	String params = "";

	try 
	{
		java.util.Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) 
		{
			String param = (String) enu.nextElement();
			String value = request.getParameter(param);
			if (!param.equals("object") && 
				 !param.equals("object-id") && 
				 !param.equals("no-form")) 
			{
				parameters.put(param,value);
				newParameters.put(param,value);
				if(!param.equals("level") && 
					!param.equals("type") && 
					!param.equals("page") && 
					!param.equals("menu") && 
					!param.equals("tab")) 
				{
                params += "&" + param + "=" + value;
				}
			}
		}
	} catch(java.util.NoSuchElementException e) {
		System.out.println ("" + e.getMessage() + "\n");
	}


	Context context = UIUtil.obtainContext(request);
	StatsReader reader = new StatsReader(request, context);
	Document statsDoc = reader.getDocument();	

%>
           <div class="nav-cent">  <!--navbar-header-->
         <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
           <span class="icon-bar"></span>
           <span class="icon-bar"></span>
           <span class="icon-bar"></span>
         </button>
         <div id="nav-content">
         <a class="navbar-brand eg-brand" href="<%= request.getContextPath() %>/"><img height="77" src="<%= request.getContextPath() %>/image/eg2.png" alt="EG logo" /></a>
        
           
            <div id="home_search" class="header-search">
            <form method="get" action="<%= request.getContextPath() %>/simple-search" class="navbar-form-sr navbar-form navbar-right header-search-form">  <%--navbar-right--%>
                    <div class="form-group">
                      <input type="text" class="form-control input-search" placeholder="<fmt:message key="jsp.layout.navbar-default.search"/>" name="query" id="tequery" size="25"/>
                    </div>
                    <button type="submit"  class="btn btn-search"><span class="glyphicon glyphicon-search"></span></button>  <!-- btn-primary--> 
            <%--               <br/><a href="<%= request.getContextPath() %>/advanced-search"><fmt:message key="jsp.layout.navbar-default.advanced"/></a>
            <%
                        if (ConfigurationManager.getBooleanProperty("webui.controlledvocabulary.enable"))
                        {
            %>        
                          <br/><a href="<%= request.getContextPath() %>/subject-search"><fmt:message key="jsp.layout.navbar-default.subjectsearch"/></a>
            <%
                        }
            %> --%>
                </form>	
            </div>
		

		
	
	<nav id="nav_head" class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
		<ul class="nav navbar-nav">

			<%
			NodeList listLevels = statsDoc.getElementsByTagName("level");
			if (listLevels.getLength()>0) 
			{
				Element stat = (Element)XPathAPI.selectSingleNode(statsDoc, "statistics");
				String levelTitle = stat.getAttribute("level-title");
				String levelSelected = "--";
				for (int i = 0; i < listLevels.getLength(); i++)
				{
					Element elem = (Element)listLevels.item(i);
					if (elem.getAttribute("id").equals(paramLevel))
					{
						levelSelected = elem.getAttribute("label");
					}
				}
				%>

				<li class="dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-stats"></span> <%= levelTitle %>: <%= levelSelected %> <b class="caret"></b>
				</a>
		<ul class="dropdown-menu">
		<%
		for (int i = 0; i < listLevels.getLength(); i++) 
		{
			Element elem = (Element)listLevels.item(i);
			if (elem.getAttribute("id").equals(paramLevel))
			{
				%>
				<li class="disabled">
				<%
			}
			else
			{	
				%>
				<li>
				<%
			}
			%>
			
			<a href="<%= request.getContextPath() %>/stats?level=<%=elem.getAttribute("id") + "&page=" + nextPage(listLevels.item(i))%><%=params%>">
			<%= elem.getAttribute("label") %>
			</a>
			</li>	
			<%
		}
		%>
		</ul>
		</li>
		<%
	}
	%>


	<%
	if (paramLevel != null) // || nodePageTab != null) ver se e preciso esta condicao
	{
		NodeList listTypes = XPathAPI.selectNodeList(statsDoc, "statistics/level[@id='" + paramLevel + "']/type");
		for (int i = 0; i < listTypes.getLength(); i++) 
		{
			Element elementNode = (Element)listTypes.item(i);
			%>
			<li class="dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><%=elementNode.getAttribute("label")%> <b class="caret"></b>
				</a>
				<ul class="dropdown-menu">
				<%
				list(listTypes.item(i),1, out, request, paramLevel, elementNode.getAttribute("id"), paramPage, params);
				%>
				</ul>
			</li>
			<%
		}
	}
	%>


             
			 </ul>
			</li>
			
	   </ul>





       <div class="nav navbar-nav navbar-right">
		<ul class="nav navbar-nav navbar-right">
      
      <!-- SDUM LANGUAGE SWITCHER -->
      <% if (supportedLocales != null && supportedLocales.length > 1)
      {
      %>
         <form method="get" name="repost" action="" class="nav navbar-nav navbar-right">
                                      <%
                                      while (enParams.hasMoreElements())
                                      {
                                          String nameParam = (String)enParams.nextElement();
                                          String valueParam = request.getParameter(nameParam);
                                          if (!nameParam.equals("locale"))
                                          {
                                          %>
                                             <input type="hidden" name="<%= nameParam %>" value="<%=valueParam %>" />
                                          <%
                                          }
                                      }
                                      %>

            <input type ="hidden" name ="locale"/>
         </form>
         <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 
               <%= sessionLocale.getDisplayLanguage(sessionLocale) %> <b class="caret"></b>
            </a>
            <ul class="dropdown-menu">
               <%
                  for (int i = supportedLocales.length-1; i >= 0; i--)
                  {
                     if (!supportedLocales[i].getLanguage().equals(sessionLocale.getLanguage()))
                     {
                     %>
                        <li><a style="cursor:pointer" onclick="javascript:document.repost.locale.value='<%=supportedLocales[i].toString()%>';document.repost.submit();">
                               <%= supportedLocales[i].getDisplayLanguage(sessionLocale)%>
                            </a>
                        </li>
                     <%
                     }
                  }
               %>
            </ul>
         </li>
      <%
      }
      %>
      <!-- SDUM LANGUAGE SWITCHER -->

         <li class="dropdown">
         <%
    if (user != null)
    {
		%>
		<a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> <fmt:message key="jsp.layout.navbar-default.loggedin">
		      <fmt:param><%= StringUtils.abbreviate(navbarEmail, 20) %></fmt:param>
		  </fmt:message> <b class="caret"></b></a>
		<%
    } else {
		%>
             <a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> <b class="caret"></b></a>
             
	<% } %>             
             <ul class="dropdown-menu">
               <li><a href="<%= request.getContextPath() %>/mydspace"><fmt:message key="jsp.layout.navbar-default.users"/></a></li>
               <li><a href="<%= request.getContextPath() %>/subscribe"><fmt:message key="jsp.layout.navbar-default.receive"/></a></li>
               <li><a href="<%= request.getContextPath() %>/profile"><fmt:message key="jsp.layout.navbar-default.edit"/></a></li>

		<%
		  if (isAdmin)
		  {
		%>
			   <li class="divider"></li>  
               <li><a href="<%= request.getContextPath() %>/dspace-admin"><fmt:message key="jsp.administer"/></a></li>
		<%
		  }
		  if (user != null) {
		%>
		<li><a href="<%= request.getContextPath() %>/logout"><span class="glyphicon glyphicon-log-out"></span> <fmt:message key="jsp.layout.navbar-default.logout"/></a></li>
		<% } %>
             </ul>
           </li>
          </ul>

</div>
    </nav>

<%!
public void list(Node node, int level, JspWriter out, HttpServletRequest request, String paramLevel, String paramType, String paramPage, String params) throws Exception {

        Element temp = (Element)node;
        String np = nextPage(node);



//        if (level == 1 || np.equals(paramPage) || isParent(node,paramPage)) {
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

if (hasSubmenus(childNode))
{
	out.println("<li class=\"dropdown dropdown-submenu\">");
	out.print("<a href=\"" + request.getContextPath() + "/stats");
   out.print("?level=" + paramLevel + "&type=" + paramType);
   out.print("&page=" + nextPage(childNode));
   out.println(params + "\">");
   out.println(element.getAttribute("label"));
   out.println("</a>");
	out.println("<ul class=\"dropdown-menu\">");
	list(childNode, level+1, out, request, paramLevel, paramType, paramPage, params);
	out.println("</ul>");
   out.println("</li>");
}
else
{
	out.println("<li>");
	out.print("<a href=\"" + request.getContextPath() + "/stats");
	out.print("?level=" + paramLevel + "&type=" + paramType);
	out.print("&page=" + nextPage(childNode));
	out.println(params + "\">");
	out.println(element.getAttribute("label"));
	out.println("</a>");
	out.println("</li>");
}


                                        }
                        }
                }
            }
        //}
}

public boolean hasSubmenus(Node node)
{
	boolean res = false;

	NodeList list = node.getChildNodes();

	for (int i=0; i<list.getLength(); i++) 
	{
		// Get child node
		Node childNode = list.item(i);

		if (childNode.getNodeType() == Node.ELEMENT_NODE) 
		{
			Element element = (Element)list.item(i);
			if (element.getTagName().equals("menu")) 
			{
				String isActive = element.getAttribute("active");
            if (isActive == null || "".equals(isActive)) isActive="true";

            if (isActive.equals("true")) 
				{
					res = true;
				}
			}
		}
	}
	return res;
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