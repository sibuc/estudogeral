/*
 * Country.java
 *
 * Copyright (c) 2007, University of Minho.  All rights reserved.
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

package org.dspace.app.stats;

import com.maxmind.geoip.*;

import com.maxmind.geoip.*;
import java.io.*;

import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.net.InetAddress;


/**
 * Class to get country code and name from MaxMind GeoIP
 * 
 * @author Angelo Miranda
 */
public class Country {
	
	private static Logger log = LogManager.getLogger(Country.class);
    /**
     * Return an IP country code and name 
     *
     * @param   ip    the ip to get the country
     *
     * @return        the country code and name in the form "country code;country name"
     */
    public static String getCountry(Context context, String ip) throws SQLException
    {
        if (IsInstitution(context, ip))
        {
            String institutionName = ConfigurationManager.getProperty("stats.country.institutionname");
            return "ZZ;" + institutionName;
        }

        DatabaseReader reader = null;

        try
        {
           String dbfile = ConfigurationManager.getProperty("stats.dbfile");

           // A File object pointing to your GeoIP2 or GeoLite2 database
           File database = new File(dbfile);

           reader = new DatabaseReader.Builder(database).build();

           // Replace "city" with the appropriate method for your database, e.g.,
           // "country".
           CountryResponse response = reader.country(InetAddress.getByName(ip));

	   if (response.getCountry().getIsoCode() == null)
	   {
		return "--;N/A";
	   }
	   else
	   {
               String temp = response.getCountry().getIsoCode() + ";" + response.getCountry().getName();

               return temp;
           }
        }
        catch (IOException e) {
            Country.log.error(e.getMessage(),e);
            return "--;N/A";
        }
        catch (ArrayIndexOutOfBoundsException e) {
           Country.log.error(e.getMessage(),e);
             return "--;N/A";
        } catch (GeoIp2Exception e){
            Country.log.error(e.getMessage(),e);
             return "--;N/A";
        }
        catch (Exception e){
            Country.log.error(e.getMessage(),e);
             return "--;N/A";
        }
        finally
        {
           try
           {
              if (reader != null) reader.close();
           }
           catch (IOException e){}
        }
    }

	
	public static boolean IsInstitution(Context context, String ip) throws SQLException
	{
        String sql = "select ip_range from stats.ip_institution";

        TableRowIterator iterator = DatabaseManager.query(context, sql);
        while (iterator.hasNext())
        {
            TableRow row = iterator.next();
            String range = row.getStringColumn("ip_range");
            if (ip.indexOf(range)==0)
                return true;
        }
        return false;
	}
}
