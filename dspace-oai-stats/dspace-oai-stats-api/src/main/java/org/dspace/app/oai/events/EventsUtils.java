package org.dspace.app.oai.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.content.DCValue;

import pt.keep.sceur.contextobjects.DescriptorType;
import pt.keep.sceur.contextobjects.MetadataByValType;
import pt.keep.sceur.contextobjects.MetadataType;

public class EventsUtils {
	public static Logger logger = Logger.getLogger(EventsUtils.class);

	public static void addDcTermMetadataToDescriptorType(DescriptorType descriptorType, Object dcTerm) {
		// MetadataByValType metadataByValueType = new MetadataByValType();
		// metadataByValueType.setFormat("http://dublincore.org/documents/dcmi-terms/");
		// MetadataType dctermsMetadataType = new MetadataType();
		// dctermsMetadataType.setAny(dcTerm);
		// metadataByValueType.setMetadata(dctermsMetadataType);
		// descriptorType.getMetadataByVal().add(metadataByValueType);
		MetadataByValType metadataByValueType = null;
		List<Object> listOfDcTerms = new ArrayList<Object>();
		List<MetadataByValType> metadataByVal = descriptorType.getMetadataByVal();
		if (metadataByVal != null) {
			for (MetadataByValType metadata : metadataByVal) {
				if (metadata != null && metadata.getFormat() != null && metadata.getFormat().equals("http://dublincore.org/documents/dcmi-terms/")) {
					metadataByValueType = metadata;
					listOfDcTerms = metadataByValueType.getMetadata().getAny();
					break;
				}
			}
		} else {
			metadataByValueType = new MetadataByValType();
			metadataByValueType.setFormat("http://dublincore.org/documents/dcmi-terms/");
			descriptorType.getMetadataByVal().add(metadataByValueType);
		}
		if (metadataByValueType == null) {
			metadataByValueType = new MetadataByValType();
			metadataByValueType.setFormat("http://dublincore.org/documents/dcmi-terms/");
			descriptorType.getMetadataByVal().add(metadataByValueType);
		}
		MetadataType dctermsMetadataType = new MetadataType();
		listOfDcTerms.add(dcTerm);
		dctermsMetadataType.setAny(listOfDcTerms);
		metadataByValueType.setMetadata(dctermsMetadataType);
	}

	public static String getFirstValueIfNotNullOrEmpty(DCValue[] metadataArray) {
		String res = null;
		if (metadataArray != null && metadataArray.length > 0 && metadataArray[0].value != null) {
			res = metadataArray[0].value;
		}
		return res;
	}

	public static List<String> getAllElementsNotNullOrEmpty(DCValue[] metadataArray) {
		List<String> res = new ArrayList<String>();
		if (metadataArray != null) {
			for (DCValue value : metadataArray) {
				if (value.value != null && !value.value.equals("")) {
					res.add(value.value);
				}
			}
		}
		return res;
	}
}
