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
					<h2> Ligações Úteis </h2>
                    <br />
                    <p><strong>Repositórios e Outros Recursos</strong></p>
                    <p><br />
                    <p>  OpenDOAR  - The Directory of Open Acces Repositories <a href="http://www.opendoar.org/" target="_blank">http://www.opendoar.org/</a></p> 
                    <p>  ROAR  - Registry of Open Access Repositories - <a href="http://roar.eprints.org/" target="_blank">http://roar.eprints.org/</a></p>
                    <p>  Sherpa  Search - simple full-text search of UK repositories <a href="http://www.sherpa.ac.uk/repositories/sherpasearchalluk.html" target="_blank">http://www.sherpa.ac.uk/repositories/sherpasearchalluk.html</a></p>
                    <p>  Networked  Digital Library of Theses and Dissertations (NDLTD) - <a href="http://www.ndltd.org/" target="_blank">http://www.ndltd.org/</a></p>
                    <p>  The  OAIster database - <a href="http://www.oclc.org/oaister.en.html" target="_blank">http://www.oclc.org/oaister.en.html</a><p>
                    <p>  PubMed&nbsp;-&nbsp;<a href="https://www.ncbi.nlm.nih.gov/pubmed" target="_blank">https://www.ncbi.nlm.nih.gov/pubmed</a></p>
                    <p>  SPARC (the Scholarly Publishing  and Academic Resources Coalition) -&nbsp;<a href="http://sparcopen.org/" target="_blank">http://sparcopen.org/</a></p>
                    <p>RCAAP&nbsp;- <a href="http://www.rcaap.pt" target="_blank">http://www.rcaap.pt</a></p>
                    <p>  Ciência Aberta - <a href="http://www.ciencia-aberta.pt" target="_blank">http://www.ciencia-aberta.pt</a>  </p>
                    <p>DOAJ&nbsp;-<a href="https://doaj.org/" target="_blank">Directory of Open Access Journals (DOAJ)</a></p>
                    <br />
 <p> </p>
 <p> </p>
 <br />
        
   

		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
        <h2> Useful links </h2>
<br />

                    <p>  OpenDOAR  - The Directory of Open Acces Repositories <a href="http://www.opendoar.org/" target="_blank">http://www.opendoar.org/</a></p> 
                    <p>  ROAR  - Registry of Open Access Repositories - <a href="http://roar.eprints.org/" target="_blank">http://roar.eprints.org/</a></p>
                    <p>  Sherpa  Search - simple full-text search of UK repositories <a href="http://www.sherpa.ac.uk/repositories/sherpasearchalluk.html" target="_blank">http://www.sherpa.ac.uk/repositories/sherpasearchalluk.html</a></p>
                    <p>  Networked  Digital Library of Theses and Dissertations (NDLTD) - <a href="http://www.ndltd.org/" target="_blank">http://www.ndltd.org/</a></p>
                    <p>  The  OAIster database - <a href="http://www.oclc.org/oaister.en.html" target="_blank">http://www.oclc.org/oaister.en.html</a><p>
                    <p>  PubMed&nbsp;-&nbsp;<a href="https://www.ncbi.nlm.nih.gov/pubmed" target="_blank">https://www.ncbi.nlm.nih.gov/pubmed</a></p>
                    <p>  SPARC (the Scholarly Publishing  and Academic Resources Coalition) -&nbsp;<a href="http://sparcopen.org/" target="_blank">http://sparcopen.org/</a></p>
                    <p>RCAAP&nbsp;- <a href="http://www.rcaap.pt" target="_blank">http://www.rcaap.pt</a></p>
                    <p>  Ciência Aberta - <a href="http://www.ciencia-aberta.pt" target="_blank">http://www.ciencia-aberta.pt</a>  </p>
                    <p>DOAJ&nbsp;-<a href="https://doaj.org/" target="_blank">Directory of Open Access Journals (DOAJ)</a></p>
                    <br />
 <p> </p>
          <p> </p>
          <br />
       

    
		<%	}%>
		







</dspace:layout>