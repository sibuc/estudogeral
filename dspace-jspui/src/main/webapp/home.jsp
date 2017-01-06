<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Home page JSP
  -
  - Attributes:
  -    communities - Community[] all communities in DSpace
  -    recent.submissions - RecetSubmissions
  --%>

<style>
	div.container.banner,div.container.bread {
    display:none;
}

div.container ol.breadcrumb.btn-success
{
	display:none;
}

main#content div.container.cont
{
padding-left: 0px;
padding-right: 0px;
}

.container
{
	background_color:#FFF;
	max-width: 100%;
}

@media screen and (min-height: 768px){
	footer.navbar.navbar-bottom
{
	

   position: absolute;
   margin-top:10px;
   bottom: 0px;

    
}

}

</style>



<%@page import="org.dspace.core.Utils"%>
<%@page import="org.dspace.content.Bitstream"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>


<%@ page import="org.dspace.core.Context" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Locale"%>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.core.I18nUtil" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.app.webui.components.RecentSubmissions" %>
<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.core.NewsManager" %>
<%@ page import="org.dspace.browse.ItemCounter" %>
<%@ page import="org.dspace.content.Metadatum" %>
<%@ page import="org.dspace.content.Item" %>
<%@ page import="org.dspace.app.webui.servlet.admin.EditCommunitiesServlet" %>


<%
    Community[] communities = (Community[]) request.getAttribute("communities");
   


    Locale sessionLocale = UIUtil.getSessionLocale(request);
    Config.set(request.getSession(), Config.FMT_LOCALE, sessionLocale);
    String topNews = NewsManager.readNewsFile(LocaleSupport.getLocalizedMessage(pageContext, "news-top.html"));
    String sideNews = NewsManager.readNewsFile(LocaleSupport.getLocalizedMessage(pageContext, "news-side.html"));


    boolean feedEnabled = ConfigurationManager.getBooleanProperty("webui.feed.enable");
    String feedData = "NONE";
    if (feedEnabled)
    {
        feedData = "ALL:" + ConfigurationManager.getProperty("webui.feed.formats");
    }
    
   
	
	Context context = UIUtil.obtainContext(request);

        ItemCounter ic = new ItemCounter(context);
        
        Community[] communities2 = Community.findAllTop(context);
        int contagem = 0;
        for (int cc = 0 ; cc < communities2.length; cc++) {
            contagem =  contagem + ic.getCount(communities2[cc]);
        }
		
	
		int openaccess = contagem * 82 / 100;
		int downloads = 343914;
		
		
	

    RecentSubmissions submissions = (RecentSubmissions) request.getAttribute("recent.submissions");
%>

<%
	 	String current_lang = UIUtil.getSessionLocale(request).toString();
		/*String open_access="";
		if (current_lang.equals("pt_PT")) 
			{	
				open_access="Acesso Aberto";
			}
		if (current_lang.equals("en")) 
			{	
				open_access="Open Access";
			}*/
		
%>

<dspace:layout locbar="nolink" titlekey="jsp.home.title" feedData="<%= feedData %>">
<div class="row row_jumbo">
	<div class="jumbo-center">
    <div class="col-md-6 intro-text">		
				<!--<h1>Estudo Geral</h1>-->
                <br>
                <h2 class="desc-hd"><fmt:message key="jsp.home.welcome"/></h2>
                <p class="desc"><fmt:message key="jsp.home.welcomemsg"/></p>
                <br />
                <div class="col-md-2 stat" >
               <p class="huge"><%= contagem %></p><p id="num-docs"><fmt:message key="jsp.home.documentos"/><br /></p>
               </div>
               <div class="col-md-2 stat" >
               <p class="huge"><%= openaccess %></p><p id="num-docs"><fmt:message key="jsp.home.documentosoa"/><br /></p>
               </div>
               <div class="col-md-2 stat" >
               <p class="huge"><%= downloads %></p><p id="num-docs"><fmt:message key="jsp.home.documentosdown"/><br /></p>
               </div>
            
            <!--<div class="l" style="width:90px;margin-right:16px;">
                    <div class="huge" style="color:#104b7d;letter-spacing:-1px;">224,464</div>
                    <div style="line-height:18px;">publications</div>
			</div>-->
        	 <!--<img alt="Repositório Científico da Universidade de Coimbra" class="img-responsive" src="<%= request.getContextPath() %>/image/logoeg.png" /> -->
        		
		</div>    

	<div class="col-md-6 jumbotron photo">
        <%= topNews %>
	</div>
    </div>
    </div>
               
<div class="row" id="sub-content">
<%
if (submissions != null && submissions.count() > 0)
{
%>
        <div class="contentsub">
        <div class="col-md-6 col-6-rec-sub">  
       
        <!--<div id="recent-submissions-carousel" class="panel-heading carousel slide">-->
        <div id="recent-submissions" class="listsubmissions">
          <h3 class="list-header"><fmt:message key="jsp.collection-home.recentsub"/>
                
         
         
          </h3>
          
		  <!-- Wrapper for slides -->
		  <!--div class="carousel-inner"-->
          <ul class="list-group">
		    <%
		    boolean first = true;
		    for (Item item : submissions.getRecentSubmissions())
		    {
		        Metadatum[] dcv = item.getMetadata("dc", "title", null, Item.ANY);
		        String displayTitle = "Untitled";
		        if (dcv != null & dcv.length > 0)
		        {
		            displayTitle = Utils.addEntities(dcv[0].value);
		        }
		        dcv = item.getMetadata("dc", "description", "abstract", Item.ANY);
		        String displayAbstract = "";
		        if (dcv != null & dcv.length > 0)
		        {
		            displayAbstract = Utils.addEntities(dcv[0].value);
		        }
		%>
        
     
  

<li class="list-group-item"><a href="<%= request.getContextPath() %>/handle/<%=item.getHandle() %>"><%= displayTitle%></a></li>
        
        
		<%

		     }
			 
		%>
        </ul>
             <%
    if(feedEnabled)
    {
	    	String[] fmts = feedData.substring(feedData.indexOf(':')+1).split(",");
	    	String icon = null;
	    	int width = 0;
	    	for (int j = 0; j < fmts.length; j++)
	    	{
	    		if ("rss_1.0".equals(fmts[j]))
	    		{
	    		   icon = "rss1.gif";
	    		   width = 80;
				   %>
	  <!--<a href="<%= request.getContextPath() %>/feed/<%= fmts[j] %>/site"><img src="<%= request.getContextPath() %>/image/<%= icon %>" alt="RSS Feed" width="<%= width %>" height="15" style="margin: 3px 0 3px" /></a>-->
	<%
	    		}
	    		else if ("rss_2.0".equals(fmts[j]))
	    		{
	    		   icon = "rss2.gif";
	    		   width = 80;
				   %>
	   <a href="<%= request.getContextPath() %>/feed/<%= fmts[j] %>/site"><img src="<%= request.getContextPath() %>/image/<%= icon %>" alt="RSS Feed" width="<%= width %>" height="15" style="margin: 3px 0 3px" /></a>
	<%
	    		}
	    		else
	    	    {
	    	       icon = "atom1.gif";
	    	       width = 80;
				   %>
	    <a href="<%= request.getContextPath() %>/feed/<%= fmts[j] %>/site"><img src="<%= request.getContextPath() %>/image/<%= icon %>" alt="ATOM Feed" width="<%= width %>" height="15" style=" margin: 3px 0 3px" /></a>
	<%
	    	    }
	%>
	  <!--  <a href="<%= request.getContextPath() %>/feed/<%= fmts[j] %>/site"><img src="<%= request.getContextPath() %>/image/<%= icon %>" alt="RSS Feed" width="<%= width %>" height="15" style="margin: 3px 0 3px" /></a>-->
	<%
	    	}
	    }
	%>
		  </div>



		 
     </div><div class="col-md-3 hp-sidebar estudo-geral-ul">
     <h3 class="list-header">Estudo Geral<!--<fmt:message key="jsp.collection-home.highlights"/>-->
             
          </h3>
    
         
        <ul class="list-group">
			<li class="list-group-item no-border"><a href="/sobre.jsp"><fmt:message key="jsp.home.about"/></a></li>
			<li class="list-group-item no-border"><a href="/politica.jsp"><fmt:message key="jsp.home.politica"/></a></li>
			<li class="list-group-item no-border"><a href="/depositar.jsp"><fmt:message key="jsp.home.deposita"/></a></li>
			<li class="list-group-item no-border"><a href="/faqs.jsp">FAQ's</a></li>
        </ul>
             
    
</div>
<div class="col-md-3 hp-sidebar acesso-aberto-ul">
      <h3 class="list-header"><fmt:message key="jsp.home.highlights-oa"/><!--<fmt:message key="jsp.collection-home.highlights"/>-->
             
          </h3>
    
         
        <ul class="list-group">
			<li class="list-group-item no-border"><a href="/fct.jsp"><fmt:message key="jsp.home.fct"/></a></li>
			<li class="list-group-item no-border"><a href="/h2020.jsp"><fmt:message key="jsp.home.h2020"/></a></li>
			<li class="list-group-item no-border"><a href="/direitos.jsp"><fmt:message key="jsp.home.diraut"/></a></li>
            <li class="list-group-item no-border"><a href="/ligacoes.jsp"><fmt:message key="jsp.home.ligut"/></a></li>
			
        </ul>
             
	    
		  
</div>

</div>
<%
}
%>

</div>
<div class="container row">
<%
if (communities != null && communities.length != 0)
{
%>
	<div class="col-md-4">		
               <h3><fmt:message key="jsp.home.com1"/></h3>
                <p><fmt:message key="jsp.home.com2"/></p>
				<div class="list-group">
<%
	boolean showLogos = ConfigurationManager.getBooleanProperty("jspui.home-page.logos", true);
    for (int i = 0; i < communities.length; i++)
    {
%><div class="list-group-item row">
<%  
		Bitstream logo = communities[i].getLogo();
		if (showLogos && logo != null) { %>
	<div class="col-md-3">
        <img alt="Logo" class="img-responsive" src="<%= request.getContextPath() %>/retrieve/<%= logo.getID() %>" /> 
	</div>
	<div class="col-md-9">
<% } else { %>
	<div class="col-md-12">
<% }  %>		
		<h4 class="list-group-item-heading"><a href="<%= request.getContextPath() %>/handle/<%= communities[i].getHandle() %>"><%= communities[i].getMetadata("name") %></a>
<%
        if (ConfigurationManager.getBooleanProperty("webui.strengths.show"))
        {
%>
		<span class="badge pull-right"><%= ic.getCount(communities[i]) %></span>
<%
        }

%>
		</h4>
		<p><%= communities[i].getMetadata("short_description") %></p>
    </div>
</div>                            
<%
    }
%>
	</div>
	</div>
<%
}
%>
	<%
    	int discovery_panel_cols = 8;
    	int discovery_facet_cols = 4;
    %>
	<%@ include file="discovery/static-sidebar-facet.jsp" %>
</div>

<div class="row">
	<%@ include file="discovery/static-tagcloud-facet.jsp" %>
</div>
	
<!--</div>-->
</dspace:layout>
