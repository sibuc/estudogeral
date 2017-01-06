<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Footer for home page
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>

<%
    String sidebar = (String) request.getAttribute("dspace.layout.sidebar");
%>

            <%-- Right-hand side bar if appropriate --%>
<%
    if (sidebar != null)
    {
%>
	</div>
	<div class="col-md-3">
                    <%= sidebar %>
    </div>
    </div>       
<%
    }
%>
<%@ include file="footer-rcaap-sari.jsp" %>
</div>
</main>
            <%-- Page footer --%>
             <footer class="navbar navbar-bottom">
             <div id="designedby" class="container text-muted">
	     			<div id="footer_feedback" class="pull-right">                                    
	                                     <!--<p class="text-muted"><fmt:message key="jsp.layout.footer-default.text"/>&nbsp;-
	                                     <a target="_blank" href="<%= request.getContextPath() %>/feedback"><fmt:message key="jsp.layout.footer-default.feedback"/></a>
	                                     <a href="<%= request.getContextPath() %>/htmlmap"></a></p>-->
	                                     </div>
	     			</div>
	    <!-- rcaap footer -->
	    <div class="container">
	    <div class="row">
		<div class="col-md-6 promotors">
			<p>Promotores do RCAAP:</p>
			<a href="http://www.fct.pt" target="_blank" title="Funda&ccedil;&atilde;o para a Ci&ecirc;ncia e a Tecnologia"><img src="<%= request.getContextPath() %>/image/logos/logo_fct_fccn.png" alt="Logo da Funda&ccedil;&atilde;o para a Ci&ecirc;ncia e a Tecnologia - Funda&ccedil;&atilde;o para a Computa&ccedil;&atilde;o Cient&iacute;fica Nacional" /></a>
			<a href="http://www.uminho.pt" target="_blank" title="Universidade do Minho"><img src="<%= request.getContextPath() %>/image/logos/logo_uminho.png" alt="Logo da Universidade do Minho"></a>
		</div>
		<div class="col-md-6 sponsors">
			<p>Financiadores do RCAAP:</p>
			<a href="http://www.portugal.gov.pt/" target="_blank" title="Governo Portugu&ecirc;s"><img src="<%= request.getContextPath() %>/image/logos/logo_pt_mec.png" alt="Logo do Governo de Portugal &middot; Minist&eacute;rio da Educa&ccedil;&atilde;o e Ci&ecirc;ncia" /></a>
			<a href="http://www.posc.mctes.pt/" target="_blank" title="POS_Conhecimento"><img src="<%= request.getContextPath() %>/image/logos/logo_pos_conhecimento.png" alt="POS_C"></a>
			<a href="http://www.europa.eu.int/" target="_blank" title="Uni&atilde;o Europeia"><img src="<%= request.getContextPath() %>/image/logos/logo_ue.png" alt="Logo da Uni&atilde;o Europeia"></a>
		</div>
	</div>
	 </div>   
    </footer>
    </body>
</html>
