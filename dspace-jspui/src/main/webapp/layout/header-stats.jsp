<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - HTML header for main home page
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="org.dspace.app.webui.util.JSPManager" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.app.util.Util" %>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ page import="org.dspace.core.NewsManager" %>

<%
    String title = (String) request.getAttribute("dspace.layout.title");
    String navbar = (String) request.getAttribute("dspace.layout.navbar");
    boolean locbar = ((Boolean) request.getAttribute("dspace.layout.locbar")).booleanValue();

    String siteName = ConfigurationManager.getProperty("dspace.name");
    String feedRef = (String)request.getAttribute("dspace.layout.feedref");
    boolean osLink = ConfigurationManager.getBooleanProperty("websvc.opensearch.autolink");
    String osCtx = ConfigurationManager.getProperty("websvc.opensearch.svccontext");
    String osName = ConfigurationManager.getProperty("websvc.opensearch.shortname");
    List parts = (List)request.getAttribute("dspace.layout.linkparts");
    String extraHeadData = (String)request.getAttribute("dspace.layout.head");
    String extraHeadDataLast = (String)request.getAttribute("dspace.layout.head.last");
    String dsVersion = Util.getSourceVersion();
    String generator = dsVersion == null ? "DSpace" : "DSpace "+dsVersion;
    String analyticsKey = ConfigurationManager.getProperty("jspui.google.analytics.key");
	
	String topNews = NewsManager.readNewsFile(LocaleSupport.getLocalizedMessage(pageContext, "news-top.html"));
%>

<!DOCTYPE html>
<html>
    <head>
        <title><%= siteName %>: <%= title %></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="Generator" content="<%= generator %>" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- RCCAP - Open Graph Protocol implementation-->
        <%
            String baseUrl = ConfigurationManager.getProperty("dspace.baseUrl");
            String currentUrl = org.dspace.app.webui.util.UIUtil.getOriginalURL(request);
        %>
        <head prefix="dspace: <%= baseUrl %>">
        <meta property="og:title" content="<%= title %>" />
        <meta property="og:type" content="dspace:page" />
        <meta property="og:url" content="<%= currentUrl %>" />
        <meta property="og:image" content="<%= baseUrl %><%= request.getContextPath() %>/image/logo-uc.gif" />
        <!-- /RCCAP - Open Graph Protocol implementation-->

        <link rel="shortcut icon" href="<%= request.getContextPath() %>/image/favicon.ico" type="image/x-icon"/>
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/jquery-ui-1.10.3.custom/redmond/jquery-ui-1.10.3.custom.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/bootstrap.min.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/bootstrap-theme.min.css" type="text/css" />
	    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/bootstrap/dspace-theme.css" type="text/css" />
        <link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400,600' rel='stylesheet' type='text/css'>
<%
    if (!"NONE".equals(feedRef))
    {
        for (int i = 0; i < parts.size(); i+= 3)
        {
%>
        <link rel="alternate" type="application/<%= (String)parts.get(i) %>" title="<%= (String)parts.get(i+1) %>" href="<%= request.getContextPath() %>/feed/<%= (String)parts.get(i+2) %>/<%= feedRef %>"/>
<%
        }
    }
    
    if (osLink)
    {
%>
        <link rel="search" type="application/opensearchdescription+xml" href="<%= request.getContextPath() %>/<%= osCtx %>description.xml" title="<%= osName %>"/>
<%
    }

    if (extraHeadData != null)
        { %>
<%= extraHeadData %>
<%
        }
%>
        
	<script type='text/javascript' src="<%= request.getContextPath() %>/static/js/jquery/jquery-1.10.2.min.js"></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/jquery/jquery-ui-1.10.3.custom.min.js'></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/bootstrap/bootstrap.min.js'></script>
	<script type='text/javascript' src='<%= request.getContextPath() %>/static/js/holder.js'></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/utils.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/static/js/choice-support.js"> </script>

    <%--Gooogle Analytics recording.--%>
    <%
    if (analyticsKey != null && analyticsKey.length() > 0)
    {
    %>
        <script type="text/javascript">
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '<%= analyticsKey %>']);
            _gaq.push(['_trackPageview']);

            (function() {
                var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
            })();
        </script>
    <%
    }
    if (extraHeadDataLast != null)
    { %>
		<%= extraHeadDataLast %>
		<%
		    }
    %>
    

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="<%= request.getContextPath() %>/static/js/html5shiv.js"></script>
  <script src="<%= request.getContextPath() %>/static/js/respond.min.js"></script>
<![endif]-->


<!-- Begin Cookie Consent plugin by Silktide - http://silktide.com/cookieconsent -->
<link rel="stylesheet" type="text/css" href="https://s3-eu-west-1.amazonaws.com/assets.cookieconsent.silktide.com/current/style.min.css"/>
<script type="text/javascript" src="https://s3-eu-west-1.amazonaws.com/assets.cookieconsent.silktide.com/current/plugin.min.js"></script>
<script type="text/javascript">
// <![CDATA[
cc.initialise({
	cookies: {
		analytics: {}
	},
	settings: {
		consenttype: "implicit",
		bannerPosition: "bottom",
		tagPosition: "vertical-right",
		hideprivacysettingstab: true,
		onlyshowbanneronce: true,
		useSSL: true
	}
});
// ]]>
</script>
<!-- End Cookie Consent plugin -->

<script type="text/javascript">
$(window).scroll(function(){
  var sticky = $('.eg_header'),
      scroll = $(window).scrollTop();

  if (scroll >= 100) sticky.addClass('navbar-fixed-top');
  else sticky.removeClass('navbar-fixed-top');
});
</script>
    </head>

    <%-- HACK: leftmargin, topmargin: for non-CSS compliant Microsoft IE browser --%>
    <%-- HACK: marginwidth, marginheight: for non-CSS compliant Netscape browser --%>
    <body class="undernavigation">
    <div id="page-wrapper">
<a class="sr-only" href="#content">Skip navigation</a>

<header id="uc_header" class="navbar navbar-inverse">    <%-- navbar-fixed-top--%>
 <div class="container">
  <div class="uc">
        <!-- <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
           <span class="icon-bar"></span>
           <span class="icon-bar"></span>
           <span class="icon-bar"></span>
         </button>-->
         <a class="navbar-brand" href="<%= request.getContextPath() %>/"><img height="50" src="<%= request.getContextPath() %>/image/logo3.png" alt="UC logo" /></a>
         <a class="navbar-brand navbar-brand2" href="http://worldheritage.uc.pt"><img style="margin-right:30px" height="50" src="<%= request.getContextPath() %>/image/unesco.png" alt="unesco logo" /></a>
      
 
	</div> 
 </div>
 
</header>
<header id="eg_header" class="navbar navbar-inverse eg_header">    <%-- navbar-fixed-top--%>
    <%
    if (!navbar.equals("off"))
    {
%>
		<div class="container">
            <dspace:include page="/layout/navbar-stats.jsp" />
        </div>
            <%
    }
    else
    {
    	%>
        <div class="container">
            <dspace:include page="/layout/navbar-minimal.jsp" />
        </div>
<%    	
    }
%>

 
</header>
<main id="content" role="main">
<!--<div class="container banner">
	<div class="rowx">		
			<div class="rowx">		
				<a href="/"><img style="width:100%" alt="sibuc banner" src="<%= request.getContextPath() %>/image/bannereg_cr_com.png" class="Ximg-responsive"></a>		
        	</div>
        		
		</div>
</div>	-->

                <%-- Location bar --%>
<%
    if (locbar)
    {
%>
<div class="container bread">
                <dspace:include page="/layout/location-bar.jsp" />
</div>                
<%
    }
%>


        <%-- Page contents --%>
<div class="container cont">
<% if (request.getAttribute("dspace.layout.sidebar") != null) { %>
	<div class="row">
		<div class="col-md-9">
<% } %>		
