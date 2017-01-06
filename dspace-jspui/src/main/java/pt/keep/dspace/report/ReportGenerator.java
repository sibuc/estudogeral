package pt.keep.dspace.report;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.core.Context;

import pt.keep.dspace.report.data.ExportFormat;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.generators.DSpaceItemReportGenerator;

public class ReportGenerator extends DSpaceServlet {
	private static final long serialVersionUID = 6721586726275317042L;
	private static Logger log = Logger.getLogger(ReportGenerator.class);
	public static int getMaxYear () {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	private static String[] _columns = null;
	
	public static String[] getOrderComumns () {
		if (_columns == null) _columns = new String[]{
				"issue",
				"title",
				"peerreview",
				"type"
			};
		return _columns;
	}
	
    protected void doDSPost(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doDSGet(context, request, response);
    }
    
    protected void doDSGet(Context context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		if (request.getParameter("bibtex") != null) {
    			BibTexGenerator generator = new BibTexGenerator(context, this, request, response);
    			generator.export(context, response);
    		} else {
				DSpaceItemReportGenerator generator = new DSpaceItemReportGenerator(context, this, request, response);
				ExportFormat format = ExportFormat.PDF;
				String param = null;
				if ((param = request.getParameter("format")) != null) {
					format = ExportFormat.valueOf(param);
				}
				generator.export(context, format, response);
    		}
		} catch (ReportGenerationException e) {
			log.error(e.getMessage(), e);
            JSPManager.showJSP(request, response, "/stats/error.jsp");
		}
    }
}
