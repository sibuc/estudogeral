package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.handlers.exporters.BibTexExportHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;
import pt.keep.dspace.sharing.htmlbuilders.LinkImageHTMLBuilder;

public class BibtexSharing extends GenericSharing {

	@Override
	public GenericHTMLBuilder getHTMLBuilder() {
		return new LinkImageHTMLBuilder("bibtex", super.getItemWrapper());
	}

	@Override
	public RequestHandler getHandler() {
		return new BibTexExportHandler();
	}

}
