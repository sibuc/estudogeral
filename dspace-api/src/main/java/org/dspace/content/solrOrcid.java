/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.content;

import org.apache.log4j.Logger;
import org.dspace.event.Event;
import org.dspace.core.*;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import java.sql.SQLException;
import org.dspace.authorize.AuthorizeException;

/**
 *
 * @author als
 * solrOrcid permite guardar numa tabela a correspondencia entre 
 * id gerados no controlo de autoridades do DSpace e o Orcid 
 * consultado a partir dos dados publicos do orcid
 */
public class solrOrcid extends DSpaceObject {
     /** log4j category */
    private static final Logger log = Logger.getLogger(solrOrcid.class);

    /** The table row corresponding to this item */
    private final TableRow solrOrcidRow;

    /** Authority Id */
    private String authority_id;
    
    /** Orc Id */
    private String orcid;   
    
    private boolean modified; 


    /**
     * Construct a solrOrcid with the given table row
     *
     * @param context
     *            the context this object exists in
     * @param row
     *            the corresponding row in the table
     * @throws SQLException
     */
    solrOrcid(Context context, TableRow row) throws SQLException
    {
        super(context);

        // Ensure that my TableRow is typed.
        if (null == row.getTable())
            row.setTable("orcid_authority");

        solrOrcidRow = row;
        
        // Cache ourselves
        context.cache(this, row.getIntColumn("orcid_authority_id"));
        modified = false;
        clearDetails();

    }
    public static solrOrcid create(Context context, String authority_id, String orcid) throws SQLException
    {


        // Create a table row or select table row that exist for that orcid...
        log.info("create orcid:" + orcid);
        TableRow row = DatabaseManager.findByUnique(context, "orcid_authority", "orcid", orcid);
        if (row==null) {
            log.info("row is null");
               row = DatabaseManager.create(context, "orcid_authority");
        }
        log.info("row is NOT null");
        solrOrcid s = new solrOrcid(context, row);

        log.info(LogManager.getHeader(context, "create_orcid_authority", "orcid_authority_id="
                + s.getID() + " ORCID length:" + orcid.trim().length()+ " authority length:" + authority_id.trim().length()));

        s.authority_id = authority_id.trim();
        s.orcid = orcid.trim();
        
        DatabaseManager.updateQuery(context,
                "UPDATE orcid_authority SET (authority_id,orcid) = ('" +  authority_id.trim() + "','" +
                orcid.trim() + "') WHERE orcid_authority_id= ? ",
                s.getID());

        return s;
    }

    /**
     * Get a orcid from the database. Loads in the metadata
     *
     * @param context
     *            DSpace context object
     * @param authority_id
     *            ID of the authority
     *
     * @return the collection, or null if the ID is invalid.
     * @throws SQLException
     */
    public static solrOrcid findorcid(Context context, String authority_id) throws SQLException
    {

        TableRow row = DatabaseManager.findByUnique(context, "orcid_authority", authority_id, null);
        if (row == null)
        {
            if (log.isDebugEnabled())
            {
                log.debug(LogManager.getHeader(context, "findorcid",
                        "not_found,authority_id=" + authority_id));
            }

            return null;
        }

        // not null, return Collection
        if (log.isDebugEnabled())
        {
            log.debug(LogManager.getHeader(context, "findorcid",
                    "authority_id=" + authority_id));
        }

        return new solrOrcid(context, row);
    }

    @Override
    public void updateLastModified()
    {

    }
    
    @Override
    public void update()
    {

    }
    
    @Override
    public String getName()
    {
       return null;
    }
    
    @Override
    public String getHandle()
    {
       return null;
    }
    
    public int getType()
    {
        return Constants.AUTHORITY;
    }
     /**
     * Get the internal ID of this authority
     *
     * @return the internal identifier
     */
    public int getID()
    {
        return solrOrcidRow.getIntColumn("orcid_authority_id");
    }
    


}