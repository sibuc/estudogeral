/*
 * DSpaceRecordFactory.java
 *
 * Version: $Revision: 3705 $
 *
 * Date: $Date: 2009-04-11 17:02:24 +0000 (Sat, 11 Apr 2009) $
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.oai;

import java.util.Iterator;
import java.util.Properties;

import org.dspace.app.oai.events.Deposit;
import org.dspace.app.oai.events.Download;
import org.dspace.app.oai.events.ItemCount;
import org.dspace.app.oai.events.Publication;
import org.dspace.app.oai.events.View;

import ORG.oclc.oai.server.catalog.RecordFactory;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;

/**
 * Implementation of the OAICat RecordFactory base class for DSpace items.
 * 
 * @author Robert Tansley
 * @version $Revision: 3705 $
 */
public class DSpaceRecordFactory extends RecordFactory {
	/** log4j category */
	// private static Logger log = Logger.getLogger(DSpaceRecordFactory.class);

	public DSpaceRecordFactory(Properties properties) {
		// We don't use the OAICat properties; pass on up
		super(properties);
	}

	public String fromOAIIdentifier(String identifier) {
		// FIXME - other then oai identifier ? ? ? ?
		return identifier;
	}

	public String quickCreate(Object nativeItem, String schemaURL, String metadataPrefix) throws IllegalArgumentException,
			CannotDisseminateFormatException {
		// Not supported
		return null;
	}

	public String getOAIIdentifier(Object event) {
		String h = DSpaceOAICatalog.OAI_ID_PREFIX;
		if (event instanceof View) {
			h += "view_" + ((View) event).getView_id();
		} else if (event instanceof Download) {
			h += "download_" + ((Download) event).getDownload_id();
		} else if (event instanceof Deposit) {
			h += "deposit_" + ((Deposit) event).getDeposit_id();
		} else if (event instanceof Publication) {
			h += "publication_" + ((Publication) event).getPublication_id();
		} else if (event instanceof ItemCount) {
			h += "itemCount_" + ((ItemCount) event).getItemCount_id();
		}
		return h;
	}

	public String getDatestamp(Object event) {
		// Return as ISO8601
		String date = "";
		if (event instanceof View) {
			date = ((View) event).getDate();
		} else if (event instanceof Download) {
			date = ((Download) event).getDate();
		} else if (event instanceof Deposit) {
			date = ((Deposit) event).getDate();
		} else if (event instanceof Publication) {
			date = ((Publication) event).getDate();
		} else if (event instanceof ItemCount) {
			date = ((ItemCount) event).getDate();
		}
		return date;
	}

	public Iterator<Object> getSetSpecs(Object nativeItem) {
		// There is no set concept, for now...
		return null;
	}

	public boolean isDeleted(Object nativeItem) {
		// an event never has the deleted status - doesn't make any sense
		return false;
	}

	public Iterator<Object> getAbouts(Object nativeItem) {
		// There is no "abouts" concept, for now...
		return null;
	}
}
