package pt.keep.dspace.sharing.htmlbuilders;

import javax.servlet.http.HttpServletRequest;

public abstract class GenericHTMLBuilder {
	
	public abstract String getHTML (HttpServletRequest request);
}
