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
					<h2> Política de Acesso Livre da UC </h2>
<br />
    <p>(aprovado a 23 de Setembro de 2010)</p>
    <p><a href="http://www.uc.pt/sibuc/Estudo_Geral/mandatoUC" title="" target="_blank">Descarregar PDF</a></p>
    <p><strong>I - Preâmbulo</strong></p>
    <p>A   produção científica dos docentes, investigadores e alunos da   Universidade de Coimbra (UC) poderá ser mais valorizada se for melhorada   a sua visibilidade e difusão usando, de uma forma coordenada, os mais   modernos meios. À semelhança de outras grandes universidades nacionais e   internacionais, a UC tem o maior interesse em aumentar a sua presença   na rede informática mundial, sendo cada vez mais – e também por essa via   - um emissor de conhecimento e cultura. A nível mundial são inúmeras as   iniciativas e projectos de promoção do Acesso Livre à literatura   científica. Destacam-se a &ldquo;Budapest Open Access Initiative&rdquo; (2002), o   &ldquo;ECHO Charter&rdquo; (2002), a &ldquo;Bethesda Statement on Open Access Publishing&rdquo;   (2003), a &ldquo;Berlin Declaration on Open Access to Knowledge in the   Sciences and Humanities&rdquo; (2003) e as recomendações do Grupo de Trabalho   sobre Open Access da European University Association (EUA) aprovadas por   unanimidade pelo Conselho da EUA em 2008. No que respeita ao acesso aos   dados e aos artigos científicos relacionados com a investigação   financiada pela União Europeia, destacam-se a &ldquo;OECD Ministerial   Declaration on Access to Digital Research Data&rdquo; (2004) e as decisões da   Comissão Europeia IP/07/190 (2007) e IP/08/1262 (2008), bem como a   resolução do Conselho Científico do European Research Council de   estabelecer, em Dezembro de 2007, uma política obrigatória de depósito   em repositórios de acesso livre (temáticos ou institucionais) das   publicações que resultem dos projectos que financie.</p>
    <p>O   Conselho de Reitores das Universidades Portuguesas manifestou o seu   apoio e adesão aos princípios do Acesso Livre à literatura científica   subscrevendo, em 2006, a Declaração de Berlim e recomendando às   Universidades a criação de Repositórios Institucionais e a definição de   políticas institucionais de depósito das suas publicações científicas e   académicas. Pode ler-se nessa recomendação: &ldquo;(…) a missão da   Universidade de disseminar conhecimento apenas será integralmente   realizada se a informação correspondente for rapidamente disponibilizada   ao conjunto da sociedade através do paradigma do Acesso Livre através   da Internet.&rdquo; E ainda: &ldquo;Os benefícios do Acesso Livre à literatura   científica têm sido crescentemente reconhecidos a nível internacional. O   Acesso Livre promove a visibilidade, acessibilidade e difusão dos   resultados da actividade científica de cada investigador, de cada   universidade ou organização de investigação, bem como de cada país,   potenciando o seu uso e subsequente impacto na comunidade científica   internacional. Um número crescente de estudos tem revelado que os   artigos científicos livremente acessíveis na Internet são mais citados, e   portanto têm um maior impacto, que os artigos que não estão assim   disponíveis.&rdquo;</p>
    <p><strong>II - A Política da Universidade de Coimbra</strong></p>
    <p>A   Universidade de Coimbra, que subscreveu esses princípios em 2007,   decidiu criar o Repositório Digital da Produção Científica da UC, ao   qual foi dado o nome de &ldquo;Estudo Geral&rdquo; e que entrou em funcionamento em   Junho de 2008. Na sequência de bases de dados anteriores, pretende   inventariar e dar uma divulgação acrescida à produção científica da   comunidade universitária e, sempre que possível, disponibilizar o seu   texto integral à mesma comunidade, tal como já acontece em numerosas   universidades nacionais e estrangeiras. </p>
    <p>Em   concordância com os princípios atrás enunciados, a UC define por esta   via a sua Política de Acesso Livre, no que respeita à produção   científica de docentes e investigadores da UC – artigos em revistas,   actas e outras publicações sujeitas ao processo de peer review, bem como   livros, dissertações de mestrado, teses de doutoramento, documentos   académicos, relatórios técnicos e outras obras resultantes do desempenho   das actividades científicas dos seus autores na Universidade. A   Política de Acesso Livre ora estabelecida enquadra-se nas declarações de   Budapeste, Bethesda e Berlim de Open Access e na declaração &ldquo;Acesso   Livre (Open Access) à literatura científica&rdquo; do Conselho de Reitores das   Universidades Portuguesas.</p>
    <p>A UC compromete-se a   disponibilizar o texto integral de todas as publicações com acesso livre   registadas no &ldquo;Estudo Geral&rdquo;, assegurando que este está conforme as   normas técnicas internacionais adequadas, como as definições Open   Archives Initiative e DRIVER – Digital Repository Infrastructure Vision   for European Research, bem como com os critérios de Acessibilidade nível   A da World Wide Web Consortium (W3C). A UC estabelece e assegura os   procedimentos necessários à preservação digital dos conteúdos registados   no Estudo Geral e à garantia de acesso permanente ao material aí   depositado. A UC compromete-se também a manter a ligação ao Repositório   Científico de Acesso Aberto de Portugal (RCAAP), presente na Biblioteca   do Conhecimento Online (b-on), bem como a outras infraestruturas cujo   interesse para a UC venha a ser reconhecido.</p>
    <p><strong>II.1. - Orientações gerais</strong></p>
    <p>Tendo por objectivo o amplo acesso e a disseminação da produção científica da UC, bem como a sua preservação a longo prazo,</p>
    <p>1.   A Comunidade Científica da UC deve concretizar a inclusão no Estudo   Geral de toda a produção científica realizada no contexto das suas   actividades na Universidade, com texto integral de cada publicação, em   formato PDF (Portable Document Format), logo após publicação, ou   aceitação para publicação. No caso de publicações (livros e artigos de   revistas) cujos editores não permitam o Acesso Livre, o depósito deve   ser também realizado, mas ficando em acesso restrito.</p>
    <p>2.   No caso das teses de doutoramento, das dissertações e dos relatórios de   projecto/estágio de mestrado que tenham obtido aprovação, o depósito   obrigatório em texto integral é efectuado pelos Serviços de Gestão   Académica da UC.</p>
    <p>3. Exceptuam-se da aplicação do   disposto nos nºs 1 e 2, por um período variável (dependente dos termos   dos contratos celebrados com editoras e não superior a seis anos), os   trabalhos cujos autores expressamente o solicitem de modo justificado.   Este embargo aplica-se ao acesso e não ao depósito. </p>
    <p>4.   Deve recorrer-se a utilização, sempre que possível, do &ldquo;SPARC Author   Adendum&rdquo; nos contratos celebrados com editores, para manter os direitos   de uso não comercial das obras, seja através da sua distribuição para   fins não comerciais, seja através da sua colocação em bases de dados de   acesso público.</p>
    <p>5. O depósito de documentos no &ldquo;Estudo Geral&rdquo; pode ser feito pela intervenção do Autor</p>
    <p>a. directamente, em http://193.137.200.162;</p>
    <p>b. enviando a informação para o endereço repositório@sib.uc.pt ;</p>
    <p>c.   entregando a informação à Biblioteca associada à(s) Unidade(s)   Orgânica(s), Departamento(s) ou Centro(s) de Investigação   correspondente(s).</p>
    <p>6. O Estudo Geral é considerado a   fonte de informação privilegiada sobre a produção científica de todos os   investigadores da UC, utilizada para a caracterizar, com base em   indicadores a manter e aperfeiçoar, aos níveis da Universidade, das   Unidades Orgânicas e das Unidades de I&amp;D.</p>
    <p>7. Com   base na informação existente no Estudo Geral são atribuídos prémios   pecuniários a investigadores da UC, um por cada Unidade Orgânica por   ano, segundo método e critérios estabelecidos pelo Reitor com   auscultação aos Conselhos Científicos das Unidades Orgânicas e consulta   ao Senado.</p>
    <h4>II.2. – Papel desempenhado pelos Serviços da UC</h4>
    <p>1. <strong><em>Serviço Integrado das Bibliotecas da UC (SIBUC)</em></strong>. </p>
    <p>Compete ao SIBUC gerir o &ldquo;Estudo Geral&rdquo; - Repositório digital da produção científica da UC, nomeadamente:</p>
    <p>a.   Administrar, em colaboração com os serviços de natureza central da   Universidade no domínio das tecnologias da informação e comunicação, o   sistema informático onde está instalado o &ldquo;Estudo Geral&rdquo; e apoiar a   implementação de novas versões e funcionalidades.</p>
    <p>b.   Garantir a interoperabilidade do &ldquo;Estudo Geral&rdquo; com o Repositório   Científico de Acesso Aberto de Portugal (RCAAP) e outros repositórios   cujo interesse venha a ser reconhecido, de acordo com as normas técnicas   atrás referidas.</p>
    <p>c. Assegurar a correcta divulgação no   &ldquo;Estudo Geral&rdquo; das teses de doutoramento, das dissertações e relatórios   de projecto/estágio de mestrado, nas condições estabelecidas em II 1.1 e   1.2, em colaboração com os Serviços de Gestão Académica, com a   Biblioteca Geral (BGUC) e restantes Bibliotecas da UC.</p>
    <p>d.   Fazer o levantamento da restante produção científica da UC, assegurar a   sua divulgação no &ldquo;Estudo Geral&rdquo; e promover o auto-arquivo por parte   dos autores.</p>
    <p>e. Colaborar com os Centros de   Investigação, nomeadamente através do Instituto de Investigação   Interdisciplinar, e com a Administração da UC, para a disponibilização   de dados no &ldquo;Estudo Geral&rdquo;.</p>
    <p>f. Colaborar com a Imprensa   da Universidade de Coimbra e com as Direcções das Publicações Periódicas   editadas na UC com vista à inclusão no &ldquo;Estudo Geral&rdquo; dos conteúdos   apropriados.</p>
    <p>g. Promover o registo e arquivo no &ldquo;Estudo Geral&rdquo; dos documentos que lhe forem entregues para esse fim.</p>
    <p>h. Promover acções de divulgação e formação para utilizadores. </p>
    <p>i.   Assegurar, em colaboração com as Bibliotecas da UC, a formação técnica   dos profissionais de biblioteca necessária ao bom funcionamento do   sistema.</p>
    <p>j. Caracterizar a produção científica global da   UC, de cada Unidade Orgânica e de cada Unidade de I&amp;D, com base num   painel de indicadores que deve propor.</p>
    <p>k. Tratar   tecnicamente a informação existente no Estudo Geral com o objectivo de   suportar o(s) processo(s) de atribuição de prémio(s) à produção   científica de investigadores da UC que estejam definidos em cada ano.</p>
    <p>2. <strong><em>Serviço de Gestão Académica da UC</em></strong>. </p>
    <p>Compete aos Serviços de Gestão Académica:</p>
    <p>a.   Registar e arquivar no Estudo Geral todas as teses de doutoramento e as   dissertações e relatórios de projecto/estágio de mestrado que tenham   obtido aprovação, associando ao registo o respectivo texto integral.</p>
    <p>c.   Enviar à Biblioteca Geral da Universidade de Coimbra (BGUC) um exemplar   em papel e um exemplar em formato digital de todas as teses e   dissertações aprovadas.</p>
    <p><em>3. </em><strong><em>Bibliotecas da UC</em></strong></p>
    <p>Compete às Bibliotecas da UC:</p>
    <p>a.   Zelar pela qualidade dos metadados relativos às publicações da   comunidade académica de cada Unidade Orgânica, Departamento ou Unidade   de I&amp;D.</p>
    <p>b. Apoiar os utilizadores de cada Unidade   Orgânica, Departamento ou Unidade de I&amp;D no registo e arquivo de   documentos no Estudo Geral</p>
    <p>c. Registar e arquivar no Estudo Geral os documentos que lhe forem entregues para esse fim</p>
    <p>4. <strong><em>Divisão de Planeamento, Gestão e Desenvolvimento (DPGD)</em></strong> do Centro de Serviços Especializados da Administração da UC. </p>
    <p>Compete à DPGD:</p>
    <p>a.   Incluir os resultados da caracterização da produção científica apurados   pelo SIBUC no relatório anual de actividade da UC e nas publicações de   natureza estatística aplicáveis.</p>
    <p><strong>III - Conclusão</strong></p>
    <p>A   UC procura assegurar os mecanismos necessários para a correcta   atribuição e uso responsável dos trabalhos publicados no &ldquo;Estudo Geral&rdquo;,   em particular segundo os termos da Licença Creative Commons Atribuição   2.5 Portugal. </p>
    <p>A UC procura apoiar iniciativas institucionais de relevo que visem maximizar o benefício público do conhecimento científico.</p>
    <p>Esta Política entra em vigor 30 dias após a sua aprovação.</p>

		<%	}%>
		
        <%if (current_lang.equals("en")) 
			{%>
				<h2> Política de Open Access da UC </h2>
<br />
    <p>(aprovado a 23 de Setembro de 2010)</p>
    <p><a href="http://www.uc.pt/sibuc/Estudo_Geral/mandatoUC" title="" target="_blank">Descarregar PDF</a></p>
    <p><strong>I - Preâmbulo</strong></p>
    <p>A   produção científica dos docentes, investigadores e alunos da   Universidade de Coimbra (UC) poderá ser mais valorizada se for melhorada   a sua visibilidade e difusão usando, de uma forma coordenada, os mais   modernos meios. À semelhança de outras grandes universidades nacionais e   internacionais, a UC tem o maior interesse em aumentar a sua presença   na rede informática mundial, sendo cada vez mais – e também por essa via   - um emissor de conhecimento e cultura. A nível mundial são inúmeras as   iniciativas e projectos de promoção do Acesso Livre à literatura   científica. Destacam-se a &ldquo;Budapest Open Access Initiative&rdquo; (2002), o   &ldquo;ECHO Charter&rdquo; (2002), a &ldquo;Bethesda Statement on Open Access Publishing&rdquo;   (2003), a &ldquo;Berlin Declaration on Open Access to Knowledge in the   Sciences and Humanities&rdquo; (2003) e as recomendações do Grupo de Trabalho   sobre Open Access da European University Association (EUA) aprovadas por   unanimidade pelo Conselho da EUA em 2008. No que respeita ao acesso aos   dados e aos artigos científicos relacionados com a investigação   financiada pela União Europeia, destacam-se a &ldquo;OECD Ministerial   Declaration on Access to Digital Research Data&rdquo; (2004) e as decisões da   Comissão Europeia IP/07/190 (2007) e IP/08/1262 (2008), bem como a   resolução do Conselho Científico do European Research Council de   estabelecer, em Dezembro de 2007, uma política obrigatória de depósito   em repositórios de acesso livre (temáticos ou institucionais) das   publicações que resultem dos projectos que financie.</p>
    <p>O   Conselho de Reitores das Universidades Portuguesas manifestou o seu   apoio e adesão aos princípios do Acesso Livre à literatura científica   subscrevendo, em 2006, a Declaração de Berlim e recomendando às   Universidades a criação de Repositórios Institucionais e a definição de   políticas institucionais de depósito das suas publicações científicas e   académicas. Pode ler-se nessa recomendação: &ldquo;(…) a missão da   Universidade de disseminar conhecimento apenas será integralmente   realizada se a informação correspondente for rapidamente disponibilizada   ao conjunto da sociedade através do paradigma do Acesso Livre através   da Internet.&rdquo; E ainda: &ldquo;Os benefícios do Acesso Livre à literatura   científica têm sido crescentemente reconhecidos a nível internacional. O   Acesso Livre promove a visibilidade, acessibilidade e difusão dos   resultados da actividade científica de cada investigador, de cada   universidade ou organização de investigação, bem como de cada país,   potenciando o seu uso e subsequente impacto na comunidade científica   internacional. Um número crescente de estudos tem revelado que os   artigos científicos livremente acessíveis na Internet são mais citados, e   portanto têm um maior impacto, que os artigos que não estão assim   disponíveis.&rdquo;</p>
    <p><strong>II - A Política da Universidade de Coimbra</strong></p>
    <p>A   Universidade de Coimbra, que subscreveu esses princípios em 2007,   decidiu criar o Repositório Digital da Produção Científica da UC, ao   qual foi dado o nome de &ldquo;Estudo Geral&rdquo; e que entrou em funcionamento em   Junho de 2008. Na sequência de bases de dados anteriores, pretende   inventariar e dar uma divulgação acrescida à produção científica da   comunidade universitária e, sempre que possível, disponibilizar o seu   texto integral à mesma comunidade, tal como já acontece em numerosas   universidades nacionais e estrangeiras. </p>
    <p>Em   concordância com os princípios atrás enunciados, a UC define por esta   via a sua Política de Acesso Livre, no que respeita à produção   científica de docentes e investigadores da UC – artigos em revistas,   actas e outras publicações sujeitas ao processo de peer review, bem como   livros, dissertações de mestrado, teses de doutoramento, documentos   académicos, relatórios técnicos e outras obras resultantes do desempenho   das actividades científicas dos seus autores na Universidade. A   Política de Acesso Livre ora estabelecida enquadra-se nas declarações de   Budapeste, Bethesda e Berlim de Open Access e na declaração &ldquo;Acesso   Livre (Open Access) à literatura científica&rdquo; do Conselho de Reitores das   Universidades Portuguesas.</p>
    <p>A UC compromete-se a   disponibilizar o texto integral de todas as publicações com acesso livre   registadas no &ldquo;Estudo Geral&rdquo;, assegurando que este está conforme as   normas técnicas internacionais adequadas, como as definições Open   Archives Initiative e DRIVER – Digital Repository Infrastructure Vision   for European Research, bem como com os critérios de Acessibilidade nível   A da World Wide Web Consortium (W3C). A UC estabelece e assegura os   procedimentos necessários à preservação digital dos conteúdos registados   no Estudo Geral e à garantia de acesso permanente ao material aí   depositado. A UC compromete-se também a manter a ligação ao Repositório   Científico de Acesso Aberto de Portugal (RCAAP), presente na Biblioteca   do Conhecimento Online (b-on), bem como a outras infraestruturas cujo   interesse para a UC venha a ser reconhecido.</p>
    <p><strong>II.1. - Orientações gerais</strong></p>
    <p>Tendo por objectivo o amplo acesso e a disseminação da produção científica da UC, bem como a sua preservação a longo prazo,</p>
    <p>1.   A Comunidade Científica da UC deve concretizar a inclusão no Estudo   Geral de toda a produção científica realizada no contexto das suas   actividades na Universidade, com texto integral de cada publicação, em   formato PDF (Portable Document Format), logo após publicação, ou   aceitação para publicação. No caso de publicações (livros e artigos de   revistas) cujos editores não permitam o Acesso Livre, o depósito deve   ser também realizado, mas ficando em acesso restrito.</p>
    <p>2.   No caso das teses de doutoramento, das dissertações e dos relatórios de   projecto/estágio de mestrado que tenham obtido aprovação, o depósito   obrigatório em texto integral é efectuado pelos Serviços de Gestão   Académica da UC.</p>
    <p>3. Exceptuam-se da aplicação do   disposto nos nºs 1 e 2, por um período variável (dependente dos termos   dos contratos celebrados com editoras e não superior a seis anos), os   trabalhos cujos autores expressamente o solicitem de modo justificado.   Este embargo aplica-se ao acesso e não ao depósito. </p>
    <p>4.   Deve recorrer-se a utilização, sempre que possível, do &ldquo;SPARC Author   Adendum&rdquo; nos contratos celebrados com editores, para manter os direitos   de uso não comercial das obras, seja através da sua distribuição para   fins não comerciais, seja através da sua colocação em bases de dados de   acesso público.</p>
    <p>5. O depósito de documentos no &ldquo;Estudo Geral&rdquo; pode ser feito pela intervenção do Autor</p>
    <p>a. directamente, em http://193.137.200.162;</p>
    <p>b. enviando a informação para o endereço repositório@sib.uc.pt ;</p>
    <p>c.   entregando a informação à Biblioteca associada à(s) Unidade(s)   Orgânica(s), Departamento(s) ou Centro(s) de Investigação   correspondente(s).</p>
    <p>6. O Estudo Geral é considerado a   fonte de informação privilegiada sobre a produção científica de todos os   investigadores da UC, utilizada para a caracterizar, com base em   indicadores a manter e aperfeiçoar, aos níveis da Universidade, das   Unidades Orgânicas e das Unidades de I&amp;D.</p>
    <p>7. Com   base na informação existente no Estudo Geral são atribuídos prémios   pecuniários a investigadores da UC, um por cada Unidade Orgânica por   ano, segundo método e critérios estabelecidos pelo Reitor com   auscultação aos Conselhos Científicos das Unidades Orgânicas e consulta   ao Senado.</p>
    <h4>II.2. – Papel desempenhado pelos Serviços da UC</h4>
    <p>1. <strong><em>Serviço Integrado das Bibliotecas da UC (SIBUC)</em></strong>. </p>
    <p>Compete ao SIBUC gerir o &ldquo;Estudo Geral&rdquo; - Repositório digital da produção científica da UC, nomeadamente:</p>
    <p>a.   Administrar, em colaboração com os serviços de natureza central da   Universidade no domínio das tecnologias da informação e comunicação, o   sistema informático onde está instalado o &ldquo;Estudo Geral&rdquo; e apoiar a   implementação de novas versões e funcionalidades.</p>
    <p>b.   Garantir a interoperabilidade do &ldquo;Estudo Geral&rdquo; com o Repositório   Científico de Acesso Aberto de Portugal (RCAAP) e outros repositórios   cujo interesse venha a ser reconhecido, de acordo com as normas técnicas   atrás referidas.</p>
    <p>c. Assegurar a correcta divulgação no   &ldquo;Estudo Geral&rdquo; das teses de doutoramento, das dissertações e relatórios   de projecto/estágio de mestrado, nas condições estabelecidas em II 1.1 e   1.2, em colaboração com os Serviços de Gestão Académica, com a   Biblioteca Geral (BGUC) e restantes Bibliotecas da UC.</p>
    <p>d.   Fazer o levantamento da restante produção científica da UC, assegurar a   sua divulgação no &ldquo;Estudo Geral&rdquo; e promover o auto-arquivo por parte   dos autores.</p>
    <p>e. Colaborar com os Centros de   Investigação, nomeadamente através do Instituto de Investigação   Interdisciplinar, e com a Administração da UC, para a disponibilização   de dados no &ldquo;Estudo Geral&rdquo;.</p>
    <p>f. Colaborar com a Imprensa   da Universidade de Coimbra e com as Direcções das Publicações Periódicas   editadas na UC com vista à inclusão no &ldquo;Estudo Geral&rdquo; dos conteúdos   apropriados.</p>
    <p>g. Promover o registo e arquivo no &ldquo;Estudo Geral&rdquo; dos documentos que lhe forem entregues para esse fim.</p>
    <p>h. Promover acções de divulgação e formação para utilizadores. </p>
    <p>i.   Assegurar, em colaboração com as Bibliotecas da UC, a formação técnica   dos profissionais de biblioteca necessária ao bom funcionamento do   sistema.</p>
    <p>j. Caracterizar a produção científica global da   UC, de cada Unidade Orgânica e de cada Unidade de I&amp;D, com base num   painel de indicadores que deve propor.</p>
    <p>k. Tratar   tecnicamente a informação existente no Estudo Geral com o objectivo de   suportar o(s) processo(s) de atribuição de prémio(s) à produção   científica de investigadores da UC que estejam definidos em cada ano.</p>
    <p>2. <strong><em>Serviço de Gestão Académica da UC</em></strong>. </p>
    <p>Compete aos Serviços de Gestão Académica:</p>
    <p>a.   Registar e arquivar no Estudo Geral todas as teses de doutoramento e as   dissertações e relatórios de projecto/estágio de mestrado que tenham   obtido aprovação, associando ao registo o respectivo texto integral.</p>
    <p>c.   Enviar à Biblioteca Geral da Universidade de Coimbra (BGUC) um exemplar   em papel e um exemplar em formato digital de todas as teses e   dissertações aprovadas.</p>
    <p><em>3. </em><strong><em>Bibliotecas da UC</em></strong></p>
    <p>Compete às Bibliotecas da UC:</p>
    <p>a.   Zelar pela qualidade dos metadados relativos às publicações da   comunidade académica de cada Unidade Orgânica, Departamento ou Unidade   de I&amp;D.</p>
    <p>b. Apoiar os utilizadores de cada Unidade   Orgânica, Departamento ou Unidade de I&amp;D no registo e arquivo de   documentos no Estudo Geral</p>
    <p>c. Registar e arquivar no Estudo Geral os documentos que lhe forem entregues para esse fim</p>
    <p>4. <strong><em>Divisão de Planeamento, Gestão e Desenvolvimento (DPGD)</em></strong> do Centro de Serviços Especializados da Administração da UC. </p>
    <p>Compete à DPGD:</p>
    <p>a.   Incluir os resultados da caracterização da produção científica apurados   pelo SIBUC no relatório anual de actividade da UC e nas publicações de   natureza estatística aplicáveis.</p>
    <p><strong>III - Conclusão</strong></p>
    <p>A   UC procura assegurar os mecanismos necessários para a correcta   atribuição e uso responsável dos trabalhos publicados no &ldquo;Estudo Geral&rdquo;,   em particular segundo os termos da Licença Creative Commons Atribuição   2.5 Portugal. </p>
    <p>A UC procura apoiar iniciativas institucionais de relevo que visem maximizar o benefício público do conhecimento científico.</p>
    <p>Esta Política entra em vigor 30 dias após a sua aprovação.</p>

		<%	}%>
		







</dspace:layout>
