<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Form requesting a Handle or internal item ID for item editing
  -
  - Attributes:
  -     invalid.id  - if this attribute is present, display error msg
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.jsp.tagext.TagSupport" %>
<%@ page import="javax.servlet.jsp.PageContext" %>
<%@ page import="javax.servlet.ServletException" %>

<%@ page import="org.dspace.core.ConfigurationManager" %>



<%-- invoke "popup" style which elides all the header and footer stuff.
    --%>

<%!
    // get request parameter but return a default value if not present.
    String getDefaultedRequestParameter(HttpServletRequest r, String param, String dflt)
    {
        String result = r.getParameter(param);
        return (result == null) ? dflt : result;
    }
%>
<%
    String mdfield = getDefaultedRequestParameter(request,"field", "FieldMissing");
    String isNameValue = getDefaultedRequestParameter(request,"isName", "false");
    String isRepeatingValue = getDefaultedRequestParameter(request,"isRepeating", "false");
    boolean isName = isNameValue.equalsIgnoreCase("true");
    boolean isRepeating = isRepeatingValue.equalsIgnoreCase("true");
%>

<dspace:layout titlekey="jsp.tool.title"
               style="popup"
               navbar="off"
               locbar="off"
               parenttitlekey="jsp.administer"
               parentlink="/dspace-admin">

<style>
#aspect_general_ChoiceLookupTransformer_field_chooser,.choices-lookup
{
	height:0px;
	display:none;
	visibility:hidden;
}


html body.modal-open form#aspect_general_ChoiceLookupTransformer_div_lookup.ds-interactive-div.popup
{
	visibility:hidden;
	display:none;
}


BODY {
    background: white none repeat scroll 0 0;
    color: #000000;
    font-family: "verdana",Arial,Helvetica,sans-serif;
    font-size: 10pt;
    font-style: normal;
    margin: 0;
    padding: 0;
}


a.close {
    background: transparent none repeat scroll 0 0;
    text-decoration: none;
    border: 0 none;
    cursor: pointer;
    padding: 0;
	margin-top:-55px;
}

.close {
    color: #000;
    float: right;
    font-size: 21px;
    font-weight: bold;
    line-height: 1;
    opacity: 0.2;
    text-shadow: 0 1px 0 #fff;
}

.sr-only {
    border: 0 none;
    clip: rect(0px, 0px, 0px, 0px);
    height: 1px;
    margin: -1px;
    overflow: hidden;
    padding: 0;
    position: absolute;
    width: 1px;
}

#look
{
border-bottom: 1px solid #e5e5e5;
}

div.clearfix
{
	margin-left:-40px !important;
}

div.DataTables_sort_wrapper
{
	/*margin-left:-149px !important;*/
}

div.dataTables_info
{
	margin-left:-46px !important;
}

div#DataTables_Table_0_wrapper.dataTables_wrapper.no-footer div.clearfix div.vcard-wrapper.col-xs-8
{
	margin-left: 240px;
    position: absolute;
    top: 120px;
}

div#DataTables_Table_0_wrapper.dataTables_wrapper.no-footer div.fg-toolbar.ui-toolbar.ui-widget-header.ui-helper-clearfix.ui-corner-tl.ui-corner-tr,
div#DataTables_Table_0_wrapper.dataTables_wrapper.no-footer div.clearfix div.vcard-wrapper.col-xs-8
{
	 float: right !important;
}

.authorlookup .vcard-wrapper .vcard {
    list-style: outside none none;
    margin: 0;
}

.authorlookup .vcard-wrapper {
  /*  border-left: 1px solid #ccc;*/
    margin-left: -1px;
    padding: 15px;
}

.col-xs-8 {
    width: 45%;
	position: relative;
	margin-right:0px;
}
.col-xs-4 {
    /*width: 30%;*/
}
.clearfix::before, .clearfix::after {
    content: " ";
    display: table;
}

/*.authorlookup .clearfix {
    border: 1px solid #ccc;
}*/

table {
    border-collapse: collapse;
    border-spacing: 0;
}

.btn {
    -moz-user-select: none;
    border: 1px solid transparent;
    border-radius: 4px;
    cursor: pointer;
    display: inline-block;
    font-size: 12px;
    font-weight: normal;
    line-height: 1.42857;
    margin-bottom: 0;
    padding: 4px 12px;
    text-align: center;
    vertical-align: middle;
    white-space: nowrap;
}

.btn-default, .btn-primary, .btn-success, .btn-info, .btn-warning, .btn-danger {
    box-shadow: 0 1px 0 rgba(255, 255, 255, 0.15) inset, 0 1px 1px rgba(0, 0, 0, 0.075);
    text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.2);
}


.btn-default {
    background-image: linear-gradient(to bottom, #fff 0px, #e6e6e6 100%);
    background-repeat: repeat-x;
    border-color: #ccc;
    text-shadow: 0 1px 0 #fff;
}

li.vcard-add.list-group-item input.ds-button-field.btn.btn-default
{
	margin-top:10px;
}

.authorlookup table.dataTable tbody tr.current-item, .authorlookup table.dataTable tbody tr.current-item td {
    background-color: #607890;
    color: white;
}

.authorlookup .vcard-wrapper .no-vcard-selected {
    display: block;
    font-size: 123.1%;
    font-weight: bold;
    padding: 10px;
    text-align: center;
}
</style>
<script>
if(!window.DSpace){window.DSpace={};}window.DSpace.context_path='https://193.137.200.162/xmlui';window.DSpace.theme_path='https://193.137.200.162/xmlui/themes/Mirage2/';
</script>


<body>
<div id="look"><h1><fmt:message key="jsp.tools.person-lookup.title"/></div><%--= LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.field."+mdfield+".title") --%></h1>
<form id="aspect_general_ChoiceLookupTransformer_div_lookup"
        class="ds-interactive-div popup" action="" method="get">
    <fieldset id="aspect_general_ChoiceLookupTransformer_list_choicesList"
              class="ds-form-list choices-lookup">
           
   <%-- Results @1@ to @2@ of @3@ for "@4@" --%>
    <legend><fmt:message key="jsp.tools.lookup.results"/></legend>
    <ol>
      <li id="aspect_general_ChoiceLookupTransformer_item_select" class="ds-form-item choices-lookup"> 
        <div class="ds-form-content">
          <div>
            <select onChange="javascript:DSpaceChoicesSelectOnChange();" id="aspect_general_ChoiceLookupTransformer_field_chooser" class="ds-select-field choices-lookup" name="chooser"
             size="<%= String.valueOf(ConfigurationManager.getIntProperty("webui.lookup.select.size", 12)) %>">
             <!--space filler because "unclosed" select annoys browsers-->
            </select>
            <img style="display:none;" alt="Loading..." id="lookup_indicator_id" class="choices-lookup"
              src="<%= request.getContextPath() %>/image/authority/load-indicator.gif" />
          </div>
          <input type="hidden" name="paramField"          value="<%= getDefaultedRequestParameter(request,"field", "") %>" />
          <input type="hidden" name="paramValue"          value="<%= getDefaultedRequestParameter(request,"value", "") %>" />
          <input type="hidden" name="paramIsName"         value="<%= isNameValue %>" />
          <input type="hidden" name="paramIsRepeating"    value="<%= isRepeatingValue %>" />
          <input type="hidden" name="paramValueInput"     value="<%= getDefaultedRequestParameter(request,"valueInput", "") %>" />
          <input type="hidden" name="paramAuthorityInput" value="<%= getDefaultedRequestParameter(request,"authorityInput", "") %>" />
          <input type="hidden" name="paramStart"          value="<%= getDefaultedRequestParameter(request,"start", "0") %>" />
          <input type="hidden" name="paramLimit"          value="<%= getDefaultedRequestParameter(request,"limit", "0") %>" />
          <input type="hidden" name="paramFormID"         value="<%= getDefaultedRequestParameter(request,"formID", "") %>" />
          <input type="hidden" name="paramIsClosed"       value="<%= getDefaultedRequestParameter(request,"isClosed", "false") %>" />
          <input type="hidden" name="paramConfIndicatorID" value="<%= getDefaultedRequestParameter(request,"confIndicatorID", "") %>" />
          <input type="hidden" name="paramCollection"      value="<%= getDefaultedRequestParameter(request,"collection", "-1") %>" />
          



          <%-- XXX get this from dspace config if available..?? --%>
          <input type="hidden" name="paramNonAuthority"   value="<%= LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.field."+mdfield+".nonauthority") %>" />

          <input name="paramFail" type="hidden" value="<%= LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.fail") %>" />
          <input name="contextPath" type="hidden" value="<%= request.getContextPath() %>" />
        </div>
      </li>
      <li id="aspect_general_ChoiceLookupTransformer_item_textFields" class="ds-form-item choices-lookup"> 
        <div class="ds-form-content">

          <%-- if (isName) { --%>
          <% if (1==1) { %>
          <%-- XXX get this from dspace config if available..?? --%>
            <% String help1 = LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.field."+mdfield+".help.last"); %>
            <% String help2 = LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.field."+mdfield+".help.first"); %>
            <label class="ds-composite-component">
              <input class="ds-text-field choices-lookup" name="text1" type="text" value=""
                  title="<%= help1 %>" />
              <br/><%= help1 %>
            </label>
            <label class="ds-composite-component last">
              <input class="ds-text-field choices-lookup" name="text2" type="text" value=""
                  title="<%= help2 %>" />
              <br/><%= help2 %>
            </label>
            <input class="ds-text-field choices-lookup" name="text3" type="text" value=""
                  title="orcid" />
            <input class="ds-text-field choices-lookup" name="text4" type="text" value=""
                  title="id" />
          <% } else { %>
          <%-- XXX get this from dspace config if available..?? --%>
          <%-- ARBT --- EDIT --%>
            <% String help = LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.field."+mdfield+".help"); %>
            <label class="ds-composite-component">
              <input class="ds-text-field choices-lookup" name="text1" type="text" value=""
                  title="<%= help %>" />
              <br/><%= help %>
            </label>
          <% } %>
          <div class="spacer"> </div>
        </div>
      </li>
      <li class="ds-form-item last choices-lookup"> 
        <div class="ds-form-content">
          <input name="accept"  onClick="javascript:DSpaceChoicesAcceptOnClick();" type="button" class="ds-button-field choices-lookup"
                value="<%= LocaleSupport.getLocalizedMessage(pageContext, isRepeating ? "jsp.tools.lookup.add":"jsp.tools.lookup.accept") %>"/>
          <input name="more"  onClick="javascript:DSpaceChoicesMoreOnClick();" type="button"   class="ds-button-field choices-lookup" disabled="disabled"
                value="<%= LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.more") %>"/>
          <input name="cancel"  onClick="javascript:DSpaceChoicesCancelOnClick();" type="button" class="ds-button-field choices-lookup"
                value="<%= LocaleSupport.getLocalizedMessage(pageContext, "jsp.tools.lookup.cancel") %>"/>
        </div>
      </li>
    </ol>
  </fieldset>
</form>

<script type="text/javascript">
        var form = document.getElementById('aspect_general_ChoiceLookupTransformer_div_lookup');
        DSpaceChoicesSetup(form);
</script>
<!--<script src="http://dspacetestes.bg.uc.pt:8080/xmlui/themes/Mirage2/scripts/theme.js"></script>
<script src="http://dspacetestes.bg.uc.pt:8080/xmlui/themes/Mirage2/scripts/person-lookup.js"></script>-->

<script src="https://193.137.200.162/jspui/scripts/theme.js"></script>
<script src="https://193.137.200.162/jspui/scripts/person-lookup.js"></script>




<!--<button onclick="javascript:AuthorLookup('http://dspacetestes.bg.uc.pt:8080/xmlui/choices/dc_contributor_author', 'dc_contributor_author', 1239);" type="button" name="lookup_dc_contributor_author" class="ds-button-field ds-add-button btn btn-default ">Lookup</button>-->

<%
    String colec= getDefaultedRequestParameter(request,"collection", "-1");
%>



<%
/*    Locale[] supportedLocales = I18nUtil.getSupportedLocales();
    Locale sessionLocale = UIUtil.getSessionLocale(request);
	String current_lang = UIUtil.getSessionLocale(request).toString();
	if (current_lang.equals("pt_PT")) {String current_lang = "PT";}
	if (current_lang.equals("en")) {String current_lang = "EN";}*/
		
%>
     
<script type="text/javascript">

    function getMoreMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.more"/>';
	}
    function getLessMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.less"/>';
	}
    function getNameMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.name"/>';
	}
	 function getLoadMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.load"/>';
	}
	function getNoneMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.noone"/>';
	}
	function getItemMessage() {
    return '<fmt:message key="jsp.tools.person-lookup.items"/>';
	}
	function getAddPersonMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.addperson"/>';
	 //return "Add this person";
	}
	function getNoPersonMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.noperson"/>';
	 //return "Add this person";
	}
    function getShowMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.show"/>';
	}
	function getResultsMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.results"/>';
	}
	function getFirstNameMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.firstname"/>';
	}
    function getLastNameMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.lastname"/>';
	}
 	function getProcessingMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.processing"/>';
	}
    function getSearchMessage() {
     return '<fmt:message key="jsp.tools.person-lookup.search"/>';
	}
 



	
</script>



<script type="text/javascript">
    AuthorLookup('https://193.137.200.162/xmlui/choices/dc_contributor_author', 'dc_contributor_author',<%=colec%>,'<fmt:message key="jsp.tools.person-lookup.name"/>');
</script>

 </fieldset>
              </form>
</body>
</html>

</dspace:layout>
