/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority.orcid;

import org.dspace.authority.AuthorityValue;
import org.dspace.authority.orcid.model.Bio;
import org.dspace.authority.orcid.model.Work;
import org.dspace.authority.orcid.xml.XMLtoBio;
import org.dspace.authority.orcid.xml.XMLtoWork;
import org.dspace.authority.rest.RestSource;
import org.apache.log4j.Logger;
import org.dspace.utils.DSpace;
import org.w3c.dom.Document;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class Orcid extends RestSource {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(Orcid.class);

    private static Orcid orcid;

    public static Orcid getOrcid() {
        if (orcid == null) {
            orcid = new DSpace().getServiceManager().getServiceByName("OrcidSource", Orcid.class);
        }
        return orcid;
    }

    private Orcid(String url) {
        super(url);
    }

    public Bio getBio(String id) {
        Document bioDocument = restConnector.get(id + "/orcid-bio");
        XMLtoBio converter = new XMLtoBio();
        Bio bio = converter.convert(bioDocument).get(0);
        bio.setOrcid(id);
        return bio;
    }

    public List<Work> getWorks(String id) {
        Document document = restConnector.get(id + "/orcid-works");
        XMLtoWork converter = new XMLtoWork();
        return converter.convert(document);
    }

    public List<Bio> queryBio(String name, int start, int rows) {
        String nameurl = null;
        try {
            nameurl = URLEncoder.encode("\"" + name + "\"","UTF-8");
        }
        catch (Exception e) {
            log.info("URLEncoder exception");
        }
        Document bioDocument = restConnector.get("search/orcid-bio?q=" + nameurl + "&start=" + start + "&rows=" + rows);
        XMLtoBio converter = new XMLtoBio();
        return converter.convert(bioDocument);
    }
        public List<Bio> queryBio_a(String name, int start, int rows) {
        String nameurl = null;
        try {
            nameurl = name;
        }
        catch (Exception e) {
            log.info("URLEncoder exception");
        }
        Document bioDocument = restConnector.get("search/orcid-bio?q=" + nameurl + "&start=" + start + "&rows=" + rows);
        XMLtoBio converter = new XMLtoBio();
        return converter.convert(bioDocument);
    }

    @Override
    public List<AuthorityValue> queryAuthorities(String text, int max) {
        log.info("queryAuthorities - text " + text);
        String family_name = null;
        String given_names = null;
        int pos = 0;
        if (!text.isEmpty()) {
        if (text.contains(",")) {
                pos = text.indexOf(",");
        }
        else if (text.contains(" "))
            pos = text.indexOf(" ");
        else 
            pos = text.length();
        log.info("pos:"+pos);
        // Verifica ORCID
        String[] orcid_parts = text.split("-",4);
        boolean orcid = true;
        for (int i=0;i<orcid_parts.length;i++) {
            if (orcid_parts[i].length()!=4)
                orcid=false;
        }
        if (orcid) {
            try {
            text=URLEncoder.encode("orcid:"+text,"UTF-8");
            }
          catch (Exception e) {
            log.info("URLEncoder exception");
          }          
        }
        else {
        if (pos>-1){
            family_name = text.substring(0, pos);
            log.info("family_name:"+family_name);
            if (pos+1<text.length()){
                given_names = text.substring(pos+1);
                log.info("given-names:"+given_names);
            }
        }
        try {
        text = "(family-name:"+URLEncoder.encode(family_name.trim(),"UTF-8").replaceAll("\\+", "%20");
        if (given_names!=null)
            text = text +"+AND+given-names:"+URLEncoder.encode(given_names.trim(),"UTF-8").replaceAll("\\+", "%20")+")";
        else {
            log.info("Given names is null");
            text=text+")";
        }
        log.info("Text apos 1o given names:" + text);
        text =text + "+OR+(family-name:"+URLEncoder.encode(StringUtils.stripAccents(family_name),"UTF-8").replaceAll("\\+", "%20");
        if (given_names!=null)
            text = text +"+AND+given-names:"+URLEncoder.encode(StringUtils.stripAccents(given_names),"UTF-8").replaceAll("\\+", "%20") +")";
        else 
           text=text +")";      
        }
        catch (Exception e) {
            log.info("URLEncoder exception");
        }
        }
        log.info("queryAuthorities - newtext " + text);
        } 
        List<Bio> bios = queryBio_a(text, 0, max);
        List<AuthorityValue> authorities = new ArrayList<AuthorityValue>();
        for (Bio bio : bios) {
            authorities.add(OrcidAuthorityValue.create(bio));
        }
        return authorities;
    }

    @Override
    public AuthorityValue queryAuthorityID(String id) {
        Bio bio = getBio(id);
        return OrcidAuthorityValue.create(bio);
    }
}
