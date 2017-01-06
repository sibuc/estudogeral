/*
 * StatsUtil.java
 *
 * Created on 02 Marce 2005, 17:00
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
 * - Neither the name of the University of Edinburgh, or the names of the
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
        
package org.dspace.app.webui.util.stats;

import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.handle.HandleManager;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.eperson.Group;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import java.lang.ClassCastException;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;

import org.dspace.core.I18nUtil;

import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.jsp.jstl.core.Config;
/**
 *
 * @author  Arnaldo Dantas
 */
public class StatsUtil {
    
        /** log4j category */
    private static Logger log = Logger.getLogger(StatsUtil.class);

    /**
     * get next ID object from object ID
     */
    public static String getIdFrom(Context c, String object, String objectID, String nextobject)
    {
        int newobjectID = -1;
        try
        {
            if(object == null || object.equals("null") || object.equals("") || object.equals(nextobject))
            {
                return objectID;
            }else if(object.equals("item"))
            {
                if (nextobject.equals("collection"))
                {
                    newobjectID = getCollectionByItem(c, objectID);
                }else if (nextobject.equals("community"))
                {
                    newobjectID = getCommunityByItem(c, objectID);
                }
            }else if(object.equals("collection"))
            {
                if (nextobject.equals("community"))
                {
                    newobjectID = getCommunityByCollection(c, objectID);
                }   
            }
            return Integer.toString(newobjectID);
        } catch (SQLException e)
        {
        	log.debug(e.getMessage(), e);
        }
        return "-1";
    }

    /**
     * Get CollectionID from itemID
     */
    public static int getCollectionByItem(Context c, String item) throws SQLException
    {
        Item it=null;
        DSpaceObject dso = null;
        // Se o objectID e handle, retira o item_id
        if(item.indexOf('/')>0)
        {
            dso = HandleManager.resolveToObject(c, item);
            if (dso != null && dso.getType() == Constants.ITEM)
                it = (Item) dso;
            else
                return -1;
        }else
        {
            it = Item.find(c, Integer.parseInt(item));
        }
        if (it==null)
            return -1;
        Collection [] col = it.getCollections();
        if (col.length>0)
            return col[0].getID();
        else
            return -1;
    }

     /**
     * Get CommunityID from itemID
     */
    public static int getCommunityByItem(Context c, String item) throws SQLException
    {
        Item it=null;
        DSpaceObject dso = null;
        // Se o objectID e handle, retira o item_id
        if(item.indexOf('/')>0)
        {
            dso = HandleManager.resolveToObject(c, item);
            if (dso != null && dso.getType() == Constants.ITEM)
                it = (Item) dso;
            else
                return -1;
        }else
        {
            it = Item.find(c, Integer.parseInt(item));
        }
        if (it==null)
            return -1;
        Community [] com = it.getCommunities();
        if (com.length>0)
            return com[0].getID();
        else
            return -1;
    }

    /**
     * Get CommunityID from collectionID
     */
    public static int getCommunityByCollection(Context c, String collectionId) throws SQLException
    {
        Collection col = Collection.find(c, Integer.parseInt(collectionId));
        if (col==null)
            return -1;
        Community [] com = col.getCommunities();
        if (com.length>0)
            return com[0].getID();
        else
            return -1;
    }
    /*
     * If corrent eperson have access return true else false
     */
    public static boolean checkAcess(Context c, String groupsIds) throws SQLException, AuthorizeException
    {
    	// if user belongs to a stats admin group
    	if (StatsUtil.isAdminStatistics(c)) {
    		return true;
    	}

        List groups = new ArrayList();
        if (groupsIds == null || groupsIds.length()==0) 
        	return false;

        // get a list of groups
        int vi=0;
        int vf = groupsIds.indexOf(" ");
        while (vf > vi)
        {
            String str = groupsIds.substring(vi, vf);
            groups.add(str);
            vi = vf + 1;
            vf = groupsIds.indexOf(" ", vi);
        }
        if (vi>0)
            groups.add(groupsIds.substring(vi, groupsIds.length()));
        if (groups.size()==0)
            groups.add(groupsIds);
        
        //Validate if has access for groups
        for (int i=0; i<groups.size(); i++)
        {
            int g = Integer.parseInt((String) groups.get(i));
            if (Group.isMember(c, g))
                return true;
        }
        return false;
    }
    
        /*
     * If corrent eperson have access return true else false
     */
    public static boolean isAuthorized(Context c, String objectType, String paramValue) 
    {
    	boolean allvisible = ConfigurationManager.getBooleanProperty("stats.allvisible", false);
    	// if user belongs to a stats admin group
    	if (StatsUtil.isAdminStatistics(c)) {
    		return true;
    	}

        try {
            DSpaceObject dso = null;
            // is handle or id ?
            if (paramValue.indexOf("/")>0)
            {
                dso = HandleManager.resolveToObject(c, paramValue);
                if(dso==null)
                    return false;
            }

            if(objectType.equals("item"))
            {
            	if (allvisible) return true;
               Item it = null;
                if (dso!=null)
                {
                    it=(Item) dso;
                }else{
                    it = Item.find(c, Integer.parseInt(paramValue));
                }
                if(it == null)
                    return false;
               if(AuthorizeManager.authorizeActionBoolean(c, it, Constants.READ))
                   return true;

            }else if (objectType.equals("collection"))
            {
            	if (allvisible) return true;
               Collection col = null;
                if (dso!=null)
                {
                    col=(Collection) dso;
                }else{
                    col = Collection.find(c, Integer.parseInt(paramValue));
                }
                if(col == null)
                    return false;
               if(AuthorizeManager.authorizeActionBoolean(c, col, Constants.VIEW_STATISTICS))
                   return true;

            }else if (objectType.equals("community"))
            {

            	if (allvisible) return true;
                Community com = Community.find(c, Integer.parseInt(paramValue));

                if(com == null)
                    return false;
                
               if(AuthorizeManager.authorizeActionBoolean(c, com, Constants.VIEW_STATISTICS))
                   return true;
            }
        }catch(SQLException e)
        {
        	log.debug("ERRO ao obter autorizacao : "+e.getMessage(), e);
            return false;
        }catch(ClassCastException  cast)
        {
        	log.debug("ERRO ao obter autorizacao : ClassCastException:"+cast.getMessage(), cast);
            return false;
        }
        return false;
 
    }

        /*
     * If current eperson are in special group of statistics
     */
    public static boolean isAdminStatistics(Context c) 
    {
        String filePath = getStatsPath() + "stats-admins.xml";
        
        Document doc = null;
        try {
                DOMParser parser = new DOMParser();
                parser.parse(filePath);
                doc = parser.getDocument();
                NodeList listAdmins = (NodeList) XPathAPI.selectNodeList(doc, "admins/dspace-group");
                if (listAdmins == null || listAdmins.getLength()== 0)
                        return false;
                    for (int i =0 ; i<listAdmins.getLength(); i++)
                    {
                        String admin = ((Element)listAdmins.item(i)).getAttribute("id");
                        if (Group.isMember(c, Integer.parseInt(admin)))
                            return true;
                    }
        } catch (SAXException e) {
        	log.debug(e.getMessage(), e);
        } catch (IOException e) {
        	log.debug(e.getMessage(), e);
        } catch (TransformerException e) {
        	log.debug(e.getMessage(), e);
        }catch (SQLException e) {
        	log.debug(e.getMessage(), e);
        }
        return false;
    }
    public static String getStatsPath() {
    	String configPath = ConfigurationManager.getProperty("dspace.dir") + 
								File.separator + "config" + File.separator;
    	return configPath + File.separator + "stats" + File.separator;
    }
    public static String getStatsXslPath() {
    	return getStatsPath() + "xsl" + File.separator;
    }
    public static String getLocalizedFileName(Locale loc, String fileN, String FileType)
    {
        String fileName = null;
        String fileNameVCL = null;
        String fileNameCL = null;
        String fileNameL = null;
        fileNameL = fileN + "_" + loc.getLanguage();
        
        String fileT = FileType;

        if (fileT == null)
        {
            fileT = "";
        }

        if (!("".equals(loc.getCountry())))
        {
            fileNameCL = fileN + "_" + loc.getLanguage() + "_"
                    + loc.getCountry();

            if (!("".equals(loc.getVariant())))
            {
                fileNameVCL = fileN + "_" + loc.getLanguage() + "_"
                        + loc.getCountry() + "_" + loc.getVariant();
            }
        }

        fileName = fileN;

        if (fileNameVCL != null)
        {
            File fileTmp = new File(fileNameVCL + fileT);
            if (fileTmp.exists())
            {
                return fileNameVCL + fileT;
            }
        }

        if (fileNameCL != null)
        {
            File fileTmp = new File(fileNameCL + fileT);
            if (fileTmp.exists())
            {
                return fileNameCL + fileT;
            }
        }

        if (fileNameL != null)
        {
            File fileTmp = new File(fileNameL + fileT);
            if (fileTmp.exists())
            {
                return fileNameL + fileT;
            }
        }

        return fileName + fileT;
    }
    public static Locale getSessionLocale(HttpServletRequest request) {

        String paramLocale = request.getParameter("locale");
        Locale sessionLocale = null;
        Locale supportedLocale = null;

        if (!StringUtils.isEmpty(paramLocale))
        {
            /* get session locale according to user selection */
            sessionLocale = new Locale(paramLocale);
        }


        if (sessionLocale == null)
        {
            /* get session locale set by application */
            HttpSession session = request.getSession();
            sessionLocale = (Locale) Config.get(session, Config.FMT_LOCALE);
        }

        /*
         * if session not set by selection or application then default browser
         * locale
         */
        if (sessionLocale == null)
        {
            sessionLocale = request.getLocale();
        }

        if (sessionLocale == null)
        {
            sessionLocale = I18nUtil.DEFAULTLOCALE;
        }
        supportedLocale =  I18nUtil.getSupportedLocale(sessionLocale);

        return supportedLocale;


    }
}
