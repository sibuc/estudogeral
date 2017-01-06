/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.dspace.app.webui.util.Authenticate;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.authenticate.AuthenticationManager;
import org.dspace.authenticate.AuthenticationMethod;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.core.I18nUtil;
import org.dspace.usage.UsageEvent;
import org.dspace.utils.DSpace;

import java.util.Locale;

/**
 * LDAP username and password authentication servlet.  Displays the
 * login form <code>/login/ldap.jsp</code> on a GET,
 * otherwise process the parameters as an ldap username and password.
 *
 * @author  John Finlay (Brigham Young University)
 * @version $Revision$
 */
public class LDAPServlet extends DSpaceServlet
{
    /** log4j logger */
    private static Logger log = Logger.getLogger(LDAPServlet.class);

    protected void doDSGet(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        // check if ldap is enables and forward to the correct login form        
        boolean ldap_enabled = ConfigurationManager.getBooleanProperty("authentication-ldap", "enable");
        boolean read_only_mode = ConfigurationManager.getBooleanProperty("authentication", "read-only-mode");
        
        if (read_only_mode) {
            JSPManager.showJSP(request, response, "/login/noldap.jsp");
        }
        else {
        if (ldap_enabled)
        {
            JSPManager.showJSP(request, response, "/login/ldap.jsp");
        }
        else
        {
            JSPManager.showJSP(request, response, "/login/password.jsp");
        }
       }
    }


    protected void doDSPost(Context context,
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        // Process the POSTed email and password
        String netid = request.getParameter("login_netid");
        String password = request.getParameter("login_password");
	 	String jsp = null;
                
    /**    if ((netid.isEmpty()) && (password.isEmpty())) {
               response.sendRedirect("/");
        }
        else {  **/
		// Locate the eperson
        int status = AuthenticationManager.authenticate(context, netid, password,
                        null, request);


        if (status == AuthenticationMethod.SUCCESS)
        {
            try {
                // the AuthenticationManager updates the last_active field of the
                // eperson that logged in. We need to commit the context, to store
                // the updated field in the database.
                context.commit();
            } catch (SQLException ex) {
                // We can log the SQLException, but we should not interrupt the 
                // users interaction here.
                log.error("Failed to write an updated last_active field of an "
                        + "EPerson into the databse.", ex);
            }

            // Logged in OK.
            Authenticate.loggedIn(context, request, context.getCurrentUser());

            // Set the Locale according to user preferences
            Locale epersonLocale = I18nUtil.getEPersonLocale(context.getCurrentUser());
            context.setCurrentLocale(epersonLocale);
            Config.set(request.getSession(), Config.FMT_LOCALE, epersonLocale);

            log.info(LogManager.getHeader(context, "login", "type=explicit"));
            
			new DSpace().getEventService().fireEvent(
					new UsageEvent(UsageEvent.Action.LOGIN, request, context,
							context.getCurrentUser()));

            // resume previous request
            Authenticate.resumeInterruptedRequest(request, response);

            return;
        }
        else if (status == AuthenticationMethod.CERT_REQUIRED)
		{
			jsp = "/error/require-certificate.jsp";
		}
        else
		{
			jsp = "/login/ldap-incorrect.jsp";
		}

		// If we reach here, supplied email/password was duff.
        log.info(LogManager.getHeader(context, "failed_login",
                "netid=" + netid + ", result=" + String.valueOf(status)));
        JSPManager.showJSP(request, response, jsp);
	}
   /**  } **/
}
