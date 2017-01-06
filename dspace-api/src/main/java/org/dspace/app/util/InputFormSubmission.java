/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.util;


import org.apache.log4j.Logger;

import java.io.*;
import java.util.regex.Pattern;
import org.dspace.core.Context;
import org.dspace.core.ConfigurationManager;

/**
 *
 * @author dspace
 */
public class InputFormSubmission {

     /** log4j logger */
    private static Logger log = Logger.getLogger(InputFormSubmission.class);

    public static void actualiza_forms (Context context, String ficheiro, String handle, String tipoprova) {

        try {
             
           String form = null;
           if (tipoprova.matches("doutoramento")) 
            form = ConfigurationManager.getProperty("academicos.form.doutoramentos");
           else 
            form = ConfigurationManager.getProperty("academicos.form.agregacao");
           
           String locais = ConfigurationManager.getProperty("webui.supported.locales");
           String[] config_locales = locais.split(Pattern.quote(","));
           
           String dspace_file = ConfigurationManager.getProperty("dspace.dir") + "/config/" + ficheiro;
           String filename = null;
           String outfilename = null;
                filename = dspace_file + ".xml";
                outfilename = dspace_file + "1.xml";
                FileInputStream fstream = new FileInputStream(filename);
                FileOutputStream outfstream = new FileOutputStream(outfilename);
                DataInputStream in = new DataInputStream(fstream);
                DataOutputStream out = new DataOutputStream(outfstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                String strLine;
                while ((strLine=br.readLine())!=null) {
                    if (strLine.contains("</form-map>")){
                        bw.write("<name-map collection-handle=\"" + handle + "\" form-name=\"" + form + "\" />");
                        bw.newLine();
                    }
                    else if (strLine.contains("</submission-map>")){
                        
                        bw.write("<name-map collection-handle=\"" + handle + "\" submission-name=\"" + form + "\" />");
                        bw.newLine();
                        
                    }
                    bw.write(strLine);
                    bw.newLine();
                }
                br.close();
                bw.close();
                File f1 = new File(filename);
                f1.delete();
                File f2 = new File(outfilename);
                f2.renameTo(f1);
        
            for (String config_locale : config_locales) {
                log.info("Config locale " + config_locale.trim());
                filename = dspace_file + "_" + config_locale.trim() + ".xml";
                outfilename = dspace_file + "_" + config_locale.trim() + "1.xml";
                fstream = new FileInputStream(filename);
                outfstream = new FileOutputStream(outfilename);
                in = new DataInputStream(fstream);
                out = new DataOutputStream(outfstream);
                br = new BufferedReader(new InputStreamReader(in));
                bw = new BufferedWriter(new OutputStreamWriter(out));
                strLine = null;
                while ((strLine=br.readLine())!=null) {
                    if (strLine.contains("</form-map>")){
                        bw.write("<name-map collection-handle=\"" + handle + "\" form-name=\"" + form + "\" />");
                        bw.newLine();
                    }
                    else if (strLine.contains("</submission-map>")){
                        
                        bw.write("<name-map collection-handle=\"" + handle + "\" submission-name=\"" + form + "\" />");
                        bw.newLine();
                        
                    }
                    bw.write(strLine);
                    bw.newLine();
                }
                br.close();
                bw.close();
                f1 = new File(filename);
                f1.delete();
                f2 = new File(outfilename);
                f2.renameTo(f1);
            }
        }
        catch (Exception e) {

            log.error(e.getLocalizedMessage());

        }

    }

       
}

