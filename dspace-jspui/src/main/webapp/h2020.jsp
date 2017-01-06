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
					<h2> Directrizes Open Access H2020 </h2>
                    <br />
 <p> Diretrizes  sobre Acesso Aberto às Publicações Científicas e aos Dados de Investigação no  Horizonte 2020</p>
 <p> <br />
   Estas  diretrizes clarificam as regras sobre o acesso aberto aplicadas aos  beneficiários de projetos financiados ou cofinanciados no âmbito do Horizonte  2020. Note-se, no entanto que estas orientações não se aplicam na sua totalidade às ações financiadas pelo Conselho Europeu de Investigação (ERC)</p> <br />
<p>   Para  obter informações e orientação sobre a implementação do Acesso Aberto e do  Piloto de Dados Abertos no ERC, deve consultar as Diretrizes de implementação  do acesso aberto às publicações científicas e aos dados da investigação em  projetos apoiados pelo Conselho Europeu de Investigação, no âmbito do Horizonte  2020 ou contacte: &nbsp; <a href="mailto:erc-open-access@ec.europa.eu">erc-open-access@ec.europa.eu</a>.</p>
 <p>
   <a href="mailto:open-access@ec.europa.eu">Directrizes - Versão  PDF</a></p>
<br />
        
   

		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
        <h2> Guidelines Open Access H2020 </h2> <br />
        <p>Guidelines on Open Access to Scientific  Publications and Research Data in Horizon 2020 </p>
        <p>
          These guidelines clarify the rules on open  access that cover beneficiaries in projects funded or co-funded under Horizon  2020. Note that these guidelines do not apply to their full extent to actions  funded by the European Research Council (ERC). </p>
        <p>
          For information and guidance on  implementation of Open Access and the Open Research Data Pilot at the ERC,  please see the Guidelines on the Implementation of Open Access to Scientific  Publications and Research Data in Projects supported by the European Research  Council under Horizon 2020 or contact <a href="mailto:erc-open-access@ec.europa.eu">erc-open-access@ec.europa.eu</a>. </p>
         
           <p>
   <a href="http://ec.europa.eu/research/participants/data/ref/h2020/grants_manual/hi/oa_pilot/h2020-hi-oa-pilot-guide_en.pdf">Guidelines PDF version </a></p>
        <br />
          <p> </p>
          <br />
       

    
		<%	}%>
		







</dspace:layout>