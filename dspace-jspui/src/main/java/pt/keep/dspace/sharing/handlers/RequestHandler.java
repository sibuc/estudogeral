package pt.keep.dspace.sharing.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.sharing.ItemWrapper;

public abstract class RequestHandler {
	public static String getItemURL (ItemWrapper item) {
		String url = ConfigurationManager.getProperty("dspace.url");
		if (url == null) return "";
		if (!url.endsWith("/")) url += "/";
		url += "handle/"+item.getHandle();
		return url;
	}
	public static String getItemURL (String item) {
		String url = ConfigurationManager.getProperty("dspace.url");
		if (url == null) return "";
		if (!url.endsWith("/")) url += "/";
		url += "handle/"+item;
		return url;
	}
	
	public abstract void doProcessing (HttpServletRequest request,
            HttpServletResponse response, ItemWrapper item);
}
