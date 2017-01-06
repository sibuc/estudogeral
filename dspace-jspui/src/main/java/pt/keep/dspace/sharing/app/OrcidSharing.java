package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;
import pt.keep.dspace.sharing.htmlbuilders.LinkImageOrcidHTMLBuilder;
import pt.keep.dspace.sharing.steps.OrcidWebpage;

public class OrcidSharing extends GenericSharing {

	@Override
	public GenericHTMLBuilder getHTMLBuilder() {
		return new LinkImageOrcidHTMLBuilder("orcid", super.getItemWrapper());
	}

	@Override
	public RequestHandler getHandler() {
		return new OrcidWebpage();
	}
	
}
