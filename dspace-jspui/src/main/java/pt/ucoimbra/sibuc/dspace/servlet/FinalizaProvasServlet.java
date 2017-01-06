/*
 * FinalizaProvasServlet.java
 *
 * Version: $Revision: 1.0 $
 *
 * Date: $Date: 2016-01-27 $
 *
 * Copyright (c) 2016, SIBUC, University de Coimbra.  All rights reserved.
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
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STsent_email_pedido_copia:RICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package pt.ucoimbra.sibuc.dspace.servlet;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.lang.String;
import java.sql.SQLException;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.ListIterator;
import org.dspace.authenticate.LDAPAuthentication;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


import java.text.SimpleDateFormat;
import java.text.DateFormat;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.servlet.HandleServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.core.Email;
import org.dspace.core.LogManager;
import org.dspace.core.Constants;
import org.dspace.eperson.EPerson;
import org.dspace.handle.HandleManager;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Metadatum;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.storage.bitstore.BitstreamStorageManager;


/**
 * Servlet for sending paper copy requests to academic department
 *
 * @author  Ana Luisa Silva
 * @version $Revision: 1.0 $
 */
public class FinalizaProvasServlet extends DSpaceServlet
{
    /** log4j category */
    private static Logger log = Logger.getLogger(FinalizaProvasServlet.class);
    
    public static final String DOUTORAMENTO = "doctoralThesis";
    
    private static final String PeriodoEmbargo = "uc.date.periodoEmbargo";

       
    protected void doDSGet(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {

          log.info("finaliza_provas doDSGet");
          processForm(context, request, response);


    }

    protected void doDSPost(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        // Treat as a GET
        log.info("finaliza_provas doDSPost");
        doDSGet(context, request, response);
    }
 
    private void processForm (Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {  
       log.info("Process-form");
       
       String uc_periodo_embargo = ConfigurationManager.getProperty("embargo.terms.periodoembargo");
       if (uc_periodo_embargo== null)
           uc_periodo_embargo = PeriodoEmbargo;
           
              // inicializa variaveis a recolher
       
       Collection coll_dout = (Collection) HandleManager.resolveToObject(context, ConfigurationManager.getProperty("coll.doutoramentos"));
       String thesis_renates_id = null;  
       String thesis_degree_discipline_id = null;
       String thesis_financiamento = null;
       String thesis_date_embargo = null;       
       // inicializa variaveis a preencher automaticamente ou calcular
       String thesis_degree_name = null;
       String thesis_degree_level = "doutor";
       String thesis_peerreview = "yes";
       String thesis_degree_grantor = "Universidade de Coimbra";
       String thesis_degree_grantor_unit = null;    
       String thesis_author_mail = null;
       String thesis_author_name = null;
       String thesis_author_id = null;
       String thesis_subject_fos = null;
       String datadefesa = null;
       String thesis_degree_classification = null;
       String thesis_date_embargo_end_date = null;
       ItemIterator item = null;
       Item curr_item = null;
       String itemID = null;
       Collection collection = null;
       String collection_name = null;
       String path = request.getPathInfo();
       String return_path = path;  
       String handle = null;

       
     
        if (path != null)
        {
            // substring(1) is to remove initial '/'
            path = path.substring(1);

            try
            {
                // Extract the Handle
                int firstSlash = path.indexOf('/');
                int secondSlash = path.indexOf('/', firstSlash + 1);

                if (secondSlash != -1)
                {
                    // We have extra path info
                    handle = path.substring(0, secondSlash);
                    
                }
                else
                {
                    // The path is just the Handle
                    handle = path;
                }
            }
            catch (NumberFormatException nfe)
            {
                // Leave handle as null
            }
        }
       

  

       if (handle != null && !handle.equals(""))
        {
          log.info("handle = " + handle);
          collection  = (Collection) HandleManager.resolveToObject(context, handle);
          collection_name = collection.getName();
          // obtem nome da tese a partir da descrição da coleção criada
          if (collection.getMetadataByMetadataString("dc.description.abstract").length>0) 
            if (collection.getMetadataByMetadataString("dc.description.abstract")[0].value!= null)
               thesis_degree_name = collection.getMetadataByMetadataString("dc.description.abstract")[0].value;
          if (collection.getMetadataByMetadataString("collection.idIES.dges").length > 0)
            if (collection.getMetadataByMetadataString("collection.idIES.dges")[0].value!=null)
               thesis_degree_grantor = collection.getMetadataByMetadataString("collection.idIES.dges")[0].value;
          if (collection.getMetadataByMetadataString("collection.faculdade").length > 0)
            if (collection.getMetadataByMetadataString("collection.faculdade")[0].value!= null)
          	thesis_degree_grantor_unit = collection.getMetadataByMetadataString("collection.faculdade")[0].value;
          item = collection.getAllItems();
          Boolean termina = false;
          int i=0; 
          while (!termina && (item.hasNext())) {
            curr_item=item.next();
            i++;
            log.info("curr_item("+ i + ")= " + curr_item.getHandle());
            if (curr_item.getMetadata("dc.type").equals(DOUTORAMENTO)) {
                termina = true;
               // Pede formulario para completar dados
               // identifica aluno e recolhe email
               log.info("dctype eh doctoralThesis ....alterado!!!");
               thesis_author_name = curr_item.getMetadata("dc.contributor.author");
               
               thesis_subject_fos = curr_item.getMetadata("dc.subject.fos");
               EPerson author = curr_item.getSubmitter();
               if (curr_item.getMetadata("dc.relation")!=null) {
                 if (curr_item.getMetadata("dc.relation").contains("info-repo")) 
                   thesis_financiamento = curr_item.getMetadata("dc.relation");
               }
               if (curr_item.getMetadata(uc_periodo_embargo)!=null)
                   thesis_date_embargo = curr_item.getMetadata(uc_periodo_embargo);
               log.info("thesis_date_embargo:" + thesis_date_embargo);
               // verifica se o aluno é o depositante
               if (curr_item.getMetadata("dc.identifier.tid")!=null)
                   thesis_renates_id = curr_item.getMetadata("dc.identifier.tid");
               log.info("thesis_renates_id:" + thesis_renates_id);
               if (curr_item.getMetadata("thesis.degree.disciplineID")!=null)
                   thesis_degree_discipline_id= curr_item.getMetadata("thesis.degree.disciplineID");
               log.info("thesis.degree.disciplineID:" + thesis_degree_discipline_id);
               String netID = author.getNetid();
               if (netID.contains("student")) {
                   // o aluno eh o depositante
                   context.turnOffAuthorisationSystem();
                   thesis_author_mail = author.getEmail();
                   if (author.getMetadata("eperson","identifier","uc", Item.ANY).length>0)
                       thesis_author_id = author.getMetadata("eperson","identifier","uc", Item.ANY)[0].value;
                   else thesis_author_id = null;
                   if (thesis_author_id==null || thesis_author_id=="") {
                         thesis_author_id = netID.substring(2, netID.indexOf("@")-1);
                         log.info("author id - " + thesis_author_id);
                         author.addMetadata("eperson", "identifier", "uc", null, thesis_author_id);
                         author.update();
                   
                 }
<<<<<<< HEAD
                 context.restoreAuthSystemState();
=======
		context.restoreAuthSystemState(); 
>>>>>>> 6b1ba134a1f7a0f20c4dc27fb10f44dd66e683ea
               }  // se deposito efetuado pelos servicos, tem que pedir id do aluno...
       if (request.getParameter("submit") == null)
       {
               request.setAttribute("handle",handle);
               itemID = String.valueOf(curr_item.getID());
               request.setAttribute("itemID",itemID);
               request.setAttribute("collection_name",collection_name);
               request.setAttribute("thesis_degree_name",thesis_degree_name);
               request.setAttribute("thesis_degree_grantor", thesis_degree_grantor);
               request.setAttribute("thesis_degree_grantor_unit",thesis_degree_grantor_unit);
               request.setAttribute("thesis_renates_id",thesis_renates_id);
               request.setAttribute("thesis_degree_discipline_id",thesis_degree_discipline_id);
              // request.setAttribute("thesis_financiamento",thesis_financiamento);
               request.setAttribute("thesis_author_mail",thesis_author_mail);
               request.setAttribute("thesis_author_name", thesis_author_name);
               request.setAttribute("thesis_author_id", thesis_author_id);
               request.setAttribute("thesis_date_embargo",thesis_date_embargo);
               
               JSPManager.showJSP(request, response, "/academicos/migraProvas-form.jsp");
               return;
        }
       else // depois de submit form
       {
          log.info("submit form");
          handle = request.getParameter("handle");
          log.info("handle:" + handle);
          itemID = request.getParameter("itemID");
          log.info("itemID:" + itemID);
          collection_name = request.getParameter("collection_name");
          log.info("collection_name:" + collection_name);
          thesis_degree_name = request.getParameter("thesis_degree_name");
          log.info("thesis_degree_name:" + thesis_degree_name);
          thesis_renates_id = request.getParameter("thesis_renates_id");
          log.info("thesis_renates_id:" + thesis_renates_id);
          thesis_author_name = request.getParameter("thesis_author_name");
          log.info("thesis_degree_name:" + thesis_degree_name);
          thesis_degree_discipline_id = request.getParameter("thesis_degree_discipline_id");
          log.info("thesis_degree_discipline_id:" + thesis_degree_discipline_id);
          thesis_author_id = request.getParameter("thesis_author_id");
          log.info("thesis_author_id:" + thesis_author_id);
          thesis_date_embargo = request.getParameter("thesis_date_embargo");
          log.info("thesis_date_embargo:" + thesis_date_embargo);
          
          Boolean dados_necessarios = (thesis_degree_discipline_id!=null) && (thesis_author_id!= null) &&
                                      (itemID!=null);
          if (dados_necessarios) {
              // obtem dados Nonio
              log.info("dados_necessarios");
              String url_nonio = ConfigurationManager.getProperty("academicos.url.nonio");
              String user_nonio = ConfigurationManager.getProperty("academicos.user.nonio");
              String pass_nonio = ConfigurationManager.getProperty("academicos.pass.nonio");
              String url = url_nonio + "?username=" + user_nonio + "&password=" + pass_nonio +
                           "&nrAluno=" + thesis_author_id + "&codigoDisciplina=" + thesis_degree_discipline_id;
              log.info("url: " + url);
              log.info("itemID:" + itemID);
              int item_nr = Integer.parseInt(itemID);
              log.info("item_nr - " + item_nr);
              curr_item = Item.find(context,item_nr);
              if (curr_item == null)
                  log.info("curr_item is null");
              else
                  log.info("curr_item is NOT null");
             if  (XMLWebService(context, request, url,curr_item)) {
              ////
              curr_item = Item.find(context,item_nr);
              if (curr_item.getMetadata("dc","identifier","tid", Item.ANY).length == 0 ) {
                  curr_item.addMetadata("dc", "identifier", "tid", null, thesis_renates_id);
                  curr_item.updateMetadata();
                  log.info("dc.identifier.tid:"+curr_item.getMetadata("dc","identifier","tid", Item.ANY)[0].value+"Codigo Renates:"+thesis_renates_id);
              } else log.info("dc.indentifier.tid is not NULL:"+ curr_item.getMetadata("dc","identifier","tid", Item.ANY)[0].value);
              if (curr_item.getMetadata("thesis","degree","disciplineID", Item.ANY).length == 0) {
                  curr_item.addMetadata("thesis","degree","disciplineID", null, thesis_degree_discipline_id);
                  curr_item.updateMetadata();
                  log.info("DisciplineID:"+ curr_item.getMetadata("thesis","degree","disciplineID", Item.ANY)[0].value);
              }
             // if (curr_item.getMetadata("thesis","financiamento",Item.ANY, Item.ANY).length == 0) {
             //     curr_item.addMetadata("thesis","financiamento",null, null, thesis_financiamento);
             //     curr_item.updateMetadata();
             // }
              if (curr_item.getMetadata("thesis.degree.name") == null) {
                  curr_item.addMetadata("thesis","degree","name", "por", thesis_degree_name);
                  curr_item.updateMetadata();
              }
              if (curr_item.getMetadata("thesis","degree","level", Item.ANY).length == 0) {
                  curr_item.addMetadata("thesis","degree","level", null, thesis_degree_level);
                  curr_item.updateMetadata();
              }
              if (curr_item.getMetadata("dc","peerreviewed",Item.ANY, Item.ANY).length == 0) {
                  curr_item.addMetadata("dc","peerreviewed",null, null, thesis_peerreview);
                  curr_item.updateMetadata();
              }
              if (curr_item.getMetadata("thesis","degree","grantor", Item.ANY).length == 0) {
                  curr_item.addMetadata("thesis","degree","grantor", null, thesis_degree_grantor);
                  curr_item.updateMetadata();
              }
              curr_item.update();
              // introduzido embargo
              // altera dc.rights e dc.date.embargoEndDate
              // emargoEndDate = date.issued + date.embargo
              Calendar c = Calendar.getInstance();
              if (thesis_date_embargo!= null){
                  curr_item.clearMetadata("dc", "rights", null, Item.ANY);
                  curr_item.updateMetadata();
                  int embargo = get_embargo_days(thesis_date_embargo);
                  log.info("Periodo de embargo - "+ embargo);
                  Metadatum[] date_issued = curr_item.getMetadata("dc","date","issued", Item.ANY);
                  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                  SimpleDateFormat formatter_nonio = new SimpleDateFormat("dd-MM-yyyy");
                  Date data = null;
                  String date_i = null;
                  if (date_issued.length>0) {
                         date_i= date_issued[0].value;
                         log.info("date_i -"+String.format(date_issued[0].value,formatter));
                  }
                  if ( date_i== null) {
                      data = Calendar.getInstance().getTime();
                      date_i = formatter.format(data);
                  }
                  else {
	             try {
		         data = formatter.parse(date_i);
                     	} catch (Exception e) {
		     e.printStackTrace();
	             }    
                  }
                  
                  c.setTime(data);
                  c.add(Calendar.DATE, embargo);
                  log.info("Data de fim de embargo:" + formatter.format(c.getTime()));
                  log.info("c.after(Calendar.getTime()):" + c.getTime().after(Calendar.getInstance().getTime()) );
                  if (c.getTime().after(Calendar.getInstance().getTime())) {
                     // data de fim de embargo posterior a data actual
                     log.info("Data de embargo posterior a data atual");
                     curr_item.clearMetadata("dc", "rights", null, Item.ANY);
                     curr_item.addMetadata("dc", "rights", null, "eng", thesis_date_embargo);
                     if (curr_item.getMetadata("dc", "date", "embargoEndDate", Item.ANY) != null)
                        curr_item.clearMetadata("dc", "date", "embargoEndDate", Item.ANY);
                        curr_item.addMetadata("dc", "date", "embargoEndDate", null, formatter.format(c.getTime()));
                     if (curr_item.getMetadata("dc", "date", "embargo", Item.ANY) != null)
                        curr_item.clearMetadata("dc", "date", "embargo", Item.ANY);
                        curr_item.addMetadata("dc", "date", "embargo", null, formatter.format(c.getTime()));
                  }
                  // sem embargo
                  else {
                       log.info("Sem Embargo.");
                       if (curr_item.getMetadata("dc","rights",null, Item.ANY) != null) curr_item.clearMetadata("dc", "rights", null, Item.ANY);
                         curr_item.addMetadata("dc","rights",null,"eng",ConfigurationManager.getProperty("embargo.terms.openaccess"));
                  }
                   }
                  // sem embargo
                  else {
                       log.info("Sem Embargo.");
                       if (curr_item.getMetadata("dc","rights",null, Item.ANY) != null) curr_item.clearMetadata("dc", "rights", null, Item.ANY);
                         curr_item.addMetadata("dc","rights",null,"eng",ConfigurationManager.getProperty("embargo.terms.openaccess"));
                  }
                  curr_item.update();
                  // acrescentados os dados, migra para a colecao respetiva
                  // apenas faz a eliminacao e migracao relativa a comunidade de provas academicas
                  // as restantes, caso existam, nao sao alteradas
                 
                 
                  curr_item.move_thesis_dissertations(collection,coll_dout, c);
                  log.info("antes collection.delete");
                 
                  // Faz tb o mapeamento para a colecao da unidade organica
                
                curr_item.update();
                // apaga a colecao original
                Community cc = (Community) collection.getParentObject();
                cc.removeCollection(collection);
                context.commit();
                request.setAttribute("handle",curr_item.getOwningCollection().getHandle());
                JSPManager.showJSP(request, response,"/academicos/migraProvas_concluido.jsp");
                return;
              } 
             else {
                request.setAttribute("handle",handle);
                JSPManager.showJSP(request, response,"/academicos/migraProvas_rejeitado.jsp");
             }
          }
         else 
              log.info("nao tem dados_necessarios");
          }
         
         
       }
          else log.info("Nao e tese de doutoramento"); 
          
        }
    }
}
    
public static boolean XMLWebService(Context context, HttpServletRequest request,String url_webservice, Item curr_item) {

    HttpURLConnection connection = null;
    
    try {
      URL url = new URL(url_webservice);

      connection = (HttpURLConnection)url.openConnection();
        
      InputStream content = connection.getInputStream();
      	
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(content);
      //optional, but recommended
	doc.getDocumentElement().normalize();

	log.info("Root element :" + doc.getDocumentElement().getNodeName());
			
	NodeList nList = doc.getChildNodes();
        
			
	log.info("----------------------------" + nList.getLength());

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);
				
		log.info("\nCurrent Element :" + nNode.getNodeName());
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
                                          
                        NodeList nodemap = eElement.getChildNodes();
                        
                        for (int j = 0 ; j < nodemap.getLength(); j++) {
                     
			log.info("nodemap type("+ j + "):"+ nodemap.item(j).getNodeType() + "length -- " + nodemap.getLength());                 
                        log.info("nodemap name("+ j + "):"+ nodemap.item(j).getNodeName() + "length -- " + nodemap.getLength());
                        NodeList nlist = nodemap.item(j).getChildNodes();
                        log.info("nlist length - " + nlist.getLength());
                        String[] e = new String[7];
                        // **** elementos do array
                        // e[0] - nome
                        // e[1] - schema
                        // e[2] - element
                        // e[3] - qualifier
                        // e[4] - language
                        // e[5] - elemento externo ou nao (juri ou orientador)
                        // e[6] - genero
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     e[4] = null;
                                   }
                                   if (listaorient.item(norient).getNodeName().equals("ax27:elementoExterno")) {
                                       e[5] = listaorient.item(norient).getTextContent();
                                   }
                                   if (listaorient.item(norient).getNodeName().equals("ax27:genero")) {
                                       e[6] = listaorient.item(norient).getTextContent();
                                   }
                                   if (listaorient.item(norient).getNodeName().equals("ax27:listaNrsMecanograficos"))
      
