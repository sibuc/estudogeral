package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;
import pt.keep.dspace.sharing.htmlbuilders.MendeleyHTMLBuilder;

public class MendeleySharing extends GenericSharing {

	@Override
	public GenericHTMLBuilder getHTMLBuilder() {
		return new MendeleyHTMLBuilder("mendeley", super.getItemWrapper());
	}

	@Override
	public RequestHandler getHandler() {
		return null; // no need for Handler
	}

}
