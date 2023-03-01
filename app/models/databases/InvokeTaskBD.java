

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.databases;

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
import static play.mvc.Http.HeaderNames.USER_AGENT;

/**
 *
 * @author atovar
 */

public class InvokeTaskBD implements Job {

    static Logger logger = Logger.getLogger(InvokeTaskBD.class);
    
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

                reqUrl = pathModuleDatabases+"executeProcess?selConexion="+URLEncoder.encode(datos[0], "UTF-8")+"&folderRaiz="+URLEncoder.encode(datos[1], "UTF-8");
                
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
                    File python = new File(pf.getPathFileModule()+"/databases/filesViews/"+datos[0]+"/"+datos[0]+".py");
                    if(python.exists()){
                        if(getGenerateScript(datos[0],"databases","dashboards")==HttpURLConnection.HTTP_OK){
                            if(getRunPythonScript(datos[0],"databases","dashboards")==HttpURLConnection.HTTP_OK){
                                getIndexer(datos[0],"databases","dashboards");
                            }
                        }
                    }else{
                       logger.info("No Existe el python");
                    }
                }
            } catch (MalformedURLException ex) {
                logger.error(ex);
            } catch (IOException ex) {
                logger.error(ex);
            }
 
        }
    }
    
    public int getGenerateScript(String nameConfiguration, String type, String admin){
        int responseCode=0;
        try {
            String reqUrl;
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesCleanData();

            String pathModuleBootle = "http://"+pf.getIpServiceCleanData()+":"+pf.getPortBootleCleanData()+"/";

            reqUrl = pathModuleBootle+"convertScript?nameConfig="+URLEncoder.encode(nameConfiguration, "UTF-8").replace("+", "%20")+"&type="+type+"&admin="+admin;

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
    
    public int getRunPythonScript(String nameConfiguration, String type, String admin){
        int responseCode=0;
        try {
            String reqUrl;
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesCleanData();

            String pathModuleBootle = "http://"+pf.getIpServiceCleanData()+":"+pf.getPortBootleCleanData()+"/";

            reqUrl = pathModuleBootle+"runPython?nameConfig="+URLEncoder.encode(nameConfiguration, "UTF-8").replace("+", "%20")+"&type="+type+"&admin="+admin;

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

            logger.info("Response Execute Python : " + responseInt.toString());
            
        }   catch (MalformedURLException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return responseCode;
    }
    
    public int getIndexer(String nameConfiguration, String type, String admin){
        int responseCode=0;
        try{
            String reqUrl;
            
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesCleanData();
            
            String pathModuleBootle = "http://"+pf.getIpServiceCleanData()+":"+pf.getPortBootleCleanData()+"/";
            
            Databases db = new Databases();
            String respFun="", pathSolr,hiddenCleanCore;
            if(type.equals("databases")){
                respFun = db.loadPathService(pf.getPathFileModule()+"/databases/connections.xml", nameConfiguration);
            }else if(type.equals("fileServer")){
                respFun = db.loadPathService(pf.getPathFileModule()+"/fileServers/connections.xml", nameConfiguration);
            }
            
            pathSolr = respFun.split("<;>",-1)[0];
            hiddenCleanCore = respFun.split("<;>",-1)[1];
            
            reqUrl = pathModuleBootle+"indexFile?nameConfig="+URLEncoder.encode(nameConfiguration, "UTF-8").replace("+", "%20")+"&type="+type+"&pathSolr="+pathSolr+"&cleanCore="+hiddenCleanCore+"&admin="+admin;
            
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
