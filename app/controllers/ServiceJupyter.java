/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import models.PropertiesFile;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Controller;
import static play.mvc.Controller.ctx;
import static play.mvc.Controller.request;
import play.mvc.Result;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;
import play.mvc.Security;
import views.formdata.CleanDataServiceForm;
import views.html.cleanDataService_config;

/**
 *
 * @author atovar
 */
public class ServiceJupyter extends Controller  {
    
    private static String pathService;
    static Form<CleanDataServiceForm> cleanDataServiceForm = form(CleanDataServiceForm.class);   

    static Logger logger = Logger.getLogger(ServiceJupyter.class);
    
    public static Result loadPathCleanDataService() {
        PropertiesFile pf = new PropertiesFile();
        pf.loadOtherPropertiesCleanData();
        String concat = (pf.getPathServiceCleanData());
        return ok(concat);
    }
    
    public static Result loadCleanDataService() {
        PropertiesFile pf = new PropertiesFile();
        pf.loadOtherPropertiesCleanData();
        String concat = (pf.getIpServiceCleanData()+"<;>"+pf.getPortServiceCleanData()+"<;>"+pf.getPortBootleCleanData());
        return ok(concat);
    }
    
    @Security.Authenticated(Secured.class)
    public static Result saveOtherConfiguration() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando otras configuraciones de limpieza de datos";
        generateLineLog(lineLog);

        Form<CleanDataServiceForm> filledFormOtherConfig = cleanDataServiceForm.bindFromRequest();

        BufferedWriter writer;
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();

        if (filledFormOtherConfig.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(cleanDataService_config.render("Otras Configuraciones", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormOtherConfig));
        } else {
            CleanDataServiceForm created = filledFormOtherConfig.get();
            try {
                File f = new File(pf.getPathFileModule() + "/temp/other_configuration_CleanData.properties");
                writer = new BufferedWriter(new FileWriter(f));
                writer.append("ipServiceCleanData=" + created.ipServiceCleanData+"\n");
                writer.append("portServiceCleanData=" + created.portServiceCleanData+"\n");
                writer.append("portBootleCleanData=" + created.portBootleCleanData);
                writer.flush();
                writer.close();

                return redirect(routes.Application.profile());
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result generateScript() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Generando script de python";
        generateLineLog(lineLog);

        String nameConfiguration = request().body().asText().split("<;>",-1)[0]; // data del ajax
        String type = request().body().asText().split("<;>",-1)[1]; // data del ajax

        int responseCode = getGenerateScript(nameConfiguration,type,"dashboards");
        if(responseCode==200){
            return ok("Success");
        }else{
            return ok("Failed");
        }       
    }
    
    public static int getGenerateScript(String nameConfiguration, String type, String admin){
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
    
    @Security.Authenticated(Secured.class)
    public static Result runPython() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Ejecutando script de python";
        generateLineLog(lineLog);

        String nameConfiguration = request().body().asText().split("<;>",-1)[0]; // data del ajax
        String type = request().body().asText().split("<;>",-1)[1]; // data del ajax

        int responseCode = getRunPythonScript(nameConfiguration,type,"dashboards");
        if(responseCode==200){
            return ok("Success");
        }else{
            return ok("Failed");
        }       
    }
    
    public static int getRunPythonScript(String nameConfiguration, String type, String admin){
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
    
    @Security.Authenticated(Secured.class)
    public static Result indexer() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Indexando...";
        generateLineLog(lineLog);

        String nameConfiguration = request().body().asText().split("<;>",-1)[0]; // data del ajax
        String type = request().body().asText().split("<;>",-1)[1]; // data del ajax

        int responseCode = getIndexer(nameConfiguration,type,"dashboards");
        if(responseCode==200){
            return ok("Success");
        }else{
            return ok("Failed");
        }
    }
    
    public static int getIndexer(String nameConfiguration, String type, String admin){
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
            }else if(type.equals("fileServers")){
                respFun = db.loadPathService(pf.getPathFileModule()+"/fileServers/connections.xml", nameConfiguration);
            }
            
            logger.info(respFun);
            
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
    
    public static void loadConnection(String pathConexiones, String selConexion){         
        try {
            File file = new File(pathConexiones);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Element docEle = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");
            
            for (int i=0; i<nodeLst.getLength(); i++) {
                Element conexion = (Element)docEle.getElementsByTagName("conexion").item(i);
                logger.info("a: "+conexion.getElementsByTagName("nomConexion").item(0).getTextContent());
                logger.info("b: "+selConexion);
                if (conexion.getElementsByTagName("nomConexion").item(0).getTextContent().equals(selConexion)) {
                    pathService = conexion.getElementsByTagName("pathService").item(0).getTextContent();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
    }
    
    public static void sendToSolr(String urlService, String path){
        try {
            logger.info(""+urlService);
            SolrClient client = new HttpSolrClient.Builder(urlService).build();
            ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update");
            req.addFile(new File(path),"application/csv");
            req.setParam("f.header", "true");
            req.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
            NamedList<Object> result = client.request(req);
            logger.info(""+result);
        } catch (IOException | SolrServerException ex) {
            logger.error(ex);
        }
    }
    
    public static Result checkPython(){
        String params = request().body().asText(); // data del ajax
        String nameConfiguration = params.split("<;>",-1)[0]; 
        String type = params.split("<;>",-1)[1];
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        logger.info(pf.getPathFileModule()+"/"+type+"/filesViews/"+nameConfiguration+"/"+nameConfiguration+".py");
        File python = new File(pf.getPathFileModule()+"/"+type+"/filesViews/"+nameConfiguration+"/"+nameConfiguration+".py");
        if(python.exists()){
            return ok("existe");
        }else{
            return ok("no_existe");
        }
    }
    
    public static Result checkOutput(){
        String params = request().body().asText(); // data del ajax
        String nameConfiguration = params.split("<;>",-1)[0]; 
        String type = params.split("<;>",-1)[1];
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        logger.info(pf.getPathFileModule()+"/"+type+"/filesViews/"+nameConfiguration+"/out/");
        File dir = new File(pf.getPathFileModule()+"/"+type+"/filesViews/"+nameConfiguration+"/out/");
        if(dir.exists()){
            String[] list = dir.list();
            if (list == null || list.length == 0) {
                return ok("no_existe");
            }else{
                return ok("existe");
            }
        }else{
            return ok("no_existe");
        }
    }
    
    @Security.Authenticated(Secured.class)
    public static Result syncNow() {
        String response="";
        try{
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }

            String nameConfiguration = request().body().asText().split("<;>",-1)[0]; // data del ajax
            String type = request().body().asText().split("<;>",-1)[1]; // data del ajax   

            String lineLog = ipAddress + "<;>" + username + "<;>Sincronizando inmediatamente "+nameConfiguration+" ...";
            generateLineLog(lineLog);

            String reqUrl;

            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesBD();

            String pathModuleDatabases = pf.getPathModuleDatabases();               
            String folderRoot = pf.getPathFileModule()+"/databases/";

            reqUrl = pathModuleDatabases+"executeProcess?selConexion="+URLEncoder.encode(nameConfiguration, "UTF-8")+"&folderRaiz="+URLEncoder.encode(folderRoot, "UTF-8");

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
                File python = new File(pf.getPathFileModule()+"/databases/filesViews/"+nameConfiguration+"/"+nameConfiguration+".py");
                if(python.exists()){
                    if(getGenerateScript(nameConfiguration,"databases","dashboards")==HttpURLConnection.HTTP_OK){
                        if(getRunPythonScript(nameConfiguration,"databases","dashboards")==HttpURLConnection.HTTP_OK){
                            getIndexer(nameConfiguration,"databases","dashboards");
                        }
                    }
                }
            }
            return ok("success");
        }catch (MalformedURLException ex) {
            logger.error(ex);
            return status(580,"Error:"+ex);
        } catch (IOException ex) {
            logger.error(ex);
            return status(580,"Error:"+ex);
        }
    }
    
    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
}
