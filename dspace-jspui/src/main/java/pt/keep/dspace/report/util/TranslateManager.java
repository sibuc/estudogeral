package pt.keep.dspace.report.util;

import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import pt.keep.dspace.report.exceptions.ReportGenerationException;

public class TranslateManager {
	private static TranslateManager _instance = null;
	
	public static TranslateManager getInstance () throws ReportGenerationException {
		if (_instance == null) throw new ReportGenerationException("Translater instance doesn't exists");
		return _instance;
	}
	
	public static void init (PageContext p) {
		_instance = new TranslateManager(p);
	}
	
	
	private PageContext _page;
	
	private TranslateManager (PageContext page) {
		_page = page;
	}
	
	public String translate (String def) {
    	String res =  LocaleSupport.getLocalizedMessage(_page, def);
    	// log.info("Tranlation of "+def+" = "+res);
    	return res;
    }
    

	public String translate (String def, Object... args) {
    	String res = LocaleSupport.getLocalizedMessage(_page, def, args);
    	// log.info("Tranlation of "+def+" = "+res);
    	return res;
    }
	

	public Locale getLocale () {
		return org.dspace.core.I18nUtil.getDefaultLocale();
	}
}
