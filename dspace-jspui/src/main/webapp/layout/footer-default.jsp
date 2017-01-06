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
</div>
</main>
            <%-- Page footer --%>
            <%--div class="container"--%>
             <footer id="rodape" class="navbar navbar-bottom ">
             <div id="designedby" class="container text-muted">
          <!--   <span id="footer_sp">&nbsp;gestão:</span>-->
             	<a href="http://www.uc.pt/sibuc" target="_blank"><img id="sibuc-logo" alt="SIBUC" src="<%= request.getContextPath() %>/image/logosibuc_p_inv.png"></a>
             
            <!-- <span id="footer_sp">&nbsp;compliance:</span>-->
                 <a href="http://www.degois.pt/" target="_blank"><img alt="DeGois" src="<%= request.getContextPath() %>/image/DeGois_sm.png"></a>
                 <a href="http://www.rcaap.pt/" target="_blank"><img alt="RCAAP" src="<%= request.getContextPath() %>/image/rcaap_sm.png"></a>                              	                
                 <a href="https://www.openaire.eu/" target="_blank"><img alt="OpenAIRE" src="<%= request.getContextPath() %>/image/OpenAIRElogo.png"></a>
                    
               <div class="pull-right" id="footer_feedback">                                    
                <p class="text-muted">
                <span class="glyphicon glyphicon-envelope"></span> <a href="/feedback" target="_blank">Feedback</a>&nbsp;|
                <span class="glyphicon glyphicon-stats"></span> <a href="<%= request.getContextPath() %>/stats?level=general&amp;type=access&amp;page=downviews-series">Statistics of Estudo Geral</a>
                <br><a id="copyright" href="http://www.uc.pt/" target="_blank">Universidade de Coimbra Copyright&nbsp;&copy;&nbsp;2016</a>
                <a href="/htmlmap"></a></p>
             </div>  
			 </div>
             
                                       
                                
 
 <script>
//alert($(document).height() + "---" + $(window).height()) ;

//alert($(window).height());
	var teste = document.getElementById("ftrklg");
	if (document.getElementById('ftrklg') !== null) {document.getElementById("rodape").className += " navbar-fixed-bottom";}


    if ($(document).height() > $(window).height()) {}
    else { document.getElementById("rodape").className += " navbar-fixed-bottom";}

   
	
	//var app = teste.indexOf("ui-accordion-header ui-helper-reset ui-state-default ui-corner-all ui-accordion-icons"); 
    //if (teste=="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all ui-accordion-icons") 
	//alert(app);
	//if (app > 0)
	//{
	//	document.getElementById("rodape").className += " navbar-fixed-bottom";
	//}
    
</script>
    <%--/div--%>
    </div>
    </body>
   
</html>