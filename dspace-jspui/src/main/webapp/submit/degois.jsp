<%--
  - choose-file.jsp
  -
  - Version: $Revision: 3705 $
  -
  - Date: $Date: 2009-04-11 19:02:24 +0200 (Sat, 11 Apr 2009) $
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

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.core.Context" %>
<%@ page import="org.dspace.app.webui.servlet.SubmissionController" %>
<%@ page import="org.dspace.submit.AbstractProcessingStep" %>
<%@ page import="org.dspace.submit.step.UploadStep" %>
<%@ page import="org.dspace.submit.step.DeGoisStep" %>
<%@ page import="org.dspace.app.util.DCInputSet" %>
<%@ page import="org.dspace.app.util.DCInputsReader" %>
<%@ page import="org.dspace.app.util.SubmissionInfo" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>


<%
    request.setAttribute("LanguageSwitch", "hide");

    // Obtain DSpace context
    Context context = UIUtil.obtainContext(request);    

	//get submission information object
    SubmissionInfo subInfo = SubmissionController.getSubmissionInfo(context, request);
   
 	// Determine whether a file is REQUIRED to be uploaded (default to true)
 	// boolean fileRequired = ConfigurationManager.getBooleanProperty("webui.submit.upload.required", true);
%>


<dspace:layout locbar="off"
               navbar="off"
               titlekey="jsp.submit.degois.title"
               nocache="true">

    <form method="post" action="<%= request.getContextPath() %>/submit" enctype="multipart/form-data" onkeydown="return disableEnterKey(event);">
		
		<jsp:include page="/submit/progressbar.jsp"/>
		<%-- Hidden fields needed for SubmissionController servlet to know which step is next--%>
        <%= SubmissionController.getSubmissionParameters(context, request) %>

        <%-- <h1>Submit: Upload a File</h1> --%>
		<h1><fmt:message key="jsp.submit.degois.heading"/></h1>
    
        <%-- <p>Please enter the name of
        <%= (si.submission.hasMultipleFiles() ? "one of the files" : "the file" ) %> on your
        local hard drive corresponding to your item.  If you click "Browse...", a
        new window will appear in which you can locate and select the file on your
        local hard drive. <object><dspace:popup page="/help/index.html#upload">(More Help...)</dspace:popup></object></p> --%>

		<%-- <p><fmt:message key="jsp.submit.degois.info1"/>
			<dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.index\") + \"#upload\"%>"><fmt:message key="jsp.morehelp"/></dspace:popup></p>
        --%>
        
        <%-- FIXME: Collection-specific stuff should go here? --%>
        <%-- <p class="submitFormHelp">Please also note that the DSpace system is
        able to preserve the content of certain types of files better than other
        types.
        <dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.formats\")%>">Information about file types</dspace:popup> and levels of
        support for each are available.</p> --%>
        <%--
		<div class="submitFormHelp"><fmt:message key="jsp.submit.choose-file.info6"/>
        <dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.formats\")%>"><fmt:message key="jsp.submit.choose-file.info7"/></dspace:popup>
        </div>
    	--%>
	<table align="center"><tr><td>
        <table border="0" align="center">
            <tr>
               <td colspan="2"><fmt:message key="jsp.submit.degois.form.title"/></td>
            </tr>
            <tr>
                <td class="submitFormLabel">
                    <%-- Document File: --%>
					<label for="tuser"><fmt:message key="jsp.submit.degois.username"/></label>
                </td>
                <td>
                    <input type="text" name="degois_user" id="tuser" />
                </td>
            </tr>
            <tr>
                <td class="submitFormLabel">
                    <%-- Document File: --%>
					<label for="tpass"><fmt:message key="jsp.submit.degois.password"/></label>
                </td>
                <td>
                    <input type="password" name="degois_pass" id="tpass" />
                </td>
            </tr>
            <tr>
                <td class="submitFormLabel">
                    <%-- Document File: --%>
					<label for="tarea"><fmt:message key="jsp.submit.degois.area"/></label>
                </td>
                <td>
                	<select name="degois_area" id="tarea">
                		<option value="Ci&#234;ncias Naturais"><fmt:message key="jsp.submit.degois.areas.naturalSciences"/></option>
						<option value="Engenharia e Tecnologia"><fmt:message key="jsp.submit.degois.areas.technology"/></option>
						<option value="Ci&#234;ncias M&#233;dicas"><fmt:message key="jsp.submit.degois.areas.medicalSciences"/></option>
						<option value="Ci&#234;ncias Agr&#225;rias"><fmt:message key="jsp.submit.degois.areas.agriculturalSciences"/></option>
						<option value="Ci&#234;ncias Sociais"><fmt:message key="jsp.submit.degois.areas.socialSciences"/></option>
						<option value="Humanidades"><fmt:message key="jsp.submit.degois.areas.humanities"/></option>
						<option value="Ci&#234;ncias Exactas"><fmt:message key="jsp.submit.degois.areas.sciences"/></option>
                	</select>
                </td>
            </tr>
        </table>
        	</td>
	        <td align="center">
	        	<table border="0" align="center" class="degoisTable">
	        		<tr>
	        			<td class="image">
							<img src="image/submit/degois.png" alt="DeGois" />
						</td>
	        		</tr>
	        		<tr>
	        			<td class="text">
	        				<fmt:message key="jsp.submit.degois.info.text"/>
	        			</td>
	        		</tr>
	        		<tr>
	        			<td class="link">
	        				<a href="<%=ConfigurationManager.getProperty("degois.info.url") %>" target="_<%=ConfigurationManager.getProperty("degois.info.open") %>"><fmt:message key="jsp.submit.degois.info.label"/></a>
	        			</td>
	        		</tr>
	        	</table>
	        </td>
	    </tr>
	</table>
		<%-- Hidden fields needed for SubmissionController servlet to know which step is next--%>
        <%= SubmissionController.getSubmissionParameters(context, request) %>
    
        <p>&nbsp;</p>

        <center>
            <table border="0" width="80%">
                <tr>
                    <td width="100%">&nbsp;</td>
               	<%  //if not first step, show "Previous" button
					if(!SubmissionController.isFirstStep(request, subInfo))
					{ %>
                    <td>
                        <input type="submit" name="<%=AbstractProcessingStep.PREVIOUS_BUTTON%>" value="<fmt:message key="jsp.submit.general.previous"/>" />
                    </td>
				<%  } %>
                    <td>
                        <input type="submit" name="<%=DeGoisStep.SUBMIT_DEGOIS_REQUEST%>" value="<fmt:message key="jsp.submit.general.next"/>" />
                    </td> 
                        	<td>
                                <input type="submit" name="<%=DeGoisStep.SUBMIT_SKIP_BUTTON%>" value="<fmt:message key="jsp.submit.degois.skip"/>" />
                            </td>
                              
                    <td>&nbsp;&nbsp;&nbsp;</td>
                    <td align="right">
                        <input type="submit" name="<%=AbstractProcessingStep.CANCEL_BUTTON%>" value="<fmt:message key="jsp.submit.general.cancel-or-save.button"/>" />
                    </td>
                </tr>
            </table>
        </center>  
    </form>
</dspace:layout>
