package pt.keep.dspace.sharing.htmlbuilders;

import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.I18nUtil;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;

public class LinkImageHTMLBuilder extends GenericHTMLBuilder {
	private static Logger logger = Logger.getLogger(LinkImageHTMLBuilder.class); 
	private String appName;
	private String image;
	private String url;
	private boolean atLeft;
	
	public LinkImageHTMLBuilder (String appName, ItemWrapper item) {
		try {
			this.appName = appName;
			this.image = "image/sharing/" + appName + ".png";
			String url = ConfigurationManager.getProperty("dspace.url");
			if (url == null) throw new InvalidAddress();
			if (!url.endsWith("/")) url += "/";
			url += "sharing";
			this.url = url + "?handler="+appName+"&id="+item.getID();
		} catch (Exception ex) {
			logger.error("Cannot get information about the Link Image to be displayed", ex);
		}
	}


	@Override
	public String getHTML(HttpServletRequest request) {
		String title = this.appName;
		String alt = this.appName;
		try {
			title = I18nUtil.getMessage("sharing."+this.appName+".title");
			alt = I18nUtil.getMessage("sharing."+this.appName+".alt");
		} catch (MissingResourceException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return "<a href=\""+this.url+"\" target=\"_blank\">" +
				"<img src="+request.getContextPath() + "/" + this.image+" title=\""+title
				+"\" alt=\""+alt+"\"></a><span class=\"sharingFloatLeft\">&nbsp;</span>";
	}
}