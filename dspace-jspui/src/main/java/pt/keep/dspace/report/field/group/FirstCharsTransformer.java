package pt.keep.dspace.report.field.group;


import pt.keep.dspace.report.exceptions.ReportGenerationException;

public class FirstCharsTransformer extends AbstractGroupTransformer {
	private String dcfield;
	private int length;
	
	@Override
	public String transform(String data) throws ReportGenerationException {
		if (data.length() >= 4) return data.substring(0, 4);
		else throw new ReportGenerationException(this.dcfield + ": "+data+" doesn't have "+this.length+" chars");
	}

	@Override
	public void init() throws ReportGenerationException {
		this.dcfield = super.getProperty("stats.reports.field."+this.getID()+".database.metadatafield", null);
		this.length = Integer.parseInt(super.getProperty("stats.reports.field."+this.getID()+".group.length", "4"));
	}

}
