/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.embargo;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.dspace.content.Community;
import org.dspace.content.Collection;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;


/**
 * 
 * 
 * 
 * 
 */
public class EmbargoAcademicos
{

    private static Logger log = Logger.getLogger(EmbargoAcademicos.class);

    public static void main(String argv[])
    {
        int status =0;
        Context context = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int prazo = ConfigurationManager.getIntProperty("academicos.default.diasprazo");
        Group submitters = null;

        try
        {
        context = new Context();
        context.setIgnoreAuthorization(true);
        Date now = new Date();
        // create prazo de deposito
        Community Comm_dout = Community.find(context, ConfigurationManager.getIntProperty("comm.doutoramentos"));

        Collection cols[] = Comm_dout.getCollections();
        log.info("Embargo Academicos");
        String metadatum = null;
        for (int i=0; i<cols.length; i++) {

            try {
            log.info("Doutorando="+ cols[i].getName());
            try {
            metadatum = cols[i].getMetadataByMetadataString("collection.inicioprazodeposito")[0].value;
            if (metadatum!=null) {
            Date data = formatter.parse(metadatum);
            long diff = now.getTime() - data.getTime();
            long diffdays = diff / (24 * 60 * 60 * 1000);
            if (diffdays > prazo ){
                log.info("Data atual=" + formatter.format(now.getTime()));
                log.info("Data colecao=" + formatter.format(data.getTime()));
                log.info("Prazo=" + prazo);
                log.info("Diferenca datas = " + diffdays);
                submitters = cols[i].getSubmitters();
                EPerson members[]= submitters.getMembers();
                log.info("Nº de Membros que podem submeter = " + members.length);
                for (int j=0; j < members.length; j++) {
                    submitters.removeMember(members[j]);
                    log.info("Membro removido="  + members[j].getFullName());
                   
                }
                submitters.update();
            }
            }
            }
            catch (IllegalArgumentException iae) {
               log.info("Illegal Argument Exception");
               continue;
           }
            }
           catch (Exception e) {
            continue;
           }
         }
            log.debug("Cache size at end = "+ context.getCacheSize());
            context.complete();
            context = null;
        }
        catch (Exception e)
        {
            System.err.println("ERROR, got exception: " + e);
            e.printStackTrace();
            status = 1;
        }
        finally
        {
            if (context != null)
            {
                try
                {
                    context.abort();
                }
                catch (Exception e)
                {
                }
            }
        }
        System.exit(status);
    }


}
