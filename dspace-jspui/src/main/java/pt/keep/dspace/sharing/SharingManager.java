package pt.keep.dspace.sharing;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.sharing.app.GenericSharing;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;

public class SharingManager {
	private static Logger log = Logger.getLogger(SharingManager.class);
	private static final String SIMPLE = "simple";
	private static final String ADVANCED = "advanced";

	private ItemWrapper _item;
	private List<GenericSharing> shareList = null;
	
	public SharingManager (ItemWrapper item) {
		_item = item;
		this.getShareList(item);
	}

	private List<GenericSharing> getShareList(ItemWrapper itemw) {
		if (shareList == null) {
			shareList = new ArrayList<GenericSharing>();
			String list = SharingConfigurationManager.getProperty("app.list",
					"");
			for (String item : list.split(",")) {
				item = item.trim();
				if (!item.equals("")) {
					try {
						GenericSharing instance = (GenericSharing) Class.forName(
								item).newInstance();
						instance.setItemWrapper(itemw);
						shareList.add(instance);
					} catch (InstantiationException e) {
						log.error("Cannot load share class", e);
					} catch (IllegalAccessException e) {
						log.error("Cannot load share class", e);
					} catch (ClassNotFoundException e) {
						log.error("Cannot load share class", e);
					}
				}
			}
		}
		return shareList;
	}
	
	public String getItemsAtLeft (HttpServletRequest request) {
		return getItems(request, "left");
	}
	public String getItemsAtRight (HttpServletRequest request) {
		return getItems(request, "right");
	}
	
	public String getItems (HttpServletRequest request, String side) {
		StringBuilder builder = new StringBuilder();
		String property = ConfigurationManager.getProperty("sharing."+side);
		if (property != null) {
			String[] parts = property.split(",");
			for (String id : parts) {
				id = id.toLowerCase().trim();
				String p = ConfigurationManager.getProperty("sharing."+id+".type");
				if (p != null) {
					log.debug("[KEEP] Type of "+id+": "+p);
					if (p.trim().toLowerCase().equals(SIMPLE)) {
						builder.append((new SimpleBuilder(request, _item, id)).build());
					} else if (p.trim().toLowerCase().equals(ADVANCED)) {
						String classHandler = ConfigurationManager.getProperty("sharing."+id+".class");
						if (classHandler != null) {
							Class<?> c;
							try {
								c = Class.forName(classHandler);
								Object obj = c.newInstance();
								if (!(obj instanceof GenericSharing))  
									throw new InstantiationException(obj.getClass().getName()+" does not extends pt.keep.dspace.sharing.app.GenericSharing");
								GenericSharing req = (GenericSharing) obj;
								req.setItemWrapper(_item);
								builder.append(req.getHTMLBuilder().getHTML(request));
							} catch (ClassNotFoundException e) {
								log.error(e.getMessage(), e);
							} catch (InstantiationException e) {
								log.error(e.getMessage(), e);
							} catch (IllegalAccessException e) {
								log.error(e.getMessage(), e);
							}
						} else log.error("sharing."+id+".class property unknown");
					}
				} else log.error("Type of sharing item "+id+" unknown");
			}
		}
		return builder.toString();
	}
}
