package pt.keep.dspace.report.generators;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.constant.ImageType;
import net.sf.dynamicreports.report.exception.DRException;

import org.apache.log4j.Logger;
import org.dspace.core.Context;

import pt.keep.dspace.report.data.ExportFormat;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.util.FakePageContext;
import pt.keep.dspace.report.util.TranslateManager;

public abstract class AbstractReportGenerator {
    private static Logger log = Logger.getLogger(AbstractReportGenerator.class);
	private PageContext _page;
	private JasperReportBuilder _report;
	
	public AbstractReportGenerator (Servlet servlet, HttpServletRequest request, HttpServletResponse response) throws ReportGenerationException {
		try {
			_page = new FakePageContext();
			_page.initialize(servlet, request, response, "", true, 20, true);
			TranslateManager.init(_page);
			_report = report();
		} catch (Exception e) {
			throw new ReportGenerationException(e);
		}
	}
	
	public abstract void addFooter (JasperReportBuilder report);
	
	public AbstractReportGenerator (PageContext page) {
		TranslateManager.init(page);
		_page = page;
	}
	
	
	protected String translate (String def) {
    	String res =  LocaleSupport.getLocalizedMessage(_page, def);
    	// log.info("Tranlation of "+def+" = "+res);
    	return res;
    }
    

    protected String translate (String def, Object... args) {
    	String res = LocaleSupport.getLocalizedMessage(_page, def, args);
    	// log.info("Tranlation of "+def+" = "+res);
    	return res;
    }
    
    private void close () {
    	if (_page != null) {
    		try {
    			_page.release();
    		} catch (Exception ex) {
    			log.debug(ex.getMessage(), ex);
    		}
    	}
    }
    
    protected abstract void build (Context ct ,JasperReportBuilder report) throws ReportGenerationException;
    
    public void export (Context ct, ExportFormat format, HttpServletResponse response) throws ReportGenerationException {
    	try {
    		this.build(ct, _report);
    		OutputStream output = response.getOutputStream();

    		response.setHeader( "Content-Disposition", "attachment; filename=\"report." + format.name().toLowerCase() + "\"" );
    		switch (format) {
				case PDF:
					response.setContentType("application/pdf");
					this.addFooter(_report);
					_report.toPdf(output);
					break;
				case XLS:
					response.setContentType("application/vnd.ms-excel");
					_report.setPageFooterStyle(null);
					_report.toXls(output);
					break;
				case XLSX:
					response.setContentType("application/vnd.ms-excel");
					_report.toXlsx(output);
					break;
				case CSV:
					response.setContentType("application/plain");
					_report.toCsv(output);
					break;
				/*case HTML:
					_report.toHtml(output);
					break;
				case XML:
					response.setContentType("text/xml");
					_report.toXml(output);
				case DOCX:
					response.setContentType("application/msword");
					_report.toDocx(output);
					break;
				case PNG:
					response.setContentType("image/png");
					_report.toImage(output, ImageType.PNG);
					break;
				case JPG:
					response.setContentType("image/jpeg");
					_report.toImage(output, ImageType.JPG);
					break;
				case GIF:
					response.setContentType("image/gif");
					_report.toImage(output, ImageType.GIF);
					break;
				case ODT:
					response.setContentType("application/vnd.oasis.opendocument.text");
					_report.toOdt(output);
					break;
				case RTF:
					response.setContentType("application/rtf");
					_report.toRtf(output);
					break;
				case PPTX:
					response.setContentType("application/vnd.ms-powerpoint");
					_report.toPptx(output);
					break;
				case TEXT:
					response.setContentType("application/plain");
					_report.toText(output);
					break;*/
			}
    		
		
	    	output.flush();
	    	output.close();
	    	
	    	this.close();
    	} catch (IOException e) {
    		throw new ReportGenerationException(e);
    	} catch (DRException e) {
    		throw new ReportGenerationException(e);
		}
    }
}
