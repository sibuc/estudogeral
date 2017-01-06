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
					<h2> Direitos de Autor </h2>
                    <p>As questões relacionadas com a propriedade  intelectual dos trabalhos depositados nos repositórios merecem uma atenção  especial.</p><br />
                    <p>  Os  direitos de autor - copyright - pertencem ao autor, a menos que este os tenha transmitido/cedido  a terceiros de modo formal e explícito, como geralmente acontece na publicação  em revistas científicas internacionais. As condições em que o autor cede os  seus direitos a terceiros (geralmente aos editores) são variáveis, embora, em  muitos casos, continuem a permitir o autodepósito (ou autoarquivo) de uma cópia  do trabalho nos repositórios.</p><br />
                     <p> Publicar  um trabalho nas atas de uma conferência, ou numa revista,  por exemplo, sem que surja essa explícita transferência de  direitos não afeta a integridade dos direitos do autor, nomeadamente o direito  de autodepositar o seu trabalho em repositórios, ou de o difundir por outros  meios.</p>
                     <br />
					<p>O<a href="http://www.sherpa.ac.uk/romeo/" target="_blank"> SHERPA/RoMEO</a> é um serviço de referência que dá a conhecer as políticas  das editoras e das revistas no que respeita aos direitos de autor (copyright) e  ao autodepósito de documentos em repositórios institucionais.</p><br />
                    <p>  A informação relativa às políticas das editoras  e das revistas disponibilizada através deste serviço é recolhida e mantida pelo<a href="http://www.sherpa.ac.uk/" target="_blank"> projeto SHERPA</a>, com o apoio do<a href="http://www.jisc.ac.uk/" target="_blank"> JISC</a> e do<a href="http://www.wellcome.ac.uk/" target="_blank"> Wellcome Trust</a>.</p><br />
                    <p>  As questões relacionadas com as políticas das  editoras e das revistas científicas portuguesas são geridas no âmbito do<a href="http://projeto.rcaap.pt/index.php/lang-pt/sobre-o-rcaap/servicos/projecto-blimunda" target="_blank"> projeto Blimunda</a>. O projeto Blimunda tem como principais objetivos fazer o  levantamento destas políticas em relação ao autodepósito em repositórios  institucionais e registá-las na base de dados internacional<a href="http://www.sherpa.ac.uk/romeo/" target="_blank"> SHERPA/RoMEO</a>.</p><br />
                    <p>  O SHERPA/RoMEO utiliza um esquema de cores para  classificar as editoras de acordo com as políticas de autodepósito de conteúdo  adotadas. Cada cor representa uma forma de política e informa se os autores da  publicação em causa a podem &nbsp;incluir num repositório, e em que versão o  podem fazer, apresentando um breve sumário sobre essas  condições e uma ligação para a página da editora.</p><br />
                    <p>As cores adotadas são as seguintes:</p>
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="133" valign="top"><p>Cores RoMEO </p></td>
                        <td width="686" valign="top"><p>Política de    arquivo </p></td>
                      </tr>
                      <tr>
                        <td valign="top"><p><a href="http://www.sherpa.ac.uk/romeo/browse.php?colour=green&amp;la=pt&amp;fIDnum=%7C&amp;mode=simple" target="_blank">Verde</a></p></td>
                        <td valign="top"><p>Pode arquivar    a versão preprint <em>e</em> postprint ou Versão/PDF do editor </p></td>
                      </tr>
                      <tr>
                        <td valign="top"><p><a href="http://www.sherpa.ac.uk/romeo/browse.php?colour=blue&amp;la=pt&amp;fIDnum=%7C&amp;mode=simple" target="_blank">Azul</a></p></td>
                        <td valign="top"><p>Pode arquivar    a versão postprint (i.e. o rascunho final após o peer-review) ou Versão/PDF    do editor </p></td>
                      </tr>
                      <tr>
                        <td valign="top"><p><a href="http://www.sherpa.ac.uk/romeo/browse.php?colour=yellow&amp;la=pt&amp;fIDnum=%7C&amp;mode=simple" target="_blank">Amarelo</a></p></td>
                        <td valign="top"><p>Pode arquivar    a versão preprint (i.e. antes do peer-review) </p></td>
                      </tr>
                      <tr>
                        <td valign="top"><p><a href="http://www.sherpa.ac.uk/romeo/browse.php?colour=white&amp;la=pt&amp;fIDnum=%7C&amp;mode=simple" target="_blank">Branco</a></p></td>
                        <td valign="top"><p>O arquivo não    é suportado formalmente </p></td>
                      </tr>
                    </table>
                    <br />
   


<p>O autor pode solicitar autorização à editora para autodepositar o seu documento, se não conhecer a sua política, se não se recordar dos termos da declaração que assinou, ou se o documento que quer depositar já tenha sido publicado há muito tempo.
Pode ainda consultar estatísticas actualizadas acerca das políticas das editoras, sugerir eventuais correções ou recomendar a inclusão de outras revistas/editoras.</p><br />

<p>Os trabalhos depositados no Estudo Geral cedem o direito à Universidade de Coimbra de arquivar e disseminar a sua produção científica no repositório institucional mas os direitos de autor ficam na dependência exclusiva dos seus autores. Deste modo, os investigadores e docentes podem ceder esses direitos a quem entenderem, quando decidem publicar o resultado do seu trabalho. Para tal, existem licenças sobre a utilização de material intelectual, como as Creative Common License, que seguem as normas dos direitos de autor, permitindo o acesso livre aos documentos depositados nos repositórios institucionais. As Creative Common License podem funcionar como uma solução eficaz para os autores manifestaram o seu consentimento ao Acesso Aberto, porque lhes permite definir as condições sob as quais a sua obra é partilhada com terceiros. Com estas licenças, que garantem a protecção e liberdade com alguns direitos reservados, o número de obras disponibilizadas livremente aumenta e estimula de forma eficaz e flexível, a criação de novas obras com base nas originais.</p>

		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
				<h2> Copyright </h2>
<br />
   

		<%	}%>
		







</dspace:layout>
