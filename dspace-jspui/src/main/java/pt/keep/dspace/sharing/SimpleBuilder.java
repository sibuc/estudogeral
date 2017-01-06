package pt.keep.dspace.sharing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.MissingResourceException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.I18nUtil;

public class SimpleBuilder {
	private static Logger logger = Logger.getLogger(SimpleBuilder.class);
	private String _id;
	private ItemWrapper _item;
	private HttpServletRequest _request;
	
	public SimpleBuilder (HttpServletRequest request, ItemWrapper item, String id) {
		_id = id;
		_item = item;
		_request = request;
	}
	
	public String build () {
		String res = "";
		String url = ConfigurationManager.getProperty("sharing."+_id+".url");
		if (url != null) {
			try {
				int titleMaxLength = ConfigurationManager.getIntProperty("sharing."+_id+".params.title.maxlength", 0);
				String itemTitle = _item.getTitle();
				if (titleMaxLength > 0) {
					if (itemTitle != null && itemTitle.length() > titleMaxLength)
    						itemTitle = itemTitle.substring(0, titleMaxLength - 3)+ "...";
				}
				String title = _id;
				String alt = _id;
				try {
					title = I18nUtil.getMessage("sharing."+_id+".title");
					alt = I18nUtil.getMessage("sharing."+_id+".alt");
				} catch (MissingResourceException ex) {
					logger.error(ex.getMessage(), ex);
				}
				url = url.
					replace("[$title]", URLEncoder.encode(itemTitle,"UTF-8")).
					replace("[$url]", URLEncoder.encode(_item.getURL(),"UTF-8")).
					replace("[$description]", URLEncoder.encode(_item.getDescriptionAbstract(),"UTF-8")).
					replace("[$handle]", URLEncoder.encode(_item.getHandle(),"UTF-8")).
					replace("[$citkey]", URLEncoder.encode(_item.getCitationKey(),"UTF-8")).
					replace("[$issuedate]", URLEncoder.encode(_item.getIssueDate(),"UTF-8")).
					replace("[$publisher]", URLEncoder.encode(_item.getPublisher(),"UTF-8")).
					replace("[$doctype]", URLEncoder.encode(_item.getType(),"UTF-8")).
					replace("[$id]", URLEncoder.encode(_item.getID()+"","UTF-8")).
					replace("[$issueyear]", URLEncoder.encode(_item.getIssueYear(),"UTF-8"));
				res += "<a target=\"_blank\" href=\""+url+"\">";
				res += "<img alt=\""+alt+"\" title=\""+title+"\" src=\""+_request.getContextPath()+"/image/sharing/"+_id+".png\">";
				res += "</a>";
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
		} else logger.error("Unable to find property sharing."+_id+".url");
		return res;
	}
}
