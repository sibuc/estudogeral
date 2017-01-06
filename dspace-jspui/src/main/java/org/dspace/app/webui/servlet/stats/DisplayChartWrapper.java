package org.dspace.app.webui.servlet.stats;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.chart.servlet.ChartDeleter;
import org.jfree.chart.servlet.DisplayChart;
import org.jfree.chart.servlet.ServletUtilities;

@SuppressWarnings("serial")
public class DisplayChartWrapper extends DisplayChart {
	private static Logger log = Logger.getLogger(DisplayChartWrapper.class);

	/**
	 * Default constructor.
	 */
	public DisplayChartWrapper() {
		super();
	}

	/**
	 * Init method.
	 * 
	 * @throws ServletException
	 *             never.
	 */
	public void init() throws ServletException {
		return;
	}

	/**
	 * Service method.
	 * 
	 * @param request
	 *            the request.
	 * @param response
	 *            the response.
	 * 
	 * @throws ServletException
	 *             ??.
	 * @throws IOException
	 *             ??.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		String filename = request.getParameter("filename");

		if (filename == null) {
			DisplayChartWrapper.log
					.debug("Parameter 'filename' must be supplied");
		} else {

			// Replace ".." with ""
			// This is to prevent access to the rest of the file system
			filename = ServletUtilities.searchReplace(filename, "..", "");

			// Check the file exists
			File file = new File(System.getProperty("java.io.tmpdir"), filename);
			if (!file.exists()) {
				DisplayChartWrapper.log.debug("File '" + file.getAbsolutePath()
						+ "' does not exist");
			} else {

				// Check that the graph being served was created by the current
				// user
				// or that it begins with "public"
				boolean isChartInUserList = false;
				ChartDeleter chartDeleter = (ChartDeleter) session
						.getAttribute("JFreeChart_Deleter");
				if (chartDeleter != null) {
					isChartInUserList = chartDeleter.isChartAvailable(filename);
				}

				boolean isChartPublic = false;
				if (filename.length() >= 6) {
					if (filename.substring(0, 6).equals("public")) {
						isChartPublic = true;
					}
				}

				boolean isOneTimeChart = false;
				if (filename.startsWith(ServletUtilities
						.getTempOneTimeFilePrefix())) {
					isOneTimeChart = true;
				}

				try {
					if (isChartInUserList || isChartPublic || isOneTimeChart) {
						// Serve it up
						ServletUtilities.sendTempFile(file, response);
						if (isOneTimeChart) {
							file.delete();
						}
					} else {
						DisplayChartWrapper.log.debug("Chart image not found");
					}
				} catch (IOException e) {
					DisplayChartWrapper.log.debug(e.getMessage(), e);
				}
			}
		}
	}
}
