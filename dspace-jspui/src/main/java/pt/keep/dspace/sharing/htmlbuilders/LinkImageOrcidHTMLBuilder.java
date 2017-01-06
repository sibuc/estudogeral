package pt.keep.dspace.sharing.htmlbuilders;

import java.net.URLEncoder;
import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.I18nUtil;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;

public class LinkImageOrcidHTMLBuilder extends GenericHTMLBuilder {
	private static Logger logger = Logger
			.getLogger(LinkImageOrcidHTMLBuilder.class);
	private String appName;
	private String image;
	private String url;

	public LinkImageOrcidHTMLBuilder(String appName, ItemWrapper item) {
		try {
			String orcidBaseUrl = SharingConfigurationManager.getProperty(
					appName + ".param.baseurl", "https://sandbox.orcid.org");
			String orcidClientId = SharingConfigurationManager.getProperty(
					appName + ".param.clientid", "APP-XXXXXXXXXXXXXXXX");
			String orcidScope = "/orcid-works/create";

			this.appName = appName;
			this.image = "image/sharing/" + appName + ".png";

			String redirect_uri = URLEncoder.encode(
					LinkImageOrcidHTMLBuilder.getSharingUrl("orcid",
							item.getID()), "UTF-8");

			this.url = orcidBaseUrl + "/oauth/authorize?client_id="
					+ orcidClientId + "&response_type=code&scope=" + orcidScope
					+ "&redirect_uri=" + redirect_uri;
		} catch (Exception ex) {
			logger.error(
					"Cannot get information about the Link Image to be displayed",
					ex);
		}
	}

	public static String getSharingUrl(String appName, int itemId)
			throws InvalidAddress {
		String sharingUrl = ConfigurationManager.getProperty("dspace.url");
		if (sharingUrl == null)
			throw new InvalidAddress();
		if (!sharingUrl.endsWith("/")) {
			sharingUrl += "/";
		}
		sharingUrl += "sharing";
		sharingUrl += "?handler=" + appName + "&id=" + itemId;

		return sharingUrl;
	}

	@Override
	public String getHTML(HttpServletRequest request) {
		String title = this.appName;
		String alt = this.appName;
		try {
			title = I18nUtil.getMessage("sharing." + this.appName + ".title");
			alt = I18nUtil.getMessage("sharing." + this.appName + ".alt");
		} catch (MissingResourceException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return "<a href=\"" + this.url + "\" target=\"_blank\">" + "<img src="
				+ request.getContextPath() + "/" + this.image + " title=\""
				+ title + "\" alt=\"" + alt
				+ "\"></a><span class=\"sharingFloatLeft\">&nbsp;</span>";
	}
}