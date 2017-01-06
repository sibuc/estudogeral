package pt.keep.dspace.sharing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.dspace.content.DCValue;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;

import pt.keep.dspace.sharing.handlers.RequestHandler;

@SuppressWarnings("deprecation")
public class ItemWrapper {
	private static Logger log = Logger.getLogger(ItemWrapper.class);
	private static final int maxAuthorsOnCitationKey = 3;
	private static final int maxCharsPerAuthorOnCitationKey=3;
	private Item item;

	public ItemWrapper (Item item) {
		this.item = item;
	}

	private String getOneValue (String md, String element, String qualifier, String language, String defaul) {
		Metadatum[] values = this.item.getMetadata(md, element, qualifier, language);
		if (values.length > 0) {
			return values[0].value;
		}
		return defaul;
	}

	private List<String> getMultiValues (String md, String element, String qualifier, String language) {
		List<String> lst = new ArrayList<String>();
		Metadatum[] values = this.item.getMetadata(md, element, qualifier, language);
		for (Metadatum val : values) {
			lst.add(val.value);
		}
		return lst;
	}

	private Map<String,String> getMultiValuesWithQualifier (String md, String element, String qualifier, String language) {
		Map<String,String> result = new HashMap<String,String>();
		Metadatum[] values = this.item.getMetadata(md, element, qualifier, language);
		for (Metadatum val : values) {
			result.put(val.value, val.qualifier);
		}
		return result;
	}

	public String getTitle () {
		return this.getOneValue("dc", "title", Item.ANY, Item.ANY, "No title defined");
	}

	public String getPublisher () {
		return this.getOneValue("dc", "publisher", Item.ANY, Item.ANY, "No publisher defined");
	}

	public String getCompleteHandle () {
		String url = ConfigurationManager.getProperty("handle.canonical.prefix");
		return url + this.item.getHandle();
	}

	public String getHandle () {
		return this.item.getHandle();
	}

	public String getURL () {
		return RequestHandler.getItemURL(item.getHandle());
	}

	public int getID () {
		return this.item.getID();
	}

	public List<String> getAuthors () {
		return this.getMultiValues("dc", "contributor", "author", Item.ANY);
	}

	public List<String> getAdvisors () {
		return this.getMultiValues("dc", "contributor", "advisor", Item.ANY);
	}

	public Map<String, String> getIdentifiersWithQualifier() {
		return this.getMultiValuesWithQualifier("dc", "identifier", Item.ANY, Item.ANY);
	}

	public String getIssueDate () {
		return this.getOneValue("dc", "date", "issued", Item.ANY, "");
	}

	public String getIssueYear () {
		String date = this.getIssueDate();
		Pattern ptn = Pattern.compile("[0-9]{4}");
		Matcher match = ptn.matcher(date);
		if (match.find()) {
			return match.group();
		}
		return "";
	}

	public String getDescriptionAbstract () {
		return this.getOneValue("dc", "description", "abstract", Item.ANY, "");
	}

	public String getType () {
		String str = this.getOneValue("dc", "type", Item.ANY, Item.ANY, "");
		return str;
	}
	public String getCitationKey () {
		String citationKey="";
		List<String> creators = this.getAuthors();
		String issueDate = this.getIssueYear();
		try{
			switch (creators.size()) {
				case 0:
					break;
				case 1:
					if(issueDate != null && !issueDate.equals("")){
						citationKey = (creators.get(0).split(" ")[0].replaceAll(",", "").toLowerCase()) +issueDate;
					}
					break;
				default:
					for(int i=0; i<maxAuthorsOnCitationKey; i++){
						if(i <= (creators.size()-1)){
							citationKey+=creators.get(i).trim().substring(0, maxCharsPerAuthorOnCitationKey).replaceAll(",", "").toLowerCase();
						}
					}
					if( !citationKey.equals("") ){
						citationKey+=issueDate;
					}
					break;
			}
		}catch (Throwable e) {
			// nothing to do...return citationKey as it is
		}
		return citationKey;
	}


	public String getAuthorsInformation(){
		List<String> creators = this.getAuthors();
		String result = "";
		int i=0;
		for(String author : creators){
			if(i>0){
				result+=" and ";
			}
			result+=author;
			i++;
		}
		return result;
	}

	public String getBibtexContent (String entryType) {
		String result = "";
		if(entryType.equalsIgnoreCase("article")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("book")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("publisher", this.getPublisher(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("booklet")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("conference")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("publisher", this.getPublisher(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("inbook")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("publisher", this.getPublisher(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("incolletion")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("publisher", this.getPublisher(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("inproceedings")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("manual")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("masterthesis")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("misc")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("phdthesis")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("proceedings")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("techreport")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("incollection")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}else if(entryType.equalsIgnoreCase("unpublished")){
			result += this.getField("author", this.getAuthorsInformation(),true);
			result += this.getField("title", this.getTitle(),true);
			result += this.getField("year", this.getIssueYear(),false);
		}
		return result;
	}



	private String getField(String fieldName, String value, boolean newLine){
		if(StringUtils.isNotBlank(value)){
			return " "+fieldName+"	= \""+value.trim().replaceAll("[\"{}]", "{$0}")+"\""+(newLine?",\n":"");
		}
		return "";
	}

	public Item getItem () {
		return this.item;
	}
}
