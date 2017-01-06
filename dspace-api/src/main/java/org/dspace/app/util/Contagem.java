/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.apache.log4j.Logger;

/**
 *
 * @author als
 */
public class Contagem {

     private static Logger log = Logger.getLogger(Contagem.class);
    
     /** als
     *  Faz contagem de items em acesso aberto
     */
    public static int CountOpenAccess(Context context) throws SQLException, AuthorizeException, IOException {
        
        MetadataSchema mds = MetadataSchema.find(context, MetadataSchema.DC_SCHEMA_ID);
        if (mds == null) {
            throw new IllegalArgumentException("No such metadata schema:" + MetadataSchema.DC_SCHEMA_ID);
        }
        MetadataField mdf = MetadataField.findByElement(context, mds.getSchemaID(), "rights", null);
        
        if (mdf == null) {
            throw new IllegalArgumentException(
                    "No such metadata field: dc.rights");
        }

        String query = "SELECT (count(item_id)::int) FROM metadatavalue,item WHERE item.in_archive='1' " +
                "AND item.item_id = metadatavalue.resource_id AND metadatavalue.resource_type_id=2 AND metadata_field_id = ? " +
                " AND position('open' in lower(metadatavalue.text_value))>0";
        TableRow rows = null; 
        log.info("CountOpenAccess:" + query);
            rows = DatabaseManager.querySingle(context,query, mdf.getFieldID());
        log.info("Object class:"+rows.getClass().getName());
        int contagem = 0;
        
        if (rows.getIntColumn("count") !=0) {
         
            contagem = rows.getIntColumn("count");
           } 
        else log.info("linhas:" + rows.getIntColumn("count"));
        return contagem;
    }
         /** als
     *  Faz contagem de items em acesso aberto
     */
    public static int CountDownloads(Context context) throws SQLException, AuthorizeException, IOException {
        
        Calendar now = Calendar.getInstance();
        Date date = now.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMM");
        Object[] params = null;
        String yearmonth =  format1.format(now.getTime()) ;
        log.info("CountDownloads:" + yearmonth);
        int anomes = Integer.parseInt(yearmonth);
        params = new Object[] {anomes};
        String query = "select (value::int) from stats.download_month where yearmonth= ? ";
        TableRow rows = null; 
        log.info("CountDownloads:" + query);
            rows = DatabaseManager.querySingle(context, query, params);
        if (rows == null) log.info("rows is null");
       int contagem = 0;
        if (rows != null) {
            contagem = rows.getIntColumn("value");
        }
        return contagem;
    }
}
