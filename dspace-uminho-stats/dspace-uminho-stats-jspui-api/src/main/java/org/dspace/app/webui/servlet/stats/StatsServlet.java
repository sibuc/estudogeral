/*
 * StatsServlet.java
 *
 * Version: $Revision:  $
 *
 * Date: $Date:  $
 *
 * Copyright (c) 2005, University of Minho.  All rights reserved.
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

package org.dspace.app.webui.servlet.stats;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.jsp.JspException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.core.Context;
//import org.dspace.core.LogManager;

/**
 * Servlet for generate a statistisc report
 *
 * @author  Arnaldo Dantas
 * @version $Revision: 1.0 $
 */
public class StatsServlet extends DSpaceServlet
{
    /** log4j category */
    private static Logger log = Logger.getLogger(StatsServlet.class);

    protected void doDSGet(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        doDSPost(context, request, response);
        //JSPManager.showJSP(request, response, "/stats/stats.jsp");
        //context.complete();
    }
    
    
    protected void doDSPost(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        //must be autorized
        //if (context.getCurrentUser() == null)
        //    throw new AuthorizeException("Authorization denied for view sdum statistics");
        
        // chek if is administrator
        //if (!AuthorizeManager.isAdmin(context))
        //{
        //    throw new AuthorizeException(
        //            "You must be an admin to view the Sdum Statistics");
        //}
        
        JSPManager.showJSP(request, response, "/stats/stats.jsp");
        context.complete();
   }
 
}
