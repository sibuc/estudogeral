/*
 * RetrieveServlet.java
 *
 * Version: $Revision: 3705 $
 *
 * Date: $Date: 2009-04-11 19:02:24 +0200 (Sat, 11 Apr 2009) $
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
package org.dspace.app.webui.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
//import org.dspace.content.Metadatum;
import org.dspace.content.Metadatum;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.core.Utils;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;
import org.dspace.workflow.WorkflowItem;

/**
 * Servlet for retrieving handles. Search for handles giving the internal item ID.
 * <P>
 * <code>/retrieve/bitstream-id</code>
 *
 * @author Robert Tansley
 * @version $Revision: 3705 $
 */
@SuppressWarnings("serial")
public class ItemHandleServlet extends DSpaceServlet {
	/** log4j category */
	private static Logger log = Logger.getLogger(ItemHandleServlet.class);

	protected void doDSGet(Context context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			SQLException, AuthorizeException {
		ItemHandleServlet.log.debug("KEEP Servlet");
		PrintStream out = new PrintStream(response.getOutputStream());

		// Get the ID from the URL
		String idString = request.getPathInfo();

		if (idString != null) {
			// Remove leading slash
			if (idString.startsWith("/")) {
				idString = idString.substring(1);
			}

			// If there's a second slash, remove it and anything after it,
			// it might be a filename
			int slashIndex = idString.indexOf('/');

			if (slashIndex != -1) {
				idString = idString.substring(0, slashIndex);
			}

			// Find the corresponding item
			try {
				int id = Integer.parseInt(idString);

				TableRowIterator it = DatabaseManager.queryTable(context, "workflowitem",
		                "SELECT * FROM workflowitem WHERE item_id= ?",id);

				if (!it.hasNext()) {
					// ITEM NOT IN WORKFLOW
					Item item = Item.find(context, id);
					if (item == null) {
						// ITEM NOT FOUND
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
						out.print("The specified item wasn't found");
					} else {
						// ITEM FOUND -- but it cannot be associated with any collection
						if (item.getCollections().length == 0) {
							// Not associated to any collection
							response.setStatus(HttpServletResponse.SC_GONE);
							out.print("The specified item was rejected.");
						} else {
							List<Metadatum> values = Arrays.asList(item.getMetadata("dc", "identifier", "uri", Item.ANY));
							if (!values.isEmpty()) {
								Metadatum val = values.get(0);
								out.print(val.value);
							}
						}
					}
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					out.print("The specified item still in the deposit workflow.");
				}
			} catch (Exception nfe) {
				// Invalid ID - this will be dealt with below
				log.info(LogManager.getHeader(context, "resolve_handle",
						"error on input"),nfe);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				out.print("Try again, if the problem persists, please contact administration.");
			}
		}
	}
}
