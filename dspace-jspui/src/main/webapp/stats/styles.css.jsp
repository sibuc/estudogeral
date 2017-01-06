<%--
  - styles.css.jsp
  -
  - Version: $Revision: 1467 $
  -
  - Date: $Date: 2006-03-27 06:24:03 +0100 (Mon, 27 Mar 2006) $
  -
  - Copyright (c) 2002, Hewlett-Packard Company and Massachusetts
  - Institute of Technology.  All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are
  - met:
  -
  - - Redistributions of source code must retain the above copyright
  - notice, this list of conditions and the following disclaimer.
  -
  - - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  -
  - - Neither the name of the Hewlett-Packard Company nor the name of the
  - Massachusetts Institute of Technology nor the names of their
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  - A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  - HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  - BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  - OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  - ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  - TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  - USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  - DAMAGE.
  --%>

<%--
  - Stats Web UI stylesheet
  -
  - This is a JSP so it can be tailored for different browser types
  --%>
<%@ page import="org.dspace.app.webui.util.JSPManager" %>

<%
    // Make sure the browser knows we're a stylesheet
    response.setContentType("text/css");

    String imageUrl   = request.getContextPath() + "/image/";

    // Netscape 4.x?
    boolean usingNetscape4 = false;
    String userAgent = request.getHeader( "User-Agent" );
    if( userAgent != null && userAgent.startsWith( "Mozilla/4" ) )
    {
        usingNetscape4 = true;
    }
%>

.Normal
{
	font-family:  Arial, Verdana, Helvetica, sans-serif;
	font-size: 12px;
	color: #666666;
	line-height:1.4
}
.StatTitle
{
	font-family:  Arial,Verdana, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 14px;
	color: #666666;
	margin-bottom: 5px;	
}
.BlockTitle
{
	font-family:  Arial,Verdana, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 13px;
	color: #666666;
	margin-bottom: 3px;	
}
.MenuTitle {  
	font-family:  Arial,Verdana, Helvetica, sans-serif;
	font-size: 13px;
	font-style: normal;
	font-weight: bold;
	color: #666666;
	text-decoration: none;
	background: #ffffff;
	white-space: nowrap 
}
.Menu1 {
	font-weight: 400; 
	FONT-SIZE: 12px; 
	BACKGROUND: #ffffff; 
	VERTICAL-ALIGN: middle; 
	COLOR: #666666; 
	FONT-STYLE: normal; 
	FONT-FAMILY: Arial,Verdana, Helvetica, sans-serif; 
	WHITE-SPACE: nowrap; 
	TEXT-DECORATION: none; 
	padding-left: 20px; 
	padding-top: 0px; 
	padding-bottom: 1px
}
A.link:link {
	font-family:  Arial, Verdana, Helvetica, sans-serif;
	font-size: 12px;
	COLOR: #972A2B;
        text-decoration: none
}
A.link:visited {
	font-family:  Arial, Verdana, Helvetica, sans-serif;
	font-size: 12px;
	COLOR: #972A2B;
    text-decoration: none
}
A.link:hover {
	font-family:  Arial, Verdana, Helvetica, sans-serif;
	font-size: 12px;
	COLOR: #972A2B;
	text-decoration: underline;
}
A.link:active {
	font-family:  Arial, Verdana, Helvetica, sans-serif;
	font-size: 12px;
	COLOR: #972A2B;
	text-decoration: none
}
.frmInput
{
	font-size: 11px;
	color: #333;
	/*border: #666666 0px solid;*/
	font-family: Arial,Verdana, sans-serif;
	background-color: #fff;
}
.frmSelect
{
	font-family: Arial,Verdana, sans-serif;
	/*border: #666666 0px solid;*/
	/*font-weight:bold; */
	color:#333; 
	font-size:11px;	
}
.statTable { font-family: "Arial", "Verdana", "Helvetica", sans-serif;
             font-size: 12px;
             font-style: normal;
             font-weight: normal;
             color: #666666;
             vertical-align: middle;
             text-decoration: none;
             background: #ffffff
}
<%
    String statpadding = "padding: 0px";

    if( usingNetscape4 )
    {
        statpadding = "padding-left: 3px;  padding-right: 3px; padding-top: 0px; padding-bottom: 0px";
    }
%>
.statTableHeader{ font-family: "Arial", "Verdana", "Helvetica", sans-serif;
               font-size: 12px;
               font-style: normal;
               font-weight: normal;
               color: White;
               vertical-align: middle;
               text-decoration: none;
               background: Gray;
			   padding-left: 3px;
			   padding-right: 3px;
			   padding-top: 2px;
			   padding-bottom: 2px
}
.statOddRowOddCol{ font-family: "Arial", "Verdana", "Helvetica", sans-serif;
               font-size: 12px;
               font-style: normal;
               font-weight: normal;
               color: #666666;
               vertical-align: middle;
               text-decoration: none;
               background: #ffffff;
               <%= statpadding %> }

.statEvenRowOddCol{ font-family: "Arial", "Verdana", "Helvetica", sans-serif;
                font-size: 12px;
                font-style: normal;
                font-weight: normal;
                color: #666666;
                vertical-align: middle;
                text-decoration: none;
                background: #eeeeee;
                <%= statpadding %>  }

.statOddRowEvenCol{ font-family: "Arial", "Verdana", "Helvetica", sans-serif;
                font-size: 12px;
                font-style: normal;
                font-weight: normal;
                color: #666666;
                vertical-align: middle;
                text-decoration: none;
                background: #ffffff;
                <%= statpadding %>  }

.statEvenRowEvenCol{ font-family: "Arial", "Verdana", "Helvetica", sans-serif;
                 font-size: 12px;
                 font-style: normal;
                 font-weight: normal;
                 color: #666666;
                 vertical-align: middle;
                 text-decoration: none;
                 background: #eeeeee;
                 <%= statpadding %>  }
