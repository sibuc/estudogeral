/*
 * CriaColProvasServlet.java
 *
 * Version: $Revision: 0 $
 *
 * Date: $Date: 2015-12-02
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package pt.ucoimbra.sibuc.dspace.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authenticate.LDAPAuthentication;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.app.util.InputFormSubmission;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

import java.text.SimpleDateFormat;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.content.authority.Choices;

/**
 * Servlet for creating collection for PhD Thesis submission or aggregation
 *
 * @author Ana Luisa Silva - SIBUC
 * @version $Revision: 0 $
 */
public class CriaColProvasServlet extends DSpaceServlet
{
    /** Logger */
    private static Logger log = Logger.getLogger(CriaColProvasServlet.class);

    protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
        /*
         * GET with no parameters displays "find by handle/id" form parameter
         * item_id -> find and edit item with internal ID item_id parameter
         * handle -> find and edit corresponding item if internal ID or Handle
         * are invalid, "find by handle/id" form is displayed again with error
         * message
         */
        log.info("GET antes recolha parametros");
        String candidato = request.getParameter("candidato");
        String emailcandidato = request.getParameter("emailcandidato");
        String emailjuri = request.getParameter("emailjuri");
        String tema = request.getParameter("tema");
        String tid = request.getParameter("tid");
        String faculdade = request.getParameter("faculdade");
        String tipoprova = request.getParameter("tipoprova");
        String embargo =request.getParameter("id_disciplina");
        String submit = request.getParameter("submit");
        String cancel = request.getParameter("cancel");



        boolean showError = false;
        log.info("GET");

       JSPManager.showJSP(request, response, "/academicos/get-col-provas.jsp");

    }

    protected void doDSPost(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {
        // First, see if we have a multipart request (uploading a new bitstream)
        /*
         * Then we check for a "cancel" button - if it's been pressed, we simply
         * return to the "find by handle/id" page
         */
       log.info("POST antes recolha parametros");
        EPerson utiliz = context.getCurrentUser();
         log.info("Depois de getCurrentUser");
      if (utiliz!=null) {
         log.info("utiliz nao e null");
        int grupo_provas = ConfigurationManager.getIntProperty("academicos.grupo.provas");

        Group grupo = Group.find(context, grupo_provas);
        log.info(grupo.getHandle());
        if (grupo.isMember(utiliz)) {
        // log.info("POST antes recolha parametros");
        if (request.getParameter("cancel") != null)
        {
            JSPManager.showJSP(request, response, "/academicos/get-col-provas.jsp");

            return;
        }
        boolean dados_minimos = false;
        String candidato = request.getParameter("candidato");
        String emailcandidato = request.getParameter("emailcandidato");
        String emailjuri = request.getParameter("emailjuri");
        String tema = request.getParameter("tema");
        String tid = request.getParameter("tid");
        String faculdade = request.getParameter("faculdade");
        String tipoprova = request.getParameter("tipoprova");
        String id_disciplina = request.getParameter("id_disciplina");
        String submit = request.getParameter("submit");
        String cancel = request.getParameter("cancel");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String datainicio = df.format(new Date());
        String PreferredHandle = null;

      if (candidato != null && !candidato.matches("")) {
          if (emailcandidato != null && !emailcandidato.matches("")) {
              if (emailjuri != null && !emailjuri.matches("")) {
               if (tipoprova != null && !tipoprova.matches("")) {
                 dados_minimos = true;
              }
            }
          }
      }
     if (!dados_minimos) {

         JSPManager.showJSP(request, response, "/academicos/get-col-provas.jsp");
         return;
    }

        Collection coll = null;
        Community comm = null;
        String descricao = null;
        if (tipoprova.matches("doutoramento")) {
          comm = Community.find(context, ConfigurationManager.getIntProperty("comm.doutoramentos"));
          log.info("tipo prova doutoramento");
          descricao = "Provas de " + tipoprova  + " em " + tema + " apresentadas à " + faculdade;
          log.info(descricao);
        }
        else if (tipoprova.matches("agregacao")) {
         comm = Community.find(context, ConfigurationManager.getIntProperty("comm.agregacao"));
         log.info("tipo prova agregacao");
         descricao = "Agregação em " + tema;
         log.info(descricao);
        }
        log.info("test comm " + comm);
        if (comm !=  null) {
            // Cria colecao e adiciona-a ah comunidade de doutoramentos
            EPerson[] admin = Group.allMembers(context, Group.findByName(context,"Administrator"));
            context.setCurrentUser(admin[0]);
            log.info("Current User - " + admin[0].getName());
              coll = Collection.create(context);
              coll.setMetadata("name", candidato);
              coll.setMetadata("short_description",descricao);
              coll.addMetadata("collection", "idDisciplina", "uc", null, id_disciplina);
              coll.addMetadata("collection", "inicioprazodeposito", null, null, datainicio);
              coll.addMetadata("collection","idIES","dges", null, faculdade);
              coll.addMetadata("collection","faculdade",null, null, faculdade);
              // coll.addMetadata("collection", "identifier", "tid", null, tid);
              coll.createTemplateItem();
              coll.update();
              comm.addCollection(coll);

            // Verifica a existencia dos utilizadores de candidato e juri
            EPerson Ecandidato = null;
            String ldapresponse = null;
            String Identificador = null;
            if (emailcandidato !=null)
                    Identificador = emailcandidato.substring(2, emailcandidato.indexOf("@")-1);
            if (EPerson.findByEmail(context, emailcandidato)==null) {                
                ldapresponse = LDAPAuthentication.academicos_authenticate(context, emailcandidato, null, null, request);
                log.info("Ldap response CriaColProvasServlet(juri)=" + ldapresponse);

               if (ldapresponse!=null){
                  if (EPerson.findByEmail(context, ldapresponse)==null)
                  {
                     Ecandidato= EPerson.create(context);
                     Ecandidato.setCanLogIn(true);
                     Ecandidato.setEmail(ldapresponse); 
                     
                  }
                  else Ecandidato = EPerson.findByEmail(context, ldapresponse);
               }
               else {
                     Ecandidato= EPerson.create(context);
                     Ecandidato.setCanLogIn(true);
                     Ecandidato.setEmail(emailcandidato); 
               }  
                String nome = candidato;
                int tam = nome.length();
                if (tam>63) tam = 63;
                Ecandidato.setFirstName(nome.substring(0, tam));
                Ecandidato.setLastName(tipoprova);
                Ecandidato.setPreferredHandle(coll.getHandle());
                Identificador = emailcandidato.substring(0, emailcandidato.indexOf("@")-1);
                Ecandidato.addMetadata("eperson", "identifier", "uc", null, emailcandidato.substring(0, emailcandidato.indexOf("@")-1));
                log.info("Eperson UC identifier" + Identificador);
                Ecandidato.update();
            }
            else   {
               log.info("Utilizador existe - " + emailcandidato + " handle da colecao - " + coll.getHandle());
                Ecandidato = EPerson.findByEmail(context, emailcandidato);
             //   PreferredHandle = Ecandidato.getPreferredHandle();
             //  if (PreferredHandle==null || PreferredHandle==""){
                log("Ecandidato - preferred_handles - " + Ecandidato.form_getPreferredHandle());
                Ecandidato.setPreferredHandle(coll.getHandle());
                Ecandidato.update();
                log.info(" Fim set preferred handle");
             //   }
             //   else
             //   Ecandidato.setPreferredHandle(PreferredHandle + ";" + coll.getHandle());

            }
           EPerson Ejuri = null;
           log.info("******    emailjuri  *******");
            if (EPerson.findByEmail(context, emailjuri)==null) {
                log.info("emailjuri não existe - " + emailjuri + "tentaldap");
                ldapresponse = LDAPAuthentication.academicos_authenticate(context, emailjuri, null, null, request);
                log.info("Ldap response CriaColProvasServlet(juri)=" + ldapresponse);
             if (ldapresponse!=null){
                  if (EPerson.findByEmail(context, ldapresponse)==null)
                     Ejuri = EPerson.create(context);
                  else
                     Ejuri = EPerson.findByEmail(context, ldapresponse);
                  Ejuri.setCanLogIn(true);
                  Ejuri.setEmail(ldapresponse);
            
        //     else {
        //         Ejuri = EPerson.create(context);
        //          Ejuri.setCanLogIn(true);
        //          Ejuri.setEmail(emailjuri);
        //     }
            String nome = "Juri de - " + candidato;
            int tam = nome.length();
            if (tam>63) tam = 63;
             Ejuri.setFirstName(nome.substring(0, tam));
             Ejuri.setLastName(tipoprova);
             Ejuri.setPreferredHandle(coll.getHandle());
             Ejuri.update();
            }
            }
            else   {
                 
            log.info("emailjuri existe - " + emailjuri + "atualiza");
                Ejuri = EPerson.findByEmail(context, emailjuri);
            //    PreferredHandle = Ejuri.getPreferredHandle();
            //    if (PreferredHandle==null || PreferredHandle==""){
               Ejuri.setPreferredHandle(coll.getHandle());
               Ejuri.update();
            //    }
            //    else
            //    Ejuri.setPreferredHandle(PreferredHandle + ";" + coll.getHandle());
            }
            if (tipoprova.matches("doutoramento")) {
              Item template = coll.getTemplateItem();
              template.addMetadata("dc", "peerreviewed", null, null, "yes");
              template.addMetadata("thesis","degree", "disciplineID", null, id_disciplina);
  //            template.addMetadata("thesis","degree", "grantorUnit",null, faculdade, IES_cod(faculdade),Choices.CF_ACCEPTED);
              template.addMetadata("thesis","degree", "grantor",null, "Universidade de Coimbra");
              template.addMetadata("thesis","degree","level",null, "doutor");
              template.addMetadata("dc","rights", null, null, ConfigurationManager.getProperty("embargo.terms.openaccess"));
              template.addMetadata("dc","contributor","author",null,coll.getName().trim());
              template.addMetadata("dc","identifier","tid",null,tid);
              template.update();
            }
           Group G_DIR = Group.create(context);
           G_DIR.setName("COLLECTION_" + String.valueOf(coll.getID()) + "_DEFAULT_ITEM_READ");
           G_DIR.addMember(Ejuri);
           G_DIR.addMember(Ecandidato);
           G_DIR.addMember(Group.find(context, grupo_provas));
           G_DIR.addMember(Group.findByName(context, "ServicosAcademicos_Atendimento"));
           //  G_DIR.addMember(Group.findByName(context, "ServicosAcademicos_Admin"));
           G_DIR.update();
           Group G_DBR = Group.create(context);
           G_DBR.setName("COLLECTION_" + String.valueOf(coll.getID()) + "_DEFAULT_BITSTREAM_READ");
           G_DBR.addMember(Ejuri);
           G_DBR.addMember(Ecandidato);
           G_DBR.addMember(Group.find(context, grupo_provas));
           G_DBR.addMember(Group.findByName(context, "ServicosAcademicos_Atendimento"));
           G_DBR.update();
        // Altera politicas de acesso
           AuthorizeManager.removeAllPolicies(context, coll);
           AuthorizeManager.addPolicy(context, coll, Constants.ADMIN, Group.findByName(context, "ServicosAcademicos_Admin"));
           AuthorizeManager.addPolicy(context, coll, Constants.READ, G_DIR);
           AuthorizeManager.addPolicy(context, coll, Constants.DEFAULT_ITEM_READ, G_DIR);
           AuthorizeManager.addPolicy(context, coll, Constants.DEFAULT_BITSTREAM_READ, G_DBR);
           Group submitters = coll.createSubmitters();
           submitters.setName("COLLECTION_" + String.valueOf(coll.getID()) + "_SUBMIT");
           submitters.addMember(Ecandidato);
           // submitters.addMember(Group.findByName(context, "ServicosAcademicos_Admin"));
           submitters.update();
           coll.update();
           AuthorizeManager.addPolicy(context, coll, Constants.WRITE, submitters);
           AuthorizeManager.addPolicy(context, coll, Constants.REMOVE, submitters);
           AuthorizeManager.addPolicy(context, coll, Constants.ADD, submitters);
           context.setCurrentUser(utiliz);
           //  AuthorizeManager.addPolicy(context, coll, Constants.ADD, submitters);
           InputFormSubmission.actualiza_forms(context,"input-forms", coll.getHandle(), tipoprova);
           InputFormSubmission.actualiza_forms(context, "item-submission", coll.getHandle(), tipoprova);

           JSPManager.showJSP(request, response, "/academicos/get-col-provas-success.jsp");

        }
context.commit();
}
        else 
        { 
            log.info("Sem permissao");
            JSPManager.showJSP(request, response, "/academicos/get-col-provas-sem-permissao.jsp");
        }
// log.info("Fim POST");

    }
  }

String IES_cod(String Faculdade) {
    String CodFaculdade=null;
    
    switch (Faculdade.trim()) {
        case "Universidade de Coimbra - Faculdade de Ciências e Tecnologia": CodFaculdade = "0501";
                     break;
        case "Universidade de Coimbra - Faculdade de Direito" : CodFaculdade = "0502";
                     break;
        case "Universidade de Coimbra - Faculdade de Economia" : CodFaculdade = "0503";
                     break;
        case "Universidade de Coimbra - Faculdade de Farmácia" : CodFaculdade = "0504";
                     break;
        case "Universidade de Coimbra - Faculdade de Letras" : CodFaculdade = "0505";
                     break;
        case "Universidade de Coimbra - Faculdade de Medicina": CodFaculdade = "0506";
                     break;   
        case "Universidade de Coimbra - Faculdade de Psicologia e de Ciências da Educação": CodFaculdade = "0507";
                     break;
        case "Universidade de Coimbra - Faculdade de Ciências do Desporto e Educação Física" : CodFaculdade = "0508";
                     break;
        case  "Universidade de Coimbra - Colégio das Artes": CodFaculdade = "0509";
                     break;
        case "Universidade de Coimbra - Instituto de Investigação Interdisciplinar" : CodFaculdade = "0510";
                     break;
        default: CodFaculdade = "0500";
                     break;
    }
    
    return CodFaculdade;
}
}
