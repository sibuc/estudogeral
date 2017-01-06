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
	max-width: 1970px;
}

.cont
{
	background_color:#FFF;
	max-width: 1170px;
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
    
    ItemCounter ic = new ItemCounter(UIUtil.obtainContext(request));

    RecentSubmissions submissions = (RecentSubmissions) request.getAttribute("recent.submissions");
%>



<dspace:layout locbar="nolink" titlekey="jsp.home.title" feedData="<%= feedData %>">


<%
	 	String current_lang = UIUtil.getSessionLocale(request).toString();
		
		if (current_lang.equals("pt_PT")) 
			{%>
					<h2> Política FCT </h2>
                   <br /><p>
                      A FCT defende a disponibilização dos resultados da  investigação científica através da internet, de forma aberta, livre e sem  custos para o utilizador. </p><br /><p>As sua Políticas de Acesso Aberto entraram em vigor a  5 de maio de 2014.</p><br />
                    <p>  Veja mais em: 
                    <a href="https://www.fct.pt/dsi/eciencia/index.phtml.pt" target="_blank">FCT</a><br /></p>
 <p> </p>
 <br />
        
   

		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
        <h2> FCT Policy </h2>
<br />
          <p>FCT believes that the  results of scientific research should be widely disseminated and freely  available.</p> <br />
<p>The FCT Policies on  Open Access came into force on 5 May 2014. </p><br />
Read more here: <a href="https://www.fct.pt/dsi/eciencia/index.phtml.en" target="_blank">FCT</a></p>
          <p> </p>
          <br />
       

    
		<%	}%>
		







</dspace:layout>