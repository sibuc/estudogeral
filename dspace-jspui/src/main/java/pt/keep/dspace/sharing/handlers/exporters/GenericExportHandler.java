package pt.keep.dspace.sharing.handlers.exporters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;
import pt.keep.dspace.sharing.handlers.RequestHandler;


public abstract class GenericExportHandler extends RequestHandler {
    /*public enum ExportType {
    	Undefined,
    	File
	}*/

	private static Logger log = Logger.getLogger(GenericExportHandler.class);
	public final static String TEMPLATE_DIR = "template.dir";
	
	private String template;
	// private ExportType exportType;
	private String mimeType;
	private String extension;
	private Map<String, String> replacements;
	
	public GenericExportHandler() {
		String templateFilePath = SharingConfigurationManager.getProperty(GenericExportHandler.TEMPLATE_DIR, "");
		String name = this.getApplicationName() + ".tpl";
		// this.exportType = ExportType.Undefined;
		if (templateFilePath.endsWith("/")) this.readTemplate(templateFilePath + name);
		else this.readTemplate(templateFilePath + "/" + name);
		this.replacements = new TreeMap<String, String>();
	}
	
	
	private void readTemplate (String filename) {
		this.template = "";
		Matcher match;
		Pattern comments = Pattern.compile("^#.*$");
		Pattern mimetype = Pattern.compile("^.*mimeType:(.*)$");
		Pattern extension = Pattern.compile("^.*extension:(.*)$");
		
		BufferedReader br = null;
		try {
			FileInputStream stream = new FileInputStream(filename);
			br = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = br.readLine()) != null) {
				log.debug("[KEEP LINE]: "+line);
				match = mimetype.matcher(line);
				if (match.find()) {
					if (match.groupCount() > 0)
						this.mimeType = match.group(1).trim();
				}
				match = extension.matcher(line);
				if (match.find()) {
					if (match.groupCount() > 0)
						this.extension = match.group(1).trim();
				}
				match = comments.matcher(line);
				if (!match.matches()) {
					this.template += line + "\n";
				}
			}
		} catch (FileNotFoundException e) {
			log.error("Cannot read template file", e);
		} catch (IOException e) {
			log.error("Cannot read template file", e);
		}finally{
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		if (!StringUtils.isNotBlank(this.mimeType)) {
			log.error("Unable to read the mime type from template, assuming text/plain");
			this.mimeType = "text/plain";
		}
		if (!StringUtils.isNotBlank(this.extension)) {
			log.error("Unable to read the extension from template, assuming .txt");
			this.extension = "txt";
		}
	}
	
	public void doProcessing (HttpServletRequest request,
            HttpServletResponse response, ItemWrapper item) {
		this.setReplacements(item);
		for (String key : this.replacements.keySet()) {
			this.template = this.template.replaceAll(Pattern.quote("${"+key+"}"), this.replacements.get(key));
		}
		response.setContentType(this.getMimeType());
		response.setHeader("Content-Disposition", "attachment; filename=\"export-item-" + item.getID() +"."+ this.getExtension() +"\"");
		try {
			response.getWriter().append(this.template);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}
	
	public abstract String getApplicationName ();
	
	public abstract void setReplacements (ItemWrapper wrapper);
	
	public void addReplacement (String key, String replace) {
		this.replacements.put(key, replace);
	}
	
	public String getExtension () {
		return this.extension;
	}
	public String getMimeType(){
		return mimeType;
	}
	
	/*public ExportType getExportType () {
		return this.exportType;
	}*/
	
	public String getTemplate () {
		return this.template;
	}
	
	public Map<String, String> getReplacements(){
		return this.replacements;
	}
}
