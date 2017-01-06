package pt.keep.dspace.report.exceptions;

public class ReportGenerationException extends Exception {
	private static final long serialVersionUID = 4432008145745807136L;

	public ReportGenerationException (Exception ex) {
		super(ex.getMessage(), ex);
	}
	public ReportGenerationException (String ex) {
		super(ex);
	}
}
