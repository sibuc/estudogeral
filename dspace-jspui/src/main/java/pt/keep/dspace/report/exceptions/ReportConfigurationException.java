package pt.keep.dspace.report.exceptions;

public class ReportConfigurationException extends Exception {
	private static final long serialVersionUID = 4432008145745807136L;

	public ReportConfigurationException (Exception ex) {
		super(ex.getMessage(), ex);
	}
	public ReportConfigurationException (String ex) {
		super(ex);
	}
}
