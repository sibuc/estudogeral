package org.dspace.app.oai;

import java.util.Properties;

import org.dspace.app.oai.events.Event;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

public class CTXOCrosswalk extends Crosswalk{

	public CTXOCrosswalk(Properties properties) {
		super("info:ofi/fmt:xml:xsd:ctx " + "http://www.openurl.info/registry/docs/xsd/info:ofi/fmt:xml:xsd:ctx");
	}

	@Override
	public String createMetadata(Object event)
			throws CannotDisseminateFormatException {
		String result = null;
		result = ((Event) event).serialize2XML();
		return result;
	}

	@Override
	public boolean isAvailableFor(Object arg0) {
		return true;
	}
}
