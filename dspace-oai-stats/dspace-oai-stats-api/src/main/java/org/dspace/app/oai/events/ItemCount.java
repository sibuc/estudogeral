package org.dspace.app.oai.events;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.digest.DigestUtils;
import org.dspace.app.oai.DSpaceOAICatalog;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import pt.keep.sceur.contextobjects.ContextObject;
import pt.keep.sceur.contextobjects.ContextObjects;
import pt.keep.sceur.contextobjects.DescriptorType;
import pt.keep.sceur.contextobjects.dcterms.Format;
import pt.keep.sceur.contextobjects.dcterms.Utils.CTXOPrefixMapper;

public class ItemCount extends Event {
	// private Logger logger = Logger.getLogger(ItemCount.class);
	private String date = null;

	public ItemCount(String table, Context dspaceContext) {
		super(null);
		super.setDspaceContext(dspaceContext);
	}

	public ItemCount(Item item) {
		super(null);
	}

	public String getItemCount_id() {
		return getDate();
	}

	public String getDate() {
		if (date == null) {
			date = Event.simpleDateFormat.format(new Date());
		}
		return date;
	}

	@Override
	public String serialize2XML() {
		StringWriter sw = new StringWriter();
		try {
			// Create jaxb and primary contextobject's objects, setting some
			// contextobject information right here
			JAXBContext jc = JAXBContext.newInstance("pt.keep.sceur.contextobjects");
			ContextObjects contextObjects = new ContextObjects();
			ContextObject contextObject = new ContextObject();
			// event unique id
			contextObject.setIdentifier(DSpaceOAICatalog.OAI_ID_PREFIX + "itemCount_" + getItemCount_id());
			// event generation date
			contextObject.setTimestamp(getDate());

			// Set referent information and add them to contextobject
			DescriptorType referentDescriptorType = new DescriptorType();
			int i = 0;
			for (Community c : Community.findAllTop(getDspaceContext())) {
				i += c.countItems();
			}
			referentDescriptorType.getIdentifier().add("" + i);
			contextObject.setReferent(referentDescriptorType);

			// Set requester information
			DescriptorType requesterDescriptorType = new DescriptorType();
			// INFO - define it to localhost (hack)
			requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + DigestUtils.md5Hex("127.0.0.1" + Event.salt));
			requesterDescriptorType.getIdentifier().add(Event.IP_PREFIX + "127.0.0.0");
			contextObject.setRequester(requesterDescriptorType);

			// Set service-type information
			DescriptorType serviceTypeDescriptorType = new DescriptorType();
			EventsUtils.addDcTermMetadataToDescriptorType(serviceTypeDescriptorType, new Format("info:eu-repo/semantics/itemCount"));

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
		} catch (JAXBException e) {
			// Nothing here - return empty string
		} catch (SQLException e) {
			// Nothing here - return empty string
		}
		return sw.toString();
	}
}
