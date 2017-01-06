package org.dspace.app.oai.events;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.digest.DigestUtils;
import org.dspace.app.oai.DSpaceOAICatalog;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;

import pt.keep.sceur.contextobjects.ContextObject;
import pt.keep.sceur.contextobjects.ContextObjects;
import pt.keep.sceur.contextobjects.DescriptorType;
import pt.keep.sceur.contextobjects.dcterms.Author;
import pt.keep.sceur.contextobjects.dcterms.Format;
import pt.keep.sceur.contextobjects.dcterms.Language;
import pt.keep.sceur.contextobjects.dcterms.Rights;
import pt.keep.sceur.contextobjects.dcterms.Type;
import pt.keep.sceur.contextobjects.dcterms.Utils.CTXOPrefixMapper;

public class Deposit extends Event {
	// private Logger logger = Logger.getLogger(Deposit.class);
	private Item item = null;

	public Deposit(String table) {
		super(null);
	}

	public Deposit(Item item) {
		super(null);
		this.item = item;
	}

	public String getDeposit_id() {
		return item.getHandle();
	}

	public String getDate() {
		return EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "date", "available", Item.ANY));
	}

	@Override
	public String serialize2XML() {
		StringWriter sw = new StringWriter();
		try {
			// Get item information
			// FIXME - what to do when item==null (item not found for some
			// reason). Empty record? Delete record?
			if (item != null) {
				String itemHandle = item.getHandle();
				String type = null, rights = null, language = null, date_available = null;
				List<String> authors = null, formats = null;

				date_available = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "date", "available", Item.ANY));
				type = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "type", Item.ANY, Item.ANY));
				rights = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "rights", Item.ANY, Item.ANY));
				language = EventsUtils.getFirstValueIfNotNullOrEmpty(item.getMetadata("dc", "language", "iso", Item.ANY));
				authors = EventsUtils.getAllElementsNotNullOrEmpty(item.getMetadata("dc", "contributor", "author", Item.ANY));
				formats = retrieveBitstreamInfo(item);

				// Create jaxb and primary contextobject's objects, setting some
				// contextobject information right here
				JAXBContext jc = JAXBContext.newInstance("pt.keep.sceur.contextobjects");
				ContextObjects contextObjects = new ContextObjects();
				ContextObject contextObject = new ContextObject();
				// event unique id
				contextObject.setIdentifier(DSpaceOAICatalog.OAI_ID_PREFIX + "deposit_" + getDeposit_id());
				// event generation date
				contextObject.setTimestamp(date_available);

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
				for (String format : formats) {
					EventsUtils.addDcTermMetadataToDescriptorType(referentDescriptorType, new Format(format));
				}
				contextObject.setReferent(referentDescriptorType);

				// Set requester information
				DescriptorType requesterDescriptorType = new DescriptorType();
				// INFO - define it to localhost (hack)
				requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + DigestUtils.md5Hex("127.0.0.1" + Event.salt));
				requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + "127.0.0.0");
				contextObject.setRequester(requesterDescriptorType);

				// Set service-type information
				DescriptorType serviceTypeDescriptorType = new DescriptorType();
				EventsUtils.addDcTermMetadataToDescriptorType(serviceTypeDescriptorType, new Format("info:eu-repo/semantics/deposit"));

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
		} catch (JAXBException e) {
			// Nothing here - return empty string
		}
		return sw.toString();
	}

	public List<String> retrieveBitstreamInfo(Item item) {
		List<String> result = new ArrayList<String>();
		String mimeType = null;
		try {
			Bundle[] bundles = item.getBundles("ORIGINAL");
			// logger.info("<1>"+bundles.length);
			for (int i = 0; (i < bundles.length); i++) {
				Bitstream[] bitstreams = bundles[i].getBitstreams();
				// logger.info("<2>"+bitstreams.length);
				for (int k = 0; (k < bitstreams.length); k++) {
					// logger.info("<3>[" + bitstreams[k].getID() + "] " +
					// bitstreams[k].getHandle() + " | " +
					// bitstreams[k].getName() + " | "
					// + bitstreams[k].getType() + " | " +
					// bitstreams[k].getSequenceID());
					if (!bitstreams[k].getFormat().isInternal()) {
						mimeType = bitstreams[k].getFormat().getMIMEType();
						if (!result.contains(mimeType)) {
							result.add(mimeType);
						}
					}
				}
			}
		} catch (SQLException e) {
			// Nothing here - return null string
		}
		return result;
	}

}
