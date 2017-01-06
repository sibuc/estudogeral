package pt.keep.dspace.sharing.app;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.handlers.RequestHandler;
import pt.keep.dspace.sharing.htmlbuilders.GenericHTMLBuilder;

public abstract class GenericSharing {
	private ItemWrapper item;
	
	public void setItemWrapper (ItemWrapper item) {
		this.item = item;
	}
	public ItemWrapper getItemWrapper () {
		return this.item;
	}
	
	public abstract GenericHTMLBuilder getHTMLBuilder();
	public abstract RequestHandler getHandler ();
}
