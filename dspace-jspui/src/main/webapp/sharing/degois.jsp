<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<dspace:layout titlekey="sharing.degois.title" locbar="off" navbar="off" nocache="true">
	<div id="degoisContent">
			<div id="degoisTitle"><fmt:message key="sharing.degois.title"/></div>
			<div id="degoisDescription"><fmt:message key="sharing.degois.description"/></div>
			<div id="degoisContentInner">
				<div id="degoisForm">
					<form id="sendDocumentToDeGois" action="<%= request.getContextPath() %>/sharing" method="post">
						<input type="hidden" name="id" value="<%= request.getParameter("id") %>" />
						<input type="hidden" name="handler" value="<%= request.getParameter("handler") %>" />
						<input type="hidden" name="step" value="1" />
						<div id="degoisCol1"><fmt:message key="sharing.degois.area"/></div>
						<div id="degoisCol2">
							<select name="degois_area">
								<option value="Ciências Naturais"><fmt:message key="sharing.degois.area.natural"/></option>
								<option value="Engenharia e Tecnologia"><fmt:message key="sharing.degois.area.tech"/></option>
								<option value="Ciências Médicas"><fmt:message key="sharing.degois.area.medical"/></option>
								<option value="Ciências Agrárias"><fmt:message key="sharing.degois.area.agrarian"/></option>
								<option value="Ciências Sociais"><fmt:message key="sharing.degois.area.social"/></option>
								<option value="Humanidades"><fmt:message key="sharing.degois.area.human"/></option>
								<option value="Ciências Exactas"><fmt:message key="sharing.degois.area.exact"/></option>
							</select>
						</div>
						<div class="clearDiv"></div>
						<div id="degoisCol1"><fmt:message key="sharing.degois.form.user"/></div>
						<div id="degoisCol2"><input type="text" name="degois_user" /></div>
						<div class="clearDiv"></div>
						<div id="degoisCol1"><fmt:message key="sharing.degois.form.pass"/></div>
						<div id="degoisCol2"><input type="password" name="degois_pass" /></div>
						<div class="clearDiv"></div>
						<div id="degoisSubmit">
							<input type="submit" value="<fmt:message key="sharing.degois.form.submit"/>"/>
						</div>
					</form>
				</div>
				<div id="degoisInfo">
					<a><img src="<%= request.getContextPath() %>/image/sharing/extra/degois.png"/></a>
					<a href="http://degois.pt" target="_blank"><fmt:message key="sharing.degois.create"/></a>
				</div>
			</div>
	</div>
</dspace:layout>
