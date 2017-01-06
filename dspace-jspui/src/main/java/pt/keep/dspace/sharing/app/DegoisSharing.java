package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;
import pt.keep.dspace.sharing.htmlbuilders.LinkImageHTMLBuilder;
import pt.keep.dspace.sharing.steps.DeGoisWebpage;

public class DegoisSharing extends GenericSharing {

	@Override
	public GenericHTMLBuilder getHTMLBuilder() {
		return new LinkImageHTMLBuilder("degois", super.getItemWrapper());
	}

	@Override
	public RequestHandler getHandler() {
		return new DeGoisWebpage();
	}
	
}
