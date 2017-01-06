package pt.keep.dspace.sharing.steps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;

import pt.keep.dspace.sharing.ItemWrapper;
import pt.keep.dspace.sharing.configuration.SharingConfigurationManager;
import pt.keep.dspace.sharing.handlers.exporters.BibTexExportHandler;
import pt.keep.dspace.sharing.handlers.exporters.GenericExportHandler;
import pt.keep.dspace.sharing.htmlbuilders.LinkImageOrcidHTMLBuilder;

import com.google.gson.Gson;

public class OrcidWebpage extends GenericExportHandler {
	public static final int NO_CODE = -1;
	public static final int BLANK_OR_INVALID_AUTH_CODE = 1;
	public static final int UNABLE_TO_CREATE_WORK = 2;

	private static final String ORCID_API_VERSION = "1.2_rc6"; // 1.1

	private static Logger log = Logger.getLogger(OrcidWebpage.class);
	private HttpServletRequest request;
	private HttpServletResponse response;

	private static Map<String, String> typesConversion;
	private static Map<String, String> identifiersConversion;

	// FIXME revise DSpace > Orcid type conversion
	// (http://support.orcid.org/knowledgebase/articles/118795)
	private static Map<String, String> getTypeConversions() {
		if (typesConversion == null) {
			typesConversion = new TreeMap<String, String>();
			typesConversion.put("article", "journal-article");
			typesConversion.put("bachelorThesis", "dissertation");
			typesConversion.put("masterThesis", "dissertation");
			typesConversion.put("doctoralThesis", "dissertation");
			typesConversion.put("book", "book");
			typesConversion.put("bookPart", "book-chapter");
			typesConversion.put("review", "other");
			typesConversion.put("conferenceObject", "other");
			typesConversion.put("lecture", "lecture-speech");
			typesConversion.put("workingPaper", "working-paper");
			typesConversion.put("preprint", "other");
			typesConversion.put("report", "report");
			typesConversion.put("annotation", "other");
			typesConversion.put("contributionToPeriodical", "journal-issue");
			typesConversion.put("patent", "patent");
			typesConversion.put("other", "other");
		}
		return typesConversion;
	}

	private static Map<String, String> getIdentifiersConversions() {
		if (identifiersConversion == null) {
			identifiersConversion = new TreeMap<String, String>();
			identifiersConversion.put("doi", "doi");
			identifiersConversion.put("uri", "uri");
			identifiersConversion.put("isbn", "isbn");
			identifiersConversion.put("issn", "issn");
		}
		return identifiersConversion;
	}

	@Override
	public void doProcessing(HttpServletRequest request,
			HttpServletResponse response, ItemWrapper item) {
		this.request = request;
		this.response = response;

		this.process(item);
	}

	private boolean process(ItemWrapper wrapper) {
		String orcidAuthorizationCode = request.getParameter("code");

		if (StringUtils.isNotBlank(orcidAuthorizationCode)) {
			log.info("Processing request for Orcid (item=" + wrapper.getID()
					+ ")(orcidAuthorizationCode=" + orcidAuthorizationCode
					+ ")");
			try {
				OrcidAccessTokenResponse orcidAccessTokenResponse = exchangeAuthorizationCodeForAnAccessToken(
						orcidAuthorizationCode, wrapper.getID());

				if (orcidAccessTokenResponse != null) {
					if (createWork(orcidAccessTokenResponse.access_token,
							orcidAccessTokenResponse.orcid, wrapper)) {
						this.ShowSubPage("Success");
					} else {
						log.error("Cannot process request for Orcid  (item="
								+ wrapper.getID()
								+ ") because an error occurred while sending the item to Orcid!");
						this.ShowSubPageWithCode("Error", UNABLE_TO_CREATE_WORK);
					}
					return false;
				} else {
					log.error("Cannot process request for Orcid  (item="
							+ wrapper.getID()
							+ ") because no access token can be generated from the authorization code!");
				}
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			} catch (InvalidAddress e) {
				log.error(e);
			}
			this.ShowSubPage("Error");
			return false;
		} else {
			log.error("Cannot process request for Orcid  (item="
					+ wrapper.getID()
					+ ") because authorization code is null/blank!");
			this.ShowSubPageWithCode("Error", BLANK_OR_INVALID_AUTH_CODE);
			return false;
		}
	}

	private boolean createWork(String orcidAccessToken, String userOrcid,
			ItemWrapper wrapper) throws UnsupportedEncodingException {
		boolean result = true;

		String orcidApiBaseUrl = SharingConfigurationManager.getProperty(
				getApplicationName() + ".param.apibaseurl",
				"https://api.sandbox.orcid.org");
		String newWorkTemplate = super.getTemplate();

		this.setReplacements(wrapper);
		for (Entry<String, String> entry : super.getReplacements().entrySet()) {
			log.debug("Replacing ${" + entry.getKey() + "} by ##"
					+ entry.getValue() + "##");
			newWorkTemplate = newWorkTemplate.replaceAll(
					Pattern.quote("${" + entry.getKey() + "}"),
					entry.getValue());
		}

		log.debug("WORK: " + newWorkTemplate);

		PostMethod post = new PostMethod(orcidApiBaseUrl + "/v"
				+ ORCID_API_VERSION + "/" + userOrcid + "/orcid-works");
		post.setRequestHeader("Authorization", "Bearer " + orcidAccessToken);
		RequestEntity entity = new StringRequestEntity(newWorkTemplate,
				"application/orcid+xml; charset=UTF-8", null);
		post.setRequestEntity(entity);

		try {
			HttpClient httpclient = new HttpClient();
			int httpResponseCode = httpclient.executeMethod(post);

			if (httpResponseCode != 201) {
				result = false;
				log.error("Error sending work to Orcid! httpResponseCode="
						+ httpResponseCode + "; "
						+ post.getResponseBodyAsString());
			}

		} catch (IOException ex) {
			log.error("Error sending work to Orcid!", ex);
		} finally {
			// Release current connection to the connection pool
			post.releaseConnection();
		}

		return result;
	}

	private OrcidAccessTokenResponse exchangeAuthorizationCodeForAnAccessToken(
			String orcidAuthorizationCode, int itemId)
			throws UnsupportedEncodingException, InvalidAddress {
		OrcidAccessTokenResponse jsonResponse = null;

		String orcidApiBaseUrl = SharingConfigurationManager.getProperty(
				getApplicationName() + ".param.apibaseurl",
				"https://api.sandbox.orcid.org");
		String orcidClientId = SharingConfigurationManager.getProperty(
				getApplicationName() + ".param.clientid",
				"APP-XXXXXXXXXXXXXXXX");
		String orcidClientSecret = SharingConfigurationManager.getProperty(
				getApplicationName() + ".param.clientsecret",
				"XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX");
		String redirect_uri = LinkImageOrcidHTMLBuilder.getSharingUrl("orcid",
				itemId);
		InputStreamReader inputStream = null;

		log.debug("client_id=" + orcidClientId + "; client_secret="
				+ orcidClientSecret + "; code=" + orcidAuthorizationCode
				+ "; redirect_uri=" + redirect_uri);

		PostMethod post = new PostMethod(orcidApiBaseUrl + "/oauth/token");
		post.setRequestHeader("Accept", "application/json");
		NameValuePair[] data = { new NameValuePair("client_id", orcidClientId),
				new NameValuePair("client_secret", orcidClientSecret),
				new NameValuePair("grant_type", "authorization_code"),
				new NameValuePair("code", orcidAuthorizationCode),
				new NameValuePair("redirect_uri", redirect_uri) };
		post.setRequestBody(data);

		try {
			HttpClient httpclient = new HttpClient();
			int httpResponseCode = httpclient.executeMethod(post);

			if (httpResponseCode == 200) {
				Gson gson = new Gson();
				inputStream = new InputStreamReader(
						post.getResponseBodyAsStream());
				jsonResponse = gson.fromJson(inputStream,
						OrcidAccessTokenResponse.class);
			} else {
				log.error("Error exchanging auth. code for an access token! httpResponseCode="
						+ httpResponseCode
						+ "; "
						+ post.getResponseBodyAsString());
			}

		} catch (IOException ex) {
			log.error("Error exchanging auth. code for an access token!", ex);
		} finally {
			// Release current connection to the connection pool
			post.releaseConnection();
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

		return jsonResponse;
	}

	private void dispatch(String page, int code) {
		try {
			if (code == NO_CODE) {
				request.getRequestDispatcher("/sharing/" + page + ".jsp")
						.forward(request, response);
			} else {
				request.getRequestDispatcher(
						"/sharing/" + page + ".jsp?code=" + code).forward(
						request, response);
			}
		} catch (ServletException ex) {
			log.debug(ex.getCause(), ex);
		} catch (IOException ex) {
			log.debug(ex.getCause(), ex);
		}
	}

	public void ShowSubPageWithCode(String page, int code) {
		this.dispatch("orcid" + page, code);
	}

	public void ShowSubPage(String page) {
		this.dispatch("orcid" + page, NO_CODE);
	}

	private class OrcidAccessTokenResponse {
		String access_token;
		String orcid;
		// String name;
	}

	@Override
	public String getApplicationName() {
		return "orcid";
	}

	@Override
	public void setReplacements(ItemWrapper wrapper) {
		// http://support.orcid.org/knowledgebase/articles/135422-xml-for-orcid-works
		String orcidDefaultVisibility = SharingConfigurationManager
				.getProperty(getApplicationName() + ".param.defaultvisibility",
						"private");
		String type = getTypeConversions().get(wrapper.getType());
		if (type == null) {
			type = "other";
		}

		super.addReplacement("api_version", ORCID_API_VERSION);
		super.addReplacement("title",
				StringEscapeUtils.escapeXml(wrapper.getTitle()));
		super.addReplacement("type", type);
		super.addReplacement("visibility", orcidDefaultVisibility);
		super.addReplacement("abstract",
				StringEscapeUtils.escapeXml(wrapper.getDescriptionAbstract()));

		// Citation
		setCitation(wrapper);

		// Publication date
		setPublicationDate(wrapper);

		// External Identifiers & Url
		setExternalIdentifiers(wrapper);

		// Contributors
		setContributors(wrapper);
	}

	private void setCitation(ItemWrapper wrapper) {
		BibTexExportHandler bibTexExportHandler = new BibTexExportHandler();
		bibTexExportHandler.setEscapeBibtexContentForXml(true);
		bibTexExportHandler.setReplacements(wrapper);
		String bibtex = bibTexExportHandler.getTemplate();
		for (Entry<String, String> entry : bibTexExportHandler
				.getReplacements().entrySet()) {
			bibtex = bibtex.replaceAll(
					Pattern.quote("${" + entry.getKey() + "}"),
					entry.getValue());
		}
		super.addReplacement("citation", bibtex);
	}

	private void setContributors(ItemWrapper wrapper) {
		final String authorTemplate = "<contributor><credit-name>%s</credit-name><contributor-attributes><contributor-sequence>%s</contributor-sequence><contributor-role>%s</contributor-role></contributor-attributes></contributor>";
		StringBuilder authors = new StringBuilder("");
		int authorCount = 0;
		String sequence = "first", role = "author";
		for (String author : wrapper.getAuthors()) {
			if (authorCount != 0) {
				sequence = "additional";
			}
			authors.append(String
					.format(authorTemplate, author, sequence, role));
			authorCount++;
		}
		final String advisorTemplate = "<contributor><credit-name>%s</credit-name></contributor>";
		for (String advisor : wrapper.getAdvisors()) {
			authors.append(String.format(advisorTemplate, advisor));
		}
		super.addReplacement("contributors", authors.toString());
	}

	private void setExternalIdentifiers(ItemWrapper wrapper) {
		super.addReplacement("handle", wrapper.getCompleteHandle());
		String identifierTemplate = "<work-external-identifier><work-external-identifier-type>%s</work-external-identifier-type><work-external-identifier-id>%s</work-external-identifier-id></work-external-identifier>";
		StringBuilder identifiers = new StringBuilder("");
		for (Entry<String, String> identifier : wrapper
				.getIdentifiersWithQualifier().entrySet()) {
			log.debug("qualifier=" + identifier.getValue() + ";value="
					+ identifier.getKey());
			if (identifier.getValue() != null
					&& getIdentifiersConversions().get(identifier.getValue()) != null) {
				identifiers.append(String.format(identifierTemplate,
						getIdentifiersConversions().get(identifier.getValue()),
						StringEscapeUtils.escapeXml(identifier.getKey())));
			}
		}
		super.addReplacement("identifiers", identifiers.toString());
	}

	private void setPublicationDate(ItemWrapper wrapper) {
		Pattern issueDatePattern = Pattern
				.compile("([0-9]{4})(?:-([0-9]{2})(?:-([0-9]{2}))?)?");
		Matcher issueDateMatcher = issueDatePattern.matcher(wrapper
				.getIssueDate());
		if (issueDateMatcher.find() && issueDateMatcher.groupCount() == 3) {
			String publicationDate = "<year>" + issueDateMatcher.group(1)
					+ "</year>";
			publicationDate += issueDateMatcher.group(2) == null ? ""
					: "<month>" + issueDateMatcher.group(2) + "</month>";
			publicationDate += issueDateMatcher.group(3) == null ? "" : "<day>"
					+ issueDateMatcher.group(3) + "</day>";

			super.addReplacement("publication_date", publicationDate);
		}
	}
}
