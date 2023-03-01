/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.fileServers;

import controllers.Databases;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import models.PropertiesFile;
import models.java_certificate.Java_Certificate;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author atovar
 */

public class InvokeTaskFS implements Job {

    static Logger logger = Logger.getLogger(InvokeTaskFS.class);
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        /* VERIFICACION VALIDEZ DEL CERTIFICADO */
        boolean valid = false; //Booleano verifica si ejecuta o no
        try {
            valid = Java_Certificate.check(); //ejecuta metodo de validacion
        } catch (Exception e) { //Captura excepcion en caso de generarse
            logger.error("excepcion: " + e);
        }
        if (valid) {
            try {
                // Se hace split con caracter# para los datos de sincronizacion
                String[] datos = jec.getJobDetail().getDescription().split("#",-1);
                
                String reqUrl;
                
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                pf.loadOtherPropertiesBD();
                
                String pathModuleDatabases = pf.getPathModuleDatabases();               

                reqUrl = pathModuleDatabases+"executeProcessFS?selConexion="+URLEncoder.encode(datos[0], "UTF-8")+"&folderRaiz="+URLEncoder.encode(datos[1], "UTF-8");
                
                logger.info(reqUrl);
                
                URL urlGet = new URL(reqUrl);
                HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setDoOutput(true);
                
                int responseCode = con.getResponseCode();
                logger.info("Response Code : " + responseCode);

                StringBuilder responseInt;
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    responseInt = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        responseInt.append(inputLine);
                    }
                }

                logger.info("Response update : " + responseInt.toString());
                
                if(responseCode==HttpURLConnection.HTTP_OK){
                    File python = new File(pf.getPathFileModule()+"/fileServers/filesViews/"+datos[0]+"/serviceCleanData.py");
                    if(python.exists()){
                        if(getGenerateScript(datos[0],"fileServer")==HttpURLConnection.HTTP_OK){
                            getIndexer(datos[0],"fileServer");
                        }
                    }
                }
            } catch (MalformedURLException ex) {
                logger.error(ex);
            } catch (IOException ex) {
                logger.error(ex);
            }
 
        }
    }
    
    public int getGenerateScript(String nameConfiguration, String type){
        int responseCode=0;
        try {
            String reqUrl;
            PropertiesFile pf = new PropertiesFile();
            pf.loadOtherPropertiesCleanData();

            String pathModuleBootle = "http://"+pf.getIpServiceCleanData()+":"+pf.getPortBootleCleanData()+"/";

            reqUrl = pathModuleBootle+"convertScript?nameConfig="+nameConfiguration+"&type="+type;

            logger.info(reqUrl);

            URL urlGet = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setDoOutput(true);

            responseCode = con.getResponseCode();
            logger.info("Response Code : " + responseCode);
            
            StringBuilder responseInt;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                responseInt = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseInt.append(inputLine);
                }
            }

            logger.info("Response Create Python : " + responseInt.toString());
            
        }   catch (MalformedURLException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return responseCode;
    }
    
    public int getIndexer(String nameConfiguration, String type){
        int responseCode=0;
        try{
            String reqUrl;
            
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesCleanData();
            
            String pathModuleBootle = "http://"+pf.getIpServiceCleanData()+":"+pf.getPortBootleCleanData()+"/";
            
            Databases db = new Databases();
            String pathSolr = db.loadPathService(pf.getPathFileModule()+"/databases/connections.xml", nameConfiguration);
            
            reqUrl = pathModuleBootle+"indexFile?nameConfig="+nameConfiguration+"&type="+type+"&pathSolr="+pathSolr;
            
            logger.info(reqUrl);
            
            URL urlGet = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setDoOutput(true);
            
            responseCode = con.getResponseCode();
            logger.info("Response Code : " + responseCode);
            
            StringBuilder responseInt;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                responseInt = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseInt.append(inputLine);
                }
            }

            logger.info("Response Indexer Solr : " + responseInt.toString());
            
        }catch (MalformedURLException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return responseCode;
    }
}
