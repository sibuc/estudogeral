package pt.keep.dspace.sharing.handlers.exporters;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import pt.keep.dspace.sharing.ItemWrapper;

public class EndnoteExportHandler extends GenericExportHandler {
	private static Map<String, String> typesConversion;
	private static Map<String, String> getTypeConversions () {
		if (typesConversion == null) {
			typesConversion = new TreeMap<String, String>();
			typesConversion.put("article", "JOUR");
			typesConversion.put("bachelorThesis", "THES");
			typesConversion.put("masterThesis", "THES");
			typesConversion.put("doctoralThesis", "THES");
			typesConversion.put("book", "BOOK");
			typesConversion.put("bookPart", "CHAP");
			typesConversion.put("review", "GEN");
			typesConversion.put("conferenceObject", "CONF");
			typesConversion.put("lecture", "GEN");
			typesConversion.put("workingPaper", "SER");
			typesConversion.put("preprint", "UNPB");
			typesConversion.put("report", "RPRT");
			typesConversion.put("annotation", "GEN");
			typesConversion.put("contributionToPeriodical", "NEWS");
			typesConversion.put("patent", "PAT");
			typesConversion.put("other", "GEN");
		}
		return typesConversion;
	}
	@Override
	public String getApplicationName() {
		return "endnote";
	}

	@Override
	public void setReplacements(ItemWrapper wrapper) {
		String typePrefix = "info:eu-repo/semantics/";
		if(getTypeConversions().containsKey(wrapper.getType())){
			super.addReplacement("type", getTypeConversions().get(wrapper.getType()));
		}else if(getTypeConversions().containsKey(wrapper.getType().replaceFirst(typePrefix, ""))){
			super.addReplacement("type", getTypeConversions().get(wrapper.getType().replaceFirst(typePrefix, "")));
		}else{
			super.addReplacement("type", "GEN");
		}
		String content = "";
		content += getLineWtihRISSynthax("T1", wrapper.getTitle(), true);
		for(String author : wrapper.getAuthors()){
			content += getLineWtihRISSynthax("A1", author, true);
		}
		String abstrac = wrapper.getDescriptionAbstract();
		if(StringUtils.isNotBlank(abstrac)){
			content += getLineWtihRISSynthax("N2", abstrac, true);
		}
		content += getLineWtihRISSynthax("UR", wrapper.getURL() , true);

		String issueDateYear = wrapper.getIssueYear();
		if (StringUtils.isNotBlank(issueDateYear))
			content += getLineWtihRISSynthax("Y1", issueDateYear, true);

		String publisher = wrapper.getPublisher();
		if (StringUtils.isNotBlank(publisher))
			content += getLineWtihRISSynthax("PB", publisher, true);

		content += getLineWtihRISSynthax("ER", "", true);

		super.addReplacement("content", content);
	}
	public String getLineWtihRISSynthax(String tag, String value, boolean newLine){
		String result="";
		if(StringUtils.isNotBlank(value)){
			result=tag+"  - "+value.trim()+(newLine?"\n":"");
		}
		return result;
	}
}
