package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.handlers.exporters.EndnoteExportHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;
import pt.keep.dspace.sharing.htmlbuilders.LinkImageHTMLBuilder;

public class EndnoteSharing extends GenericSharing {

	@Override
	public GenericHTMLBuilder getHTMLBuilder() {
		return new LinkImageHTMLBuilder("endnote", super.getItemWrapper());
	}

	@Override
	public RequestHandler getHandler() {
		return new EndnoteExportHandler();
	}

}
