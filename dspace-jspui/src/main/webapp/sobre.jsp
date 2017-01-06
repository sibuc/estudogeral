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
					<h2> Sobre o repositório </h2>
                    <br />
<p>O Estudo  Geral é o repositório digital da Universidade de Coimbra (UC). Foi constituído  com o objetivo de preservar, divulgar e dar acesso à produção científica da UC,  aumentando a sua visibilidade e a dos seus investigadores. É parte integrante  do RCCAP - Repositório Científico de Acesso Aberto de Portugal. </p>
<p><a href="http://193.137.200.162/">ESTUDO GERAL</a> é o nome  do Repositório Digital da Produção Científica da Universidade de Coimbra (UC),  cujo objetivo consiste em divulgar conteúdos digitais de natureza científica de  autores ligados à Universidade de Coimbra. A sua criação insere-se no movimento  de Acesso Livre (Open Access) à literatura científica, que deu origem a várias  Declarações e Recomendações, das quais se destacam a <a href="http://www.soros.org/openaccess/">Budapest Open Access  Initiative</a> e a <a href="http://oa.mpg.de/openaccess-berlin/berlindeclaration.html">Declaração de Berlim sobre o Acesso Livre ao Conhecimento</a>. </p>
<p>O Conselho  de Reitores das Universidades Portuguesas (CRUP), que subscreveu a Declaração  de Berlim, emitiu, em Novembro de 2006, uma Declaração em que manifesta a sua  adesão aos princípios norteadores deste movimento e em que recomenda a criação  de Repositórios Institucionais e a definição de políticas institucionais de  depósito das suas publicações científicas e académicas. </p>
<p>A  Universidade de Coimbra subscreveu estes princípios no início de 2007 e, em  junho de 2008, apresentou publicamente o seu repositório digital, manifestando,  deste modo, o interesse em aumentar a sua presença na rede informática mundial,  à semelhança de outras grandes universidades nacionais e internacionais, sendo  cada vez mais - e também por essa via - um emissor de conhecimento e cultura. </p>
<p>O ESTUDO  GERAL pretende, ainda, aumentar a visibilidade, acessibilidade e difusão dos  resultados da investigação científica e, mais em geral, da atividade académica  da Universidade de Coimbra, nomeadamente dando a conhecer não só os trabalhos  de pós-graduação como a restante produção científica da comunidade  universitária e, sempre que possível, disponibilizando o seu texto integral à  mesma comunidade e à sociedade em geral.</p>
<p>Este  projeto da Universidade de Coimbra é desenvolvido pelo <a href="http://www.uc.pt/sibuc">SIBUC</a> (Serviço  Integrado das Bibliotecas da Universidade de Coimbra), dando cumprimento à  diretiva de integração de todos os recursos digitais da UC enunciada no  Relatório &ldquo;<a href="http://www.uc.pt/sibuc/Pdfs/relatorio">Reorganização e Reestruturação das Bibliotecas da  Universidade de Coimbra</a>&rdquo;, e conta com a colaboração do Instituto de  Investigação Interdisciplinar da Universidade de Coimbra (IIIUC) e da Fundação  para a Ciência e Tecnologia (FCT). </p>
<p>Numa  primeira fase, o arquivo da produção científica foi realizado apenas pelo  SIBUC. Numa segunda fase, e logo que o projeto se abriu à comunidade académica,  e com a organização de comunidades científicas por área do conhecimento, foram  criadas as condições para que os próprios autores pudessem fazer o autodepósito  dos seus documentos.</p>
<p>Aproveitando  a mesma plataforma informática, e com o duplo objetivo de facilitar o acesso e  de promover a desmaterialização dos documentos, o Serviço de Gestão Académica  (SGA) e o SIBUC estabeleceram uma parceria para disponibilizar numa área  reservada a produção científica dos candidatos a Provas de Agregação e  similares, e mais tarde alargarem esta funcionalidade às Provas de  Doutoramento. Deste modo, os elementos do júri , e também o candidato, têm  acesso remoto, mediante password, aos documentos em formato digital. Terminadas  as provas, essa documentação em formato digital passará a ser disponibilizada,  se o autor autorizar, no todo ou em parte, no ESTUDO GERAL.</p>
<p>As  questões de direitos de autor estão devidamente acauteladas. Os autores da  Universidade de Coimbra são encorajados a conceder autorização – não exclusiva  – de afixação no ESTUDO GERAL de documentos em formato eletrónico. Com a  concessão desta licença não‑exclusiva para arquivar e dar acesso ao seu  trabalho, os docentes e investigadores continuam a manter todos os seus  direitos de autor.</p>
		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
            
            <h2> About the repository </h2>
                    <br />
                   
                        <p><a href="http://193.137.200.162/" title="" target="_self">ESTUDO GERAL </a>is  the name of the Digital Repository for the Scientific Production of the University  of Coimbra, a initiative that intends to provide access to the  digital scientific contents from authors of the University of Coimbra (master  degree and PhD thesis, scientific articles, etc.). Its creation falls within  the movement Open Access to the scientific literature, which originated several  Declarations and Recommendations, from which stand the <a href="http://www.soros.org/openaccess/" title="" target="">Budapest Open Access   Initiative</a> and the <a href="http://oa.mpg.de/openaccess-berlin/berlindeclaration.html" title="" target="">Berlin Declaration on Open Access to Scientific Knowledge</a>.The Board of Rectors of the Portuguese Universities (CRUP), that subscribed  the Berlin Declaration, issued, in November 2006, a Declaration manifesting its  support to the principles of the movement, and recommending the creation of  Institutional Archives, and the definition of institutional politics for the  deposit of scientific and academic publications. CRUP also suggested the  creation of a single access portal for the  national scientific literature, that was created in 2008 - <a href="http://www.rcaap.pt/" title="" target="_blank">The   Scientific Open Access Repository of Portugal</a> (RCAAP), sponsored by <a href="http://www.umic.pt" title="Website UMIC" target="_blank">UMIC</a> and operated by <a href="http://www.fccn.pt" title="website FCCN" target="_blank">FCCN</a>. The University of Coimbra  takes part in these initiatives. The subscription of its principles was made by  the Rectory in the beginning of 2007. The University of Coimbra, similarly to  other big national and international universities, is very interested in  increasing its presence in the worldwide network, for it is increasingly an  issuer of knowledge and culture.</p>
                        <p> ESTUDO GERAL purpose is to enhance the visibility, the access and the dissemination  of the results of the scientific research, and more widely, of the academic  activity of the University of Coimbra. It does so by making available not only  the post-graduation works as well as the remaining scientific production of the  academic community, providing, when possible, the full text.</p>
                        <p>This project is developed by <a href="http://www.uc.pt/en/sibuc" title="" target="_self">SIBUC</a> (Integrated System of the Libraries  of the University of Coimbra) – in compliance with the directive to integrate  all the digital resources of the University, stated in the Report &ldquo;<a href="http://www.uc.pt/sibuc/Pdfs/relatorio" title="" target="">Reorganização e Reestruturação das Bibliotecas da Universidade de Coimbra</a>&quot; [Reorganisation and Restructuring of the Libraries of  the University of Coimbra] and it has the cooperation of the Interdisciplinary Research  Institute from the University, and the Foundation for Science and Technology.  In a first stage the archive of the scientific production was performed only by SIBUC.  In a second stage, as soon as the project was open, the authors could perform a  self-archive of their documents. In present it is organised by  scientific communities per area of knowledge.</p>
                        <p>Taking advantage of the same computer platform, and  with the double purpose of favouring the access and promoting the dematerialization of documents, the Academic  Department of the University of Coimbra (Students Division) and SIBUC have  established a partnership to make the scientific production of the candidates to the  aggregation examination and similar examinations available online. Thereby, the  jury (and the candidate) will have remote access, through the use of a  password, to the digital documents. After the examinations, the digital  documents will become available, based on the author&rsquo;s authorisation, in full  or partially, in the ESTUDO GERAL.</p>
                        <p>All  issues concerning copyright have been safeguarded. The authors from the  University of Coimbra are encouraged to grant authorization – not exclusive – of  displaying digital documents in General Study. With the grant of this  non-exclusive authorisation, the authors  maintain their copyrights.</p>
                      </div>
                    </div>
                    <p>&nbsp;</p>

    
		<%	}%>
		







</dspace:layout>