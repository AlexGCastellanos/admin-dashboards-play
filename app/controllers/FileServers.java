/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import static controllers.Databases.generateLineLog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import models.FillLists;
import models.PropertiesFile;
import models.fileServers.ProgrammingTaskFS;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.quartz.SchedulerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Controller;
import static play.mvc.Controller.request;
import play.mvc.Result;
import static play.mvc.Results.ok;
import play.mvc.Security;
import views.formdata.FileServerForm;
import views.formdata.ProgTaskFileserversForm;
import views.html.fileServer_config;
import views.html.fileServer_prog_task;

/**
 *
 * @author atovar
 */
public class FileServers extends Controller {
    static Form<FileServerForm> FileServerForm = form(FileServerForm.class);
    static Form<ProgTaskFileserversForm> progTaskFileserversForm = form(ProgTaskFileserversForm.class);

    static Logger logger = Logger.getLogger(FileServers.class);
    
    @Security.Authenticated(Secured.class)
    public static Result saveConnection() {

        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Configuracion de servidor de archivos";
        generateLineLog(lineLog);
        
        Form<FileServerForm> filledFormFileServers = FileServerForm.bindFromRequest();

        if (filledFormFileServers.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(fileServer_config.render("Configuracio\u00F3n de Servidor de archivos",  Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()),filledFormFileServers));
        } else {
            try {
                FileServerForm created = filledFormFileServers.get();
                createRootConnectionFS(created.nomConnection, created.ipServerFS, created.portServerFS, created.pathFileServer, created.userFS, created.domainFS, created.passFS, created.pathService, created.hiddenCheckedCleanCore);
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                String path = pf.getPathFileModule() + "/fileServers/filesViews/"+created.nomConnection.trim();
                File dir = new File(path);
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setExecutable(true, false);
                dir.setWritable(true, false);        
                File fileJupyterDB = new File(path+"/serviceCleanData.ipynb");
                if(!fileJupyterDB.exists()){
                    File f = new File(pf.getPathFileModule() + "/fileServers/configBasic/serviceCleanData.ipynb");
                    File fPipes = new File(pf.getPathFileModule() + "/fileServers/configBasic/pipes_optimus.py");
                    File fDest = new File(path+"/"+created.nomConnection.trim()+".ipynb");
                    File fPipesDest = new File(path+"/pipes_optimus.py");
                    FileUtils.copyFile(f, fDest); 
                    FileUtils.copyFile(fPipes, fPipesDest); 
                    fDest.setReadable(true, false);
                    fDest.setExecutable(true, false);
                    fDest.setWritable(true, false);
                    fPipesDest.setReadable(true, false);
                    fPipesDest.setExecutable(true, false);
                    fPipesDest.setWritable(true, false);
                }
            } catch (IOException ex) {
                logger.error("Error: ",ex);
            }
            return redirect(routes.Application.profile());
        }
    }
    
    public static void createRootConnectionFS(String nomConnection, String ipServerFS, String portServerFS, String pathFileServer, String userFS, String domainFS, String passFS, String pathService, String hiddenCleanCore){
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConnections = pf.getPathFileModule() + "/fileServers/connections.xml";
            File file = new File(pathConnections);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");
            NodeList nodeLstInt;
            /*DateFormat readFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
            readFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
            DateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );*/
            String latestID = "";
            //Obtener ultimo Nodo
            if (nodeLst.getLength() - 1 >= 0) {
                nodeLstInt = nodeLst.item(nodeLst.getLength() - 1).getChildNodes();
                //Obtener ultimo ID
                latestID = nodeLstInt.item(0).getTextContent();
            } else if (nodeLst.getLength() - 1 < 0) {
                latestID = "0";
            }

            // Nos devuelve el nodo ra\u00EDz del documento XML.
            Node nodeRoot = doc.getDocumentElement();
            Element newNode = doc.createElement("conexion");

            int newID = Integer.parseInt(latestID) + 1;
            Element nodeID = doc.createElement("id");
            nodeID.setTextContent(String.valueOf(newID));

            Element nodeNomCon = doc.createElement("nomConexion");
            nodeNomCon.setTextContent(nomConnection);

            Element nodeIpServerFS = doc.createElement("ipServerFS");
            nodeIpServerFS.setTextContent(ipServerFS);
            
            Element nodePortServerFS = doc.createElement("portServerFS");
            nodePortServerFS.setTextContent(portServerFS);

            Element nodePathFileServer = doc.createElement("pathFileServer");
            nodePathFileServer.setTextContent(pathFileServer);

            Element nodeUserFS = doc.createElement("userFS");
            nodeUserFS.setTextContent(userFS);

            Element nodeDomainFS = doc.createElement("domainFS");
            nodeDomainFS.setTextContent(domainFS);
            
            Element nodePassFS = doc.createElement("passFS");
            nodePassFS.setTextContent(passFS);
            
            Element nodoPathS = doc.createElement("pathService");
            nodoPathS.setTextContent(pathService);  
            
            Element nodoHiddenCleanCore = doc.createElement("hiddenCleanCore");
            nodoHiddenCleanCore.setTextContent(hiddenCleanCore);  
            
            newNode.appendChild(nodeID);
            newNode.appendChild(nodeNomCon);
            newNode.appendChild(nodeIpServerFS);
            newNode.appendChild(nodePortServerFS);
            newNode.appendChild(nodePathFileServer);
            newNode.appendChild(nodeUserFS);
            newNode.appendChild(nodeDomainFS);
            newNode.appendChild(nodePassFS);   
            newNode.appendChild(nodoPathS); 
            newNode.appendChild(nodoHiddenCleanCore);
            nodeRoot.appendChild(newNode);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathConnections));
            transformer.transform(source, result);

        } catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            logger.error(e);
        }
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadConnections() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/fileServers/connections.xml";
            String xml = FileUtils.readFileToString(new File(pathXML));

            JSONObject jsonObject = XML.toJSONObject(xml);

            String jsonString = jsonObject.toString();

            return ok(jsonString);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result saveEditConnection() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando Configuraci\u00F3n servidor de archivos";
        generateLineLog(lineLog);

        String requestConnection = request().body().asText();
        String[] splitRequest = requestConnection.split("<;>",-1);
        
        String outValidate = validateFileServerIn(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], splitRequest[6]);
        if(outValidate.equals("Conectado")){
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConnections = pf.getPathFileModule() + "/fileServers/connections.xml";
            try {
                File file = new File(pathConnections);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList nodeLst = doc.getElementsByTagName("conexion");

                for (int i = 0; i < nodeLst.getLength(); i++) {
                    NodeList nodeLstInt = nodeLst.item(i).getChildNodes();
                    if (nodeLstInt.item(0).getTextContent().equals(splitRequest[0])) {
                        for (int j = 0; j < nodeLstInt.getLength(); j++) {
                            switch (j) {
                                case 2:
                                    nodeLstInt.item(j).setTextContent(splitRequest[1]);
                                    break;
                                case 3:
                                    nodeLstInt.item(j).setTextContent(splitRequest[2]);
                                    break;
                                case 4:
                                    nodeLstInt.item(j).setTextContent(splitRequest[3]);
                                    break;
                                case 5:
                                    nodeLstInt.item(j).setTextContent(splitRequest[4]);
                                    break;
                                case 6:
                                    nodeLstInt.item(j).setTextContent(splitRequest[5]);
                                    break;
                                case 7:
                                    nodeLstInt.item(j).setTextContent(splitRequest[6]);
                                    break;
                                case 8:
                                    nodeLstInt.item(j).setTextContent(splitRequest[7]);
                                    break;
                                case 9:
                                    nodeLstInt.item(j).setTextContent(splitRequest[8]);
                                    break;
                            }
                        }
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(pathConnections));
                transformer.transform(source, result);
                return ok(outValidate);
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
                logger.error(ex);
            }
        }else{
            return badRequest(outValidate);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteConnection() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Conexi\u00F3n de Servidor de archivos";
        generateLineLog(lineLog);

        String requestConnection = request().body().asText();
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathConnections = pf.getPathFileModule() + "/fileServers/connections.xml";
        try {
            File file = new File(pathConnections);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element e = (Element) nodeLst.item(i);
                String valueID = nodeLst.item(i).getFirstChild().getTextContent();
                if (valueID.equals(requestConnection)) {
                    e.getParentNode().removeChild(e);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathConnections));
            transformer.transform(source, result);

            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static String validateFileServerIn(String ipServerFS, String portServerFS, String pathFileServer, String userFS, String domainFS, String passFS){
        String outValidate="Conectado";
        SmbConfig config = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
                           .withTimeout(120, TimeUnit.SECONDS) // Timeout sets read, write and Transact timeouts (default is 60 seconds)
	                   .withSoTimeout(180, TimeUnit.SECONDS) // Socket timeout (default is 0 seconds)
	                   .build();
        SMBClient client = new SMBClient(config);
        Connection connection;
        try{
            if(portServerFS!=null && !portServerFS.isEmpty()){
                connection = client.connect(ipServerFS,Integer.parseInt(portServerFS));
            }else{
                connection = client.connect(ipServerFS);
            }
            
            pathFileServer = pathFileServer.replaceAll("\\\\", "/");
            String[] shareFolder = pathFileServer.split("/");
            String shareList = pathFileServer.replaceAll(shareFolder[0], "").substring(1);
            AuthenticationContext ac = new AuthenticationContext(userFS, passFS.toCharArray(), domainFS);
            Session session = connection.authenticate(ac);
            try (DiskShare share = (DiskShare) session.connectShare(shareFolder[0])) {
                if(!share.folderExists(shareList)){
                    outValidate = "No existe el directorio";
                }
            }
        }   catch (IOException | SMBApiException ex) {
            logger.error("Error: ",ex);
            outValidate = "Error conectando al servidor de archivos: "+ex.toString();
        }
        
        return outValidate;
    }
    
    public String validateFileServer(String ipServerFS, String portServerFS, String pathFileServer, String userFS, String domainFS, String passFS){
        String outValidate="Conectado";
        SmbConfig config = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
                           .withTimeout(120, TimeUnit.SECONDS) // Timeout sets read, write and Transact timeouts (default is 60 seconds)
	                   .withSoTimeout(180, TimeUnit.SECONDS) // Socket timeout (default is 0 seconds)
	                   .build();
        SMBClient client = new SMBClient(config);
        Connection connection;
        try{
            if(portServerFS!=null && !portServerFS.isEmpty()){
                connection = client.connect(ipServerFS,Integer.parseInt(portServerFS));
            }else{
                connection = client.connect(ipServerFS);
            }
            
            pathFileServer = pathFileServer.replaceAll("\\\\", "/");
            String[] shareFolder = pathFileServer.split("/");
            String shareList = pathFileServer.replaceAll(shareFolder[0], "").substring(1);
            AuthenticationContext ac = new AuthenticationContext(userFS, passFS.toCharArray(), domainFS);
            Session session = connection.authenticate(ac);
            try (DiskShare share = (DiskShare) session.connectShare(shareFolder[0])) {
                if(!share.folderExists(shareList)){
                    outValidate = "No existe el directorio";
                }
            }
        }   catch (IOException | SMBApiException ex) {
            logger.error("Error: ",ex);
            outValidate = "Error conectando al servidor de archivos: "+ex.toString();
        }
        
        return outValidate;
    }
    
    public LinkedHashMap<String, String> fillConnections(){
        try {
            LinkedHashMap<String, String> nomConnections = new LinkedHashMap<>();
            
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConnections = pf.getPathFileModule() + "/fileServers/connections.xml";
            File file = new File(pathConnections);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");
            
            for (int s = 0; s < nodeLst.getLength(); s++) {
                NodeList nodeLstInt = nodeLst.item(s).getChildNodes();
                for (int a = 0; a < nodeLstInt.getLength(); a++) {
                    switch (a) {
                        case 1:
                            if(!nodeLstInt.item(a).getTextContent().equals("")){
                                nomConnections.put(nodeLstInt.item(a).getTextContent(),nodeLstInt.item(a).getTextContent());
                            }else{
                                nomConnections.put("","");
                            }     
                        break;
                    }
                }
            }
            return nomConnections;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result saveProgTaskFS(){
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Guardando Programaci\u00F3n de tarea de Servidor de archivos";
        generateLineLog(lineLog);
        
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
            
        Form<ProgTaskFileserversForm> filledFormProgTaskFS = progTaskFileserversForm.bindFromRequest();
        
        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        FileServers fileServers = new FileServers();
        arrListDaysWeek = fillLists.fillDayWeek();
        
        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();
        
        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();
        
        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();
        
        LinkedHashMap<String, String> nomConnections;
        nomConnections = fileServers.fillConnections();
        
        if (filledFormProgTaskFS.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(fileServer_prog_task.render("Programaci\u00F3n Tarea Servidor de archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormProgTaskFS, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
        } else {
            try {
                String folderRoot = pf.getPathFileModule()+"/fileServers/";
                String pathTasks = folderRoot+"task.xml";
                ProgTaskFileserversForm created = filledFormProgTaskFS.get();
                createRootProgTask(pathTasks, created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, created.hourTask, created.minuteTask, created.selConnection);
                new ProgrammingTaskFS(created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, Integer.parseInt(created.hourTask), Integer.parseInt(created.minuteTask), created.selConnection, folderRoot).iniciarTarea();
                return redirect(routes.Application.profile());
            } catch (SchedulerException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
    
    public static void createRootProgTask(String pathTasks, String perMinutesTask, String perHoursTask, String dayTask, String dayWeekTask, String dayMonthTask, String hourTask, String minuteTask, String selConnection){
         try{
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();           
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            NodeList nodeLstInt;
            String latestID = "";
            //Obtener ultimo Nodo
            if(nodeLst.getLength()-1>=0){
                nodeLstInt = nodeLst.item(nodeLst.getLength()-1).getChildNodes();
                //Obtener ultimo ID
                latestID = nodeLstInt.item(0).getTextContent();
            }else if(nodeLst.getLength()-1<0){
                latestID = "0";
            }   
            
            // Nos devuelve el nodo ra\u00EDz del documento XML.
            Node newRoot = doc.getDocumentElement();
            Element newNodo = doc.createElement("tarea");

            int newID = Integer.parseInt(latestID)+1;
            Element nodeID = doc.createElement("id");
            nodeID.setTextContent(String.valueOf(newID));
            
            Element nodePerMinutes = doc.createElement("porMinutos");
            nodePerMinutes.setTextContent(perMinutesTask);
            
            Element nodePerHours = doc.createElement("porHoras");
            nodePerHours.setTextContent(perHoursTask);
            
            Element nodeDay = doc.createElement("dia");
            nodeDay.setTextContent(dayTask);
            
            Element nodeDayWeek = doc.createElement("diaSemana");
            nodeDayWeek.setTextContent(dayWeekTask);
            
            Element nodeDayMonth = doc.createElement("diaMes");
            nodeDayMonth.setTextContent(dayMonthTask);
            
            Element nodeHour = doc.createElement("hora");
            nodeHour.setTextContent(hourTask);
            
            Element nodeMinute = doc.createElement("minuto");
            nodeMinute.setTextContent(minuteTask);
            
            Element nodeConnec = doc.createElement("conexion");
            nodeConnec.setTextContent(selConnection);          

            newNodo.appendChild(nodeID);
            newNodo.appendChild(nodePerMinutes);
            newNodo.appendChild(nodePerHours);
            newNodo.appendChild(nodeDay);
            newNodo.appendChild(nodeDayWeek);
            newNodo.appendChild(nodeDayMonth);
            newNodo.appendChild(nodeHour);
            newNodo.appendChild(nodeMinute);
            newNodo.appendChild(nodeConnec);
            newRoot.appendChild(newNodo);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);
            
        }catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            logger.error(e);
        }
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadProgTaskFS() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/fileServers/task.xml";
            String xml = FileUtils.readFileToString(new File(pathXML));

            JSONObject jsonObject = XML.toJSONObject(xml);

            String jsonString = jsonObject.toString();

            return ok(jsonString);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result editProgTaskFS(){
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Modificando Programaci\u00E3n de tarea de Servidor de archivos";
        generateLineLog(lineLog);
        
        String requestTask = request().body().asText();
        String[] splitRequest = requestTask.split("<;>",-1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule()+"/fileServers/task.xml";
        try{
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();            
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            
            for (int i=0; i<nodeLst.getLength(); i++) {
                NodeList nodeLstInt = nodeLst.item(i).getChildNodes();
                if(nodeLstInt.item(0).getTextContent().equals(splitRequest[0])){
                    for (int j = 0; j < nodeLstInt.getLength(); j++) {
                        
                        switch (j) {
                            case 1:
                                nodeLstInt.item(j).setTextContent(splitRequest[1]);
                                break;
                            case 2:
                                nodeLstInt.item(j).setTextContent(splitRequest[2]);
                                break;
                            case 3:
                                nodeLstInt.item(j).setTextContent(splitRequest[3]);
                                break; 
                            case 4:
                                nodeLstInt.item(j).setTextContent(splitRequest[4]);
                                break;
                            case 5:
                                nodeLstInt.item(j).setTextContent(splitRequest[5]);
                                break;
                            case 6:
                                nodeLstInt.item(j).setTextContent(splitRequest[6]);
                                break;
                            case 7:
                                nodeLstInt.item(j).setTextContent(splitRequest[7]);
                                break;
                            case 8:
                                nodeLstInt.item(j).setTextContent(splitRequest[8]);
                                break;
                        }
                    }
                    String folderRoot = pf.getPathFileModule()+"/fileServers/";
                    new ProgrammingTaskFS(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], Integer.parseInt(splitRequest[6]), Integer.parseInt(splitRequest[7]), splitRequest[8], folderRoot).iniciarTarea();
                }      
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);
            return redirect(routes.Application.profile());
        }catch (ParserConfigurationException pce) {
        } catch (SAXException | IOException | TransformerException | SchedulerException ex) { 
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteProgTaskFS() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Tareas de Servidor de archivos";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule() + "/fileServers/task.xml";
        try {
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element e = (Element) nodeLst.item(i);
                String valueID = nodeLst.item(i).getFirstChild().getTextContent();
                if (valueID.equals(requestTask)) {
                    e.getParentNode().removeChild(e);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);

            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static Result loadConfigurationsFS(){         
        String concatArr="";
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        ArrayList<String> arrConfigurations = new ArrayList<>();
        File fRaiz = new File(pf.getPathFileModule() + "/fileServers/filesViews/");
        if(fRaiz.exists() && fRaiz.isDirectory()){
            String[] files = fRaiz.list();
            if (files.length > 0) {
               for (String archivo : files) {
                    File f = new File(pf.getPathFileModule() + "/fileServers/filesViews/"+archivo);
                    if(f.isDirectory() && !f.isHidden())
                        arrConfigurations.add(archivo);
               }
           }
        }
        
        for(int i=0; i<arrConfigurations.size(); i++){
            concatArr += arrConfigurations.get(i)+",";
        }
        
        return ok(concatArr);
    }
}
