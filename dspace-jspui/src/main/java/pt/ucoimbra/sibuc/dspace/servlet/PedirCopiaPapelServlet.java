/*
 * PedirCopiaPapelServlet.java
 *
 * Version: $Revision: 1.0 $
 *
 * Date: $Date: 2013-09-11 $
 *
 * Copyright (c) 2013, SIBUC, University de Coimbra.  All rights reserved.
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
import java.sql.SQLException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import org.dspace.app.webui.servlet.DSpaceServlet;
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
import org.dspace.content.Item;
import org.dspace.content.Collection;
import org.dspace.content.Metadatum;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.storage.bitstore.BitstreamStorageManager;


/**
 * Servlet for sending paper copy requests to academic department
 *
 * @author  Ana Luisa Silva
 * @version $Revision: 1.0 $
 */
public class PedirCopiaPapelServlet extends DSpaceServlet
{
    /** log4j category */
    private static Logger log = Logger.getLogger(PedirCopiaPapelServlet.class);

        /** The information get by form step */
    public static final int ENTER_FORM_PAGE = 1;

    /* resume leter for request user*/
    public static final int RESUME_REQUEST = 2;
    
    protected void doDSGet(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
       // First get the step
        int step = UIUtil.getIntParameter(request, "step");
        log.info("Step - " + step);
        switch (step)
        {
        case ENTER_FORM_PAGE:
          log.info("Enter form page");
          processForm(context, request, response);
          break;
        case RESUME_REQUEST:
          log.info("Resume request");
          resumeRequest(context,request,response);
          break;
        default:
          log.info("Process Form");
          processForm(context, request, response);
          break;
        }

      //  context.complete();
    }

    protected void doDSPost(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        // Treat as a GET
        doDSGet(context, request, response);
    }
 
    private void processForm (Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {        
        log.info("Process-form");
        // handle
        String handle = request.getParameter("item-handle");
            
        // Title
        String title = null;
        Item item = null;
        Collection collection = null;
        if (handle != null && !handle.equals(""))
        {
            item = (Item) HandleManager.resolveToObject(context, handle);
            if (item != null)
            {   
                Metadatum[] titleDC = item.getDC("title", null, Item.ANY);
                if (titleDC != null || titleDC.length > 0) 
                    title = titleDC[0].value;
                collection = item.getOwningCollection();
                log.info(LogManager.getHeader(context,
                    "sent_email_pedido_copia itemhandle=",
                    item.getHandle()));
                log.info(LogManager.getHeader(context,
                    "sent_email_pedido_copia collection=",
                    collection.getName()));
            }
        }
        if (title == null)
                title="";
        
        String collection_name = null;

        if (collection!= null)
        {
          collection_name = collection.getName();
        }
        if (collection_name == null)
        {
            collection_name="";
            log.info(LogManager.getHeader(context,
                    "sent_email_pedido_copia collection= ",
                    "null"));
        }
        else
            log.info(LogManager.getHeader(context,
                    "sent_email_pedido_copia collection = ",
                    collection_name));
        // User email from context

        EPerson currentUser = context.getCurrentUser();
        String userName = null;
        String authEmail = null;
        String membrojuri = null;
        String emailmembrojuri = null;
        if (currentUser != null)
        {
            authEmail = currentUser.getEmail();
            userName = currentUser.getFullName();
        }
        
        
        if (request.getParameter("submit") != null)
        {
            log.info("submit - processForm");
            membrojuri = request.getParameter("membrojuri");
            emailmembrojuri = request.getParameter("emailmembrojuri");
            title = request.getParameter("title");
            handle = request.getParameter("item-handle");
            collection_name = request.getParameter("collection_name");
            if (membrojuri == null || membrojuri.equals(""))
                membrojuri = "";
           if (emailmembrojuri == null || emailmembrojuri.equals(""))
                emailmembrojuri = "";
                        
            // Check all data is there
            if (membrojuri == null || membrojuri.equals("") || emailmembrojuri == null || emailmembrojuri.equals(""))
            {
                request.setAttribute("item-handle",handle);
                request.setAttribute("membrojuri", membrojuri);
                request.setAttribute("emailmembrojuri", emailmembrojuri);
                request.setAttribute("authEmail", authEmail);
                request.setAttribute("userName", userName);
                request.setAttribute("title", title); 
                request.setAttribute("collection_name", collection_name);
                JSPManager.showJSP(request, response, "/academicos/pedirCopia-form.jsp");
                return;
            }

            // All data is there, send the email
            // get submiter email
            //EPerson submiter = item.getSubmitter();
           log.info("all data is there; reencaminha para pedirCopia-form");
            //get email 
            try
            {

                            // Get the Locale
            Locale supportedLocale = I18nUtil.getEPersonLocale(currentUser);
            Email email = Email.getEmail(I18nUtil.getEmailFilename(supportedLocale, "pedir_copia_papel"));
            String academicos_email = ConfigurationManager.getProperty("mail.academicos");

            // email.addRecipient("als@uc.pt");
            log.info("Email academicos: " + academicos_email);
            email.addRecipient(academicos_email);
            email.setCharset("UTF-8");
            email.addArgument(new Date());
            email.addArgument(collection_name);
            email.addArgument(title);
            email.addArgument(handle);
            email.addArgument(membrojuri);
            email.addArgument(emailmembrojuri);
            email.addArgument(userName);
            email.send();

          
                log.info(LogManager.getHeader(context,
                    "sent_email_pedido_copia",
                    ",destinatario=" + emailmembrojuri
                        + ",colecao="+collection_name
                        + ",titulo="+title));
                request.setAttribute("item-handle",handle);
                JSPManager.showJSP(request, response,
                    "/academicos/pedido_concluido.jsp");
                return;
            }
            catch (MessagingException me)
            {
                log.warn(LogManager.getHeader(context,
                    "error_mailing_pedirCopia",
                    ""), me);
               JSPManager.showInternalError(request, response);
               return;
            }
        }
        else
        {
            // Display sugget form
            log.info(LogManager.getHeader(context,
                "show_pedirCopia_form",
                "=inicio"));
                request.setAttribute("item-handle",handle);
                request.setAttribute("membrojuri", membrojuri);
                request.setAttribute("emailmembrojuri", emailmembrojuri);
                request.setAttribute("authEmail", authEmail);
                request.setAttribute("userName", userName);
                request.setAttribute("title", title);
                request.setAttribute("collection_name", collection_name);

            JSPManager.showJSP(request, response, "/academicos/pedirCopia-form.jsp");

            return;
        }
   }


    private void resumeRequest (Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        log.info("Resume request");
        String handle = request.getParameter("item-handle");

        String url1 = null;
        String prefix1 = "/handle/" + handle;

            url1 = request.getContextPath()+ prefix1;
        log.info("url1 - " + url1);
            request.getRequestDispatcher(url1).forward(request,response);
            
        return;
    }

}
