package pt.keep.dspace.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.report.data.ShowMethod;

public class ReportLinkManager {
    private static Logger log = Logger.getLogger(ReportLinkManager.class);
    
    private final static String BASEPATH = "/stats/report.jsp"; 
    
	private static ShowMethod getShowMethod ()  {
		String method = ConfigurationManager.getProperty("stats.reports.showmethod");
		if (method != null && method.equals("blank")) {
			return ShowMethod.Blank;
		} else if (method != null && method.equals("")) {
			return ShowMethod.Popup;
		}
		return ShowMethod.Self;
	}
	
	private static String getLink (String baseLink, String title) {
		if (getShowMethod() == ShowMethod.Popup) {
			return "javascript: window.open ('"+baseLink+"','"+title+"');";
		}
		return baseLink;
	}
	
	private static String getTarget () {
		switch (getShowMethod()) {
			case Self:
				return " target=\"_self\" ";
			case Blank:
				return " target=\"_blank\" ";
			default:
				return "";
		}
	}
	
	public static String generateLink (PageContext pageContext, HttpServletRequest request, String authorName) {
		StringBuffer buffer = new StringBuffer();
		String link;
		String title = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.author.alt", StringEscapeUtils.escapeHtml4(authorName));
		link = request.getContextPath() +BASEPATH+"?type=author&amp;id="+new String(Base64.encodeBase64(authorName.getBytes()));
		
		
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/report.png\" />");
		buffer.append("</a>");
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"&bibtex=yes\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/bib.png\" />");
		buffer.append("</a>");
		return buffer.toString();
	}
	
	public static String generateLink (PageContext pageContext, HttpServletRequest request, Community c) {
		StringBuffer buffer = new StringBuffer();
		String link;
		String title = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.community.alt", c.getMetadata("name"));
		link = request.getContextPath() +BASEPATH+"?type=community&amp;id="+ c.getID();
			
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/report.png\" />");
		buffer.append("</a>");
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"&bibtex=yes\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/bib.png\" />");
		buffer.append("</a>");
		
		return buffer.toString();
	}
	
	public static String generateLink (PageContext pageContext, HttpServletRequest request, Collection c) {
		StringBuffer buffer = new StringBuffer();
		String link;
		String title = LocaleSupport.getLocalizedMessage(pageContext, "stats.reporting.collection.alt", c.getMetadata("name"));
		link = request.getContextPath() +BASEPATH+"?type=collection&amp;id="+ c.getID();
			
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/report.png\" />");
		buffer.append("</a>");
		buffer.append("<a class=\"reportimage\" "+getTarget()+" alt=\""+title+"\" href=\""+getLink(link, title)+"&bibtex=yes\">");
		buffer.append("<img src=\""+request.getContextPath()+"/image/stats/bib.png\" />");
		buffer.append("</a>");
		
		return buffer.toString();
	}
}
