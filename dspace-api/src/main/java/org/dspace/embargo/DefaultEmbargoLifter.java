/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.embargo;

import java.sql.SQLException;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.core.ConfigurationManager;

/**
 * Default plugin implementation of the embargo lifting function.
 *
 * @author Larry Stone
 * @author Richard Rodgers
 */
public class DefaultEmbargoLifter implements EmbargoLifter
{
    public DefaultEmbargoLifter()
    {
        super();
    }

    /**
     * Enforce lifting of embargo by turning read access to bitstreams in
     * this Item back on.
     *
     * @param context the DSpace context
     * @param item    the item to embargo
     */
    public void liftEmbargo(Context context, Item item)
            throws SQLException, AuthorizeException, IOException
    {
        // remove the item's policies and replace them with
        // the defaults from the collection
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String openaccess = ConfigurationManager.getProperty("embargo.terms.openaccess");
                
        item.inheritCollectionDefaultPolicies(item.getOwningCollection());
        if (item.getMetadata("dc.rights")!=null)
            item.clearMetadata("dc", "rights", Item.ANY, Item.ANY);
        item.addMetadata("dc","rights", null, null, openaccess);
        
        item.addMetadata("dc", "description", "provenance", "por", "Efetuado o levantamento da situação de embargo em " + dateFormat.format(date));
    }
}
