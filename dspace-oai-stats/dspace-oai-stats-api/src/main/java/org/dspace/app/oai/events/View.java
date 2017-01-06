package org.dspace.app.oai.events;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.digest.DigestUtils;
import org.dspace.app.oai.DSpaceOAICatalog;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.TableRow;

import pt.keep.sceur.contextobjects.ContextObject;
import pt.keep.sceur.contextobjects.ContextObjects;
import pt.keep.sceur.contextobjects.DescriptorType;
import pt.keep.sceur.contextobjects.dcterms.Author;
import pt.keep.sceur.contextobjects.dcterms.Format;
import pt.keep.sceur.contextobjects.dcterms.Language;
import pt.keep.sceur.contextobjects.dcterms.Rights;
import pt.keep.sceur.contextobjects.dcterms.Spatial;
import pt.keep.sceur.contextobjects.dcterms.Type;
import pt.keep.sceur.contextobjects.dcterms.Utils.CTXOPrefixMapper;

public class View extends Event {
	// private Logger logger = Logger.getLogger(View.class);
	private int view_id;
	private String date;
	private int item_id;
	private String session_id;
	private String user_id;
	private String ip;
	private String country_code;
	private boolean aggregated;
	private boolean spider;
	private boolean keepwatched;

	public View() {
		super(SCHEMA + "view");
		super.setPartialEventFromSelect(false);
	}

	public View(TableRow tableRow) {
		super(SCHEMA + "view");
		super.setPartialEventFromSelect(false);
		addViewFromTableRow(tableRow);
	}

	public View(TableRow tableRow, boolean partialEventFromSelect, Context dspaceContext) {
		super(SCHEMA + "view");
		super.setPartialEventFromSelect(partialEventFromSelect);
		super.setDspaceContext(dspaceContext);
		addViewFromTableRow(tableRow);
	}

	public int getView_id() {
		return view_id;
	}

	public void setView_id(int view_id) {
		this.view_id = view_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public boolean isAggregated() {
		return aggregated;
	}

	public void setAggregated(boolean aggregated) {
		this.aggregated = aggregated;
	}

	public boolean isSpider() {
		return spider;
	}

	public void setSpider(boolean spider) {
		this.spider = spider;
	}

	public boolean isKeepwatched() {
		return keepwatched;
	}

	public void setKeepwatched(boolean keepwatched) {
		this.keepwatched = keepwatched;
	}

	public void addViewFromTableRow(TableRow tableRow) {
		if (super.isPartialEventFromSelect()) {
			view_id = tableRow.getIntColumn("id");
		} else {
			view_id = tableRow.getIntColumn("view_id");
			user_id = tableRow.getStringColumn("user_id");
			aggregated = tableRow.getBooleanColumn("aggregated");
			spider = tableRow.getBooleanColumn("spider");
			keepwatched = tableRow.getBooleanColumn("keepwatched");
		}
		String dateFromTableRow = Event.simpleDateFormatUntilDay.format(tableRow.getDateColumn("date"));
		String timeFromTableRow = Event.simpleDateFormatFromHourOn.format(tableRow.getDateColumn("time"));

		session_id = tableRow.getStringColumn("session_id");
		date = dateFromTableRow + "T" + timeFromTableRow + "Z";
		item_id = tableRow.getIntColumn("item_id");
		ip = tableRow.getStringColumn("ip");
		country_code = tableRow.getStringColumn("country_code");
	}

	@Override
	public String serialize2XML() {
		StringWriter sw = new StringWriter();
		try {
			// Get item information
			Item item = Item.find(super.getDspaceContext(), item_id);
			// FIXME - what to do when item==null (item not found for some
			// reason). Empty record? Delete record?
			if (item != null) {
				String itemHandle = item.getHandle();
				String type = null, rights = null, language = null;
				List<String> authors = null;

				type = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "type", Item.ANY, Item.ANY));
				rights = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "rights", Item.ANY, Item.ANY));
				// format =
				// EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc",
				// "format", Item.ANY, Item.ANY));
				language = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "language", "iso", Item.ANY));
				authors = EventsUtils.getAllElementsNotNullOrEmpty(item.getMetadata("dc", "contributor", "author", Item.ANY));

				// Create jaxb and primary contextobject's objects, setting some
				// contextobject information right here
				JAXBContext jc = JAXBContext.newInstance("pt.keep.sceur.contextobjects");
				ContextObjects contextObjects = new ContextObjects();
				ContextObject contextObject = new ContextObject();
				// event unique id
				contextObject.setIdentifier(DSpaceOAICatalog.OAI_ID_PREFIX + "view_" + view_id);
				// event generation date
				contextObject.setTimestamp(date);

				// Set referent information and add them to contextobject
				DescriptorType referentDescriptorType = new DescriptorType();
				if (itemHandle != null) {
					// handle of the record
					referentDescriptorType.getIdentifier().add(ConfigurationManager.getProperty("handle.canonical.prefix") + itemHandle);
				}
				// dc.type
				if (type != null) {
					EventsUtils.addDcTermMetadataToDescriptorType(referentDescriptorType, new Type(type));
				}
				// dc.rights
				if (rights != null) {
					EventsUtils.addDcTermMetadataToDescriptorType(referentDescriptorType, new Rights(rights));
				}
				// dc.language.iso
				if (language != null) {
					EventsUtils.addDcTermMetadataToDescriptorType(referentDescriptorType, new Language(language));
				}
				// dc.contributor.author
				for (String author : authors) {
					EventsUtils.addDcTermMetadataToDescriptorType(referentDescriptorType, new Author(author));
				}
				contextObject.setReferent(referentDescriptorType);

				// Set requester information
				DescriptorType requesterDescriptorType = new DescriptorType();
				requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + DigestUtils.md5Hex(ip + Event.salt));
				requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + ip.replaceFirst("[0-9]+$", "0"));
				requesterDescriptorType.getIdentifier().add(Event.SESSION_PREFIX + DigestUtils.md5Hex(session_id + Event.salt));
				EventsUtils.addDcTermMetadataToDescriptorType(requesterDescriptorType, new Spatial(country_code));
				contextObject.setRequester(requesterDescriptorType);

				// Set service-type information
				DescriptorType serviceTypeDescriptorType = new DescriptorType();
				EventsUtils.addDcTermMetadataToDescriptorType(serviceTypeDescriptorType, new Format("info:eu-repo/semantics/descriptiveMetadata"));

				contextObject.getServiceType().add(serviceTypeDescriptorType);

				// Set resolver information
				DescriptorType resolverTypeDescriptorType = new DescriptorType();
				contextObject.getResolver().add(resolverTypeDescriptorType);
				resolverTypeDescriptorType.getIdentifier().add(ConfigurationManager.getProperty("dspace.oai-stats.url") + "/request");

				// Add contextobject to contextobjects
				contextObjects.getContextObject().add(contextObject);

				// Marshall contextobjects to a stringwritter
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
						"info:ofi/fmt:xml:xsd:ctx http://www.openurl.info/registry/docs/info:ofi/fmt:xml:xsd:ctx");
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CTXOPrefixMapper());
				marshaller.marshal(contextObjects, sw);
			}
		} catch (SQLException e) {
			// Nothing here - return empty string
		} catch (JAXBException e) {
			// Nothing here - return empty string
		}
		return sw.toString();
	}

}
