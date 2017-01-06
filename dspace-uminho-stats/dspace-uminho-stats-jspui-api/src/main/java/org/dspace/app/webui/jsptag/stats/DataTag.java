/*
 * StatsDataTag.java
 *
 * Version: $Revision: 1.15 $
 *
 * Date: $Date: 2005/07/29 15:56:07 $
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
package org.dspace.app.webui.jsptag.stats;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.core.Context;
import javax.servlet.jsp.JspWriter;

import org.dspace.core.ConfigurationManager;

import org.dspace.app.webui.util.stats.reportGenerator;
import org.dspace.app.webui.util.stats.StatsUtil;

import java.util.Iterator;
import java.io.PrintWriter;
/*
 * @author Arnaldo Dantas
 * @version $Revision: 1.0 $
 */
public class DataTag extends TagSupport
{
    /** log4j logger */
    private static Logger log = Logger.getLogger(DataTag.class);

    private static String cocoonChartUrl = ConfigurationManager
                        .getProperty("report.cocoon.chart.url");
    private static String dataServletPath = ConfigurationManager.getProperty("dspace.url.withport")
                        + "/cocoonreport";

    /** nodePageTab */
    private Node nodePageTab;

    private String renderTo="*jpeg";

    public DataTag()
    {
        super();
    }

    public int doStartTag() throws JspException
    {
    	log.debug("Start of Tag");
    	
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		HttpSession session = pageContext.getSession();
		
        try
        {

        	log.debug("Obtaining context");
        	
            Context context = UIUtil.obtainContext(request);
            //PrintWriter out = response.getWriter();
        	log.debug("Obtaining outputStream");
        	
            JspWriter jspout = pageContext.getOut();
            
            log.debug("Instantiation of PrintWriter");
            
            PrintWriter out = new PrintWriter(jspout);
            //Map inParams = request.getParameterMap();
            
            log.debug("Getting parameters from request");
            
            Map inParams = (Map)request.getAttribute("parameters");
            
            log.debug("Getting locale from session");
            
            Locale locale = StatsUtil.getSessionLocale(request);
            
            log.debug("Instatiation of report generator");
            
            reportGenerator rGen = new reportGenerator(context, locale, nodePageTab);

            log.debug("Setting content type");
            
            response.setContentType("text/html");
            
            log.debug("Generating report");
            
            rGen.createReport(out, session, inParams);

        }
        catch (java.sql.SQLException e)
        {
        	log.debug(e.getMessage(), e);
        	throw new JspException(e);
        }
        //catch (IOException ie)
        //{
        //    throw new JspException(ie);
        //}

        return EVAL_BODY_INCLUDE;            
    }
    public int doEndTag() throws JspException
    {
    	log.debug("End of Tag");
    	
        return EVAL_PAGE;
    }

    /**
     * Get the value of PageTab.
     * 
     * @return Value of PageTab.
     */
    public Node getPageTab()
    {
        return nodePageTab;
    }
    /**
     * Set the value of tab.
     * 
     * @param v
     *            Value to assign to tab.
     */
    public void setPageTab(Node v)
    {
        this.nodePageTab = v;
    }


    public void release()
    {
        nodePageTab = null;
    }

    static String getCocoonUrlForParams(Map inParams, String renderTo) {

        String url = cocoonChartUrl + renderTo + "?mills="
                        + System.currentTimeMillis();
        url += "&dataseturl=" + dataServletPath;
        int mapsize = inParams.size();
        Iterator keyValuePairs = inParams.entrySet().iterator();

        for (int m = 0; m < mapsize; m++) {
                Map.Entry entry = (Map.Entry) keyValuePairs.next();
                String key = (String) entry.getKey();
                String[] value = (String[]) entry.getValue();
                String values = "";
                for (int j = 0; j < value.length - 1; j++) {
                        values += value[j] + ",";
                }
                values += value[value.length - 1];
                url += "&" + key + "=" + values;
        }
        return url;
    }
}
