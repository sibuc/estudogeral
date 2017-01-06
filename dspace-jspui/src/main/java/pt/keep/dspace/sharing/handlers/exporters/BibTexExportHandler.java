package pt.keep.dspace.sharing.handlers.exporters;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;

import pt.keep.dspace.sharing.ItemWrapper;

public class BibTexExportHandler extends GenericExportHandler {
	private boolean escapeBibtexContentForXml = false;
	
	private static Map<String, String> typesConversion;

	public static Map<String, String> getTypeConversions() {
		if (typesConversion == null) {
			typesConversion = new TreeMap<String, String>();
			typesConversion.put("article", "article");
			typesConversion.put("bachelorThesis", "misc");
			typesConversion.put("masterThesis", "masterthesis");
			typesConversion.put("doctoralThesis", "phdthesis");
			typesConversion.put("book", "book");
			typesConversion.put("bookPart", "incollection");
			typesConversion.put("review", "misc");
			typesConversion.put("conferenceObject", "inproceedings");
			typesConversion.put("lecture", "misc");
			typesConversion.put("workingPaper", "misc");
			typesConversion.put("preprint", "misc");
			typesConversion.put("report", "techreport");
			typesConversion.put("annotation", "misc");
			typesConversion.put("contributionToPeriodical", "misc");
			typesConversion.put("patent", "misc");
			typesConversion.put("other", "misc");
		}
		return typesConversion;
	}

	@Override
	public void setReplacements(ItemWrapper item) {
		String entryType = this.getTypeConverstion(item.getType());
		super.addReplacement("type", entryType);
		super.addReplacement("id", item.getCitationKey());
		if(escapeBibtexContentForXml){
			super.addReplacement("content", StringEscapeUtils.escapeXml(item.getBibtexContent(entryType)));
		}else{
			super.addReplacement("content", item.getBibtexContent(entryType));	
		}
	}

	@Override
	public String getApplicationName() {
		return "bibtex";
	}

	private String getTypeConverstion(String type) {
		String convertedType = BibTexExportHandler.getTypeConversions().get(
				type);
		if (convertedType != null) {
			return convertedType;
		} else {
			return "misc";
		}
	}

	public void setEscapeBibtexContentForXml(boolean escapeBibtexContentForXml) {
		this.escapeBibtexContentForXml = escapeBibtexContentForXml;
	}
}
