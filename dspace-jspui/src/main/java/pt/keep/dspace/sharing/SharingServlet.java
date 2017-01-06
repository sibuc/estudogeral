package pt.keep.dspace.sharing;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import pt.keep.dspace.sharing.app.GenericSharing;

public class SharingServlet  extends DSpaceServlet {
	private static final long serialVersionUID = -3872357157392100086L;
	private static Logger log = Logger.getLogger(SharingServlet.class);
	
    @Override
    protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, AuthorizeException {
        this.doDSPost(context, request, response);
    }

    @Override
    protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, AuthorizeException {
    	String itemID = request.getParameter("id");
    	String handler = request.getParameter("handler");
    	
    	try {
			String handl = ConfigurationManager.getProperty("sharing."+handler + ".class");
			if (handler == null) throw new ClassNotFoundException("Configuration parameter sharing."+handler + ".class not defined");
			Class<?> c = Class.forName(handl);
			Object obj = c.newInstance();
			if (!(obj instanceof GenericSharing))  throw new InstantiationException(obj.getClass().getName()+" does not extends pt.keep.dspace.sharing.app.GenericSharing");
			GenericSharing req = (GenericSharing) obj;
			
			
			int item_id = Integer.parseInt(itemID);
			ItemWrapper wrapper = new ItemWrapper(Item.find(context, item_id));
			req.setItemWrapper(wrapper);
			
			req.getHandler().doProcessing(request, response, wrapper);
		} catch (ClassNotFoundException e) {
			log.error("Unable to load handler: "+handler, e);
		} catch (InstantiationException e) {
			log.error("Unable to instantiate handler: "+handler, e);
		} catch (IllegalAccessException e) {
			log.error("Unable to load handler: "+handler, e);
		} catch (Exception e) {
			log.error("Unable to handle request", e);
		}
    }
}
