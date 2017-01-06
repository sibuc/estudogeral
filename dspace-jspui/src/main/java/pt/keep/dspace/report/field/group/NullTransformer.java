package pt.keep.dspace.report.field.group;

import pt.keep.dspace.report.exceptions.ReportGenerationException;

public class NullTransformer extends AbstractGroupTransformer {
	
	public NullTransformer() {}
	
	@Override
	public String transform(String data) throws ReportGenerationException {
		return data;
	}

	@Override
	public void init() throws ReportGenerationException {}

}
