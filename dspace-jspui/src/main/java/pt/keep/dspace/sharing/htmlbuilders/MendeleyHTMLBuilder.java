package pt.keep.dspace.sharing.htmlbuilders;

import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.I18nUtil;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;

public class MendeleyHTMLBuilder extends GenericHTMLBuilder {
	private static Logger logger = Logger.getLogger(MendeleyHTMLBuilder.class); 
	private String appName;
	private String image;
	private String url;
	private String script;
	
	public MendeleyHTMLBuilder (String appName, ItemWrapper item) {
		try {
			this.appName = appName;
			this.image = "image/sharing/" + appName + ".png";
			this.url = item.getURL();
			this.script = ConfigurationManager.getProperty("sharing."+appName + ".param.url");
		} catch (Exception ex) {
			logger.error("Cannot get information about the Link Image to be displayed", ex);
		}
	}


	@Override
	public String getHTML(HttpServletRequest request) {
		String title = this.appName;
		try {
			I18nUtil.getMessage("sharing."+this.appName+".title");
		} catch (MissingResourceException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return "<a onclick=\"javascript:document.getElementsByTagName('body')[0].appendChild(document.createElement('script')).setAttribute('src','"+this.script+"');\" href=\"#\">" +
				"<img src="+request.getContextPath() + "/" + this.image+" title=\""+title+"\" alt=\""+title+"\"></a><span class=\"sharingFloatLeft\">&nbsp;</span>";
	}
}