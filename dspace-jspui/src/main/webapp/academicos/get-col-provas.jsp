<%-- 
    Document   : get-col-provas
    Created on : 2/Dez/2015, 16:19:47
    Author     : dspace
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%--
  - get-col-provas.jsp
  -
  - Version: $Revision: 3705 $
  -
  - Date: $Date: 2009-04-11 17:02:24 +0000 (Sat, 11 Apr 2009) $
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
  - Form requesting a Handle or internal item ID for item editing
  -
  - Attributes:
  -     invalid.id  - if this attribute is present, display error msg
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.core.ConfigurationManager" %>

<dspace:layout titlekey="jsp.tools.get-col-provas.title" navbar="default" locbar="link">

	<%-- <h1>Cria Colecao para provas</h1> --%>
	<h1><fmt:message key="jsp.tools.get-col-provas.heading"/></h1>
    
    <%-- <p>Enter the Handle or internal item ID of the item you want to edit or
    delete.  <dspace:popup page="/help/site-admin.html#items">More help...</dspace:popup></p> --%>

<%--	<div><fmt:message key="jsp.tools.get-col-provas.info2"/><dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.site-admin\") + \"#items\"%>"><fmt:message key="jsp.morehelp"/></dspace:popup></div> --%>
    
    <form method="post" action="<%= request.getContextPath() %>/criaprovas">
     <center>
        <table class="miscTable">
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">Candidato:</td> --%>
	      <td class="submitFormLabel">
                  <label for="tcandidato"><fmt:message key="jsp.tools.get-col-provas.candidato"/></label>
              </td>
              <td>
                 <input size="50" type="text" name="candidato" id="tcandidato"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">EmailCandidato:</td> --%>
	      <td class="submitFormLabel">
                  <label for="temailcandidato"><fmt:message key="jsp.tools.get-col-provas.emailcandidato"/></label>
              </td>
              <td>
                 <input size="50" type="text" name="emailcandidato" id="temailcandidato"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">EmailJuri:</td> --%>
	      <td class="submitFormLabel">
                  <label for="temailjuri"><fmt:message key="jsp.tools.get-col-provas.emailjuri"/></label>
              </td>
              <td>
                 <input size="50" type="text" name="emailjuri" id="temailjuri"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">Tema da tese:</td> --%>
	      <td class="submitFormLabel">
                  <label for="ttema"><fmt:message key="jsp.tools.get-col-provas.tema"/></label>
              </td>
              <td>
                 <input size="50" type="text" name="tema" id="ttema"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">TID do trabalho(RENATES):</td> --%>
	      <td class="submitFormLabel">
                  <label for="ttema"><fmt:message key="jsp.tools.get-col-provas.tid"/></label>
              </td>
              <td>
                 <input size="50" type="text" name="tid" id="ttid"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">EmailCandidato:</td> --%>
	      <td class="submitFormLabel">
                  <label for="tfaculdade"><fmt:message key="jsp.tools.get-col-provas.faculdade"/></label>
              </td>
              <td>
                <%--  <input size="50" type="text" name="faculdade" id="tfaculdade"/>  --%>
                    <select id="tfaculdade" name="faculdade">
                        <option value="Universidade de Coimbra - Colégio das Artes">Colégio das Artes</option>
                        <option value="Universidade de Coimbra - Faculdade de Ciências do Desporto e Educação Física">Faculdade de Ciências do Desporto e Educação Física</option>
                        <option value="Universidade de Coimbra - Faculdade de Ciências e Tecnologia">Universidade de Coimbra - Faculdade de Ciências e Tecnologia</option>
                        <option value="Universidade de Coimbra - Faculdade de Direito">Faculdade de Direito</option>
                        <option value="Universidade de Coimbra - Faculdade de Economia">Faculdade de Economia</option>
                        <option value="Universidade de Coimbra - Faculdade de Farmácia">Faculdade de Farmácia</option>
                        <option value="Universidade de Coimbra - Faculdade de Letras">Faculdade de Letras</option>
                        <option value="Universidade de Coimbra - Faculdade de Medicina">Faculdade de Medicina</option>
                        <option value="Universidade de Coimbra - Faculdade de Psicologia e de Ciências da Educação">Faculdade de Psicologia e de Ciências da Educação</option>
                        <option value="Universidade de Coimbra - Instituto de Investigação Interdisciplinar">Instituto de Investigação Interdisciplinar</option>
                </select>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
            <%-- <td class="submitFormLabel">Embargo:</td> --%>
	      <td class="submitFormLabel">
                  <label for="tid_disciplina"><fmt:message key="jsp.tools.get-col-provas.id_disciplina"/></label>
              </td>
              <td>
                  <input size="50" type="text" name="id_disciplina" id="tid_disciplina"/>
              </td>
          </tr>
          <tr><td></td></tr>
          <tr><td></td></tr>
          <tr class="oddRowEvenCol">
              <td class="submitFormLabel"><fmt:message key="jsp.tools.get-col-provas.tipoprova"/></td>
              <td></td>
          </tr>
          <tr>
              <td class="submitFormLabel"><input type="radio" name="tipoprova" id="tprova_dout" value="doutoramento" /><label for="tprova_dout"><fmt:message key="jsp.tools.get-col-provas.provadoutoramento"/></label></td>
              <td class="submitFormLabel"><input type="radio" name="tipoprova" id="tprova_mest" value="agregacao" /><label for="tprova_mest"><fmt:message key="jsp.tools.get-col-provas.provaagregacao"/></label></td>
          </tr>
          

          <tr><td></td></tr>
          <tr><td></td></tr>
             <tr class="oddRowEvenCol">
             <td>
             <input type="submit" name="submit" value="<fmt:message key="jsp.tools.get-col-provas.submitbutton"/>" />
             </td>
             <td>
             <input type="submit" name="cancel" value="<fmt:message key="jsp.tools.get-col-provas.cancelbutton"/>" />
             </td>
            </tr>
            </table>
        </center>
    </form>
</dspace:layout>

