/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import models.DatabasesModel;
import models.PropertiesFile;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import models.FillLists;
import models.databases.ProgrammingTaskBD;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.XML;
import org.quartz.SchedulerException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;
import views.formdata.DatabasesForm;
import views.formdata.ProgTaskDatabasesForm;
import views.html.databases_config;
import views.html.databases_prog_task;

/**
 *
 * @author atovar
 */
public class Databases extends Controller {
    static Form<DatabasesForm> DatabasesForm = form(DatabasesForm.class);
    static Form<ProgTaskDatabasesForm> progTaskDatabasesForm = form(ProgTaskDatabasesForm.class);

    static Logger logger = Logger.getLogger(Databases.class);

    @Security.Authenticated(Secured.class)
    public static Result saveConnection() {

        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Configuracion de Base de datos";
        generateLineLog(lineLog);
        
        Form<DatabasesForm> filledFormDatabases = DatabasesForm.bindFromRequest();

        HashMap<String, String> arrDatabases;
        DatabasesModel databases = new DatabasesModel();
        arrDatabases = databases.fillDatabases();

        if (filledFormDatabases.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(databases_config.render("Configuracion Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormDatabases, arrDatabases));
        } else {
            try {
                DatabasesForm created = filledFormDatabases.get();
                createRootConnection(created.nomConnection, created.separatorBD, created.query, created.fieldURL, created.date1, created.date2, created.fieldDate, created.databases, created.serverBD, created.portBD, created.nameBD, created.instanceBD, created.userBD, created.passBD, created.disFracView, created.numFracView, created.fieldOrderBy, created.pathService, created.hiddenCheckedCleanCore ,created.diascampoFecha,created.cleanType);
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                String path = pf.getPathFileModule() + "/databases/filesViews/"+created.nomConnection.trim();
                File dir = new File(path);
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setExecutable(true, false);
                dir.setWritable(true, false);        
                File fileJupyterDB = new File(path+"/serviceCleanData.ipynb");
                if(!fileJupyterDB.exists()){
                    File f = new File(pf.getPathFileModule() + "/databases/configBasic/serviceCleanData.ipynb");
                    File fPipes = new File(pf.getPathFileModule() + "/databases/configBasic/pipes_optimus.py");
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

    public static void createRootConnection(String nomConnection, String separatorBD, String query, String fieldURL, String date1, String date2, String fieldDate, String databases, String serverBD, String portBD, String nameBD, String instanceBD, String userBD, String passBD, String disFracView, String numFracView, String fieldOrderBy, String pathService, String hiddenCleanCore,String diascampoFecha,String cleanType) {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConnections = pf.getPathFileModule() + "/databases/connections.xml";
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

            Element nodeSeparatorBD = doc.createElement("separadorBD");
            nodeSeparatorBD.setTextContent(separatorBD);
            
            Element nodeQuery = doc.createElement("query");
            nodeQuery.setTextContent(query);

//            Element nodeFieldURL = doc.createElement("campoURL");
//            nodeFieldURL.setTextContent(fieldURL);

            Element nodeDateIni = doc.createElement("fechaIni");
            if (!date1.equals("")) {
                nodeDateIni.setTextContent(date1 + " 00:00:00");
            } else {
                nodeDateIni.setTextContent(date1);
            }

            Element nodeDateFin = doc.createElement("fechaFin");
            if (!date2.equals("")) {
                nodeDateFin.setTextContent(date2 + " 23:59:59");
            } else {
                nodeDateFin.setTextContent(date2);
            }

            Element nodeFieldDate = doc.createElement("campoFecha");
            nodeFieldDate.setTextContent(fieldDate);
            
            Element nodeTipoIndexacion = doc.createElement("tipoIndexacion");
            nodeTipoIndexacion.setTextContent(cleanType);
            
            Element nodeNumFieldDate = doc.createElement("numDays");
            nodeNumFieldDate.setTextContent(diascampoFecha);
            
            Element nodeBD = doc.createElement("baseDatos");
            nodeBD.setTextContent(databases);

            Element nodeServerBD = doc.createElement("servidorBD");
            nodeServerBD.setTextContent(serverBD);

            Element nodePortBD = doc.createElement("puertoBD");
            nodePortBD.setTextContent(portBD);

            Element nodeNomBD = doc.createElement("nombreBD");
            nodeNomBD.setTextContent(nameBD);

            Element nodeInstBD = doc.createElement("instanciaBD");
            nodeInstBD.setTextContent(instanceBD);

            Element nodeUser = doc.createElement("userBD");
            nodeUser.setTextContent(userBD);

            Element nodePass = doc.createElement("passBD");
            nodePass.setTextContent(passBD);
            
            Element nodeDisFracView = doc.createElement("disFracView");
            nodeDisFracView.setTextContent(disFracView);
            
            Element nodeNumFracView = doc.createElement("numFracView");
            nodeNumFracView.setTextContent(numFracView);
            
            Element nodeFieldOrderBy = doc.createElement("campoOrderBy");
            nodeFieldOrderBy.setTextContent(fieldOrderBy);
            
            Element nodoPathS = doc.createElement("pathService");
            nodoPathS.setTextContent(pathService); 
            
            Element nodoHiddenCleanCore = doc.createElement("hiddenCleanCore");
            nodoHiddenCleanCore.setTextContent(hiddenCleanCore);
            
            newNode.appendChild(nodeID);
            newNode.appendChild(nodeNomCon);
            newNode.appendChild(nodeSeparatorBD);
            newNode.appendChild(nodeQuery);
//            newNode.appendChild(nodeFieldURL);
            newNode.appendChild(nodeDateIni);
            newNode.appendChild(nodeDateFin);
            newNode.appendChild(nodeFieldDate);
            newNode.appendChild(nodeTipoIndexacion);
            newNode.appendChild(nodeNumFieldDate);
            newNode.appendChild(nodeBD);
            newNode.appendChild(nodeServerBD);
            newNode.appendChild(nodePortBD);
            newNode.appendChild(nodeNomBD);
            newNode.appendChild(nodeInstBD);
            newNode.appendChild(nodeUser);
            newNode.appendChild(nodePass);
            newNode.appendChild(nodeDisFracView);
            newNode.appendChild(nodeNumFracView);
            newNode.appendChild(nodeFieldOrderBy);   
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
            String pathXML = pf.getPathFileModule() + "/databases/connections.xml";
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
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando Base de datos";
        generateLineLog(lineLog);

        String requestConnection = request().body().asText();
        String[] splitRequest = requestConnection.split("<;!;>",-1);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathConnections = pf.getPathFileModule() + "/databases/connections.xml";
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
                                nodeLstInt.item(j).setTextContent(splitRequest[1]);//separadorBD
                                break;
                            case 3:
                                nodeLstInt.item(j).setTextContent(splitRequest[2]);//query
                                break;
                            
                            case 4:
                                if(!splitRequest[3].equals("") || !splitRequest[3].isEmpty()){//fechaIni
                                    nodeLstInt.item(j).setTextContent(splitRequest[3] + " 00:00:00");
                                }else{
                                    nodeLstInt.item(j).setTextContent(splitRequest[3]);
                                }
                                break;
                            case 5:
                                if(!splitRequest[4].equals("") || !splitRequest[4].isEmpty()){//fechaFin
                                    nodeLstInt.item(j).setTextContent(splitRequest[4] + " 23:59:59");
                                }else{
                                    nodeLstInt.item(j).setTextContent(splitRequest[4]);
                                }
                                break;
                            case 6:
                                nodeLstInt.item(j).setTextContent(splitRequest[5]);//campoFecha
                                break;
                            case 7:
                                nodeLstInt.item(j).setTextContent(splitRequest[6]);//tipoIndexacion
                                break;
                            case 8:
                                nodeLstInt.item(j).setTextContent(splitRequest[7]);//numDays
                                break;
                            case 9:
                                nodeLstInt.item(j).setTextContent(splitRequest[8]);//baseDatos
                                break;
                            case 10:
                                nodeLstInt.item(j).setTextContent(splitRequest[9]);//servidorBD
                                break;
                            case 11:
                                nodeLstInt.item(j).setTextContent(splitRequest[10]);
                                break;
                            case 12:
                                nodeLstInt.item(j).setTextContent(splitRequest[11]);
                                break;
                            case 13:
                                nodeLstInt.item(j).setTextContent(splitRequest[12]);
                                break;
                            case 14:
                                nodeLstInt.item(j).setTextContent(splitRequest[13]);
                                break;
                            case 15:
                                nodeLstInt.item(j).setTextContent(splitRequest[14]);
                                break;
                            case 16:
                                nodeLstInt.item(j).setTextContent(splitRequest[15]);
                                break;
                            case 17:
                                nodeLstInt.item(j).setTextContent(splitRequest[16]);
                                break;
                            case 18:
                                nodeLstInt.item(j).setTextContent(splitRequest[17]);
                                break;
                            case 19:
                                nodeLstInt.item(j).setTextContent(splitRequest[18]);
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
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
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
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Conexi\u00F3n Base de datos";
        generateLineLog(lineLog);

        String idConexion = request().body().asText().split("<;>")[0];
        String nameConfiguration = request().body().asText().split("<;>")[1];
        
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathConnections = pf.getPathFileModule() + "/databases/connections.xml";
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
                if (valueID.equals(idConexion)) {
                    e.getParentNode().removeChild(e);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathConnections));
            transformer.transform(source, result);
            
            //Eliminar directorio de la configuracion
            File dirConfig = new File(pf.getPathFileModule() + "/databases/filesViews/"+nameConfiguration);
            FileUtils.deleteDirectory(dirConfig);
            
            //Eliminar tarea (Si existe)
            deleteTasksBD(ipAddress,username,nameConfiguration);

            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static void deleteTasksBD(String ipAddress, String username, String nameConfiguration) {
        /*Lineas para Guardar en LOG */
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Programacion de tarea de Base de datos";
        generateLineLog(lineLog);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTareas = pf.getPathFileModule() + "/databases/task.xml";
        try {
            File file = new File(pathTareas);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Element docEle = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            
            for (int i=0; i<nodeLst.getLength(); i++) {
                Element tarea = (Element)docEle.getElementsByTagName("tarea").item(i);
                if (tarea.getElementsByTagName("conexion").item(0).getTextContent().equals(nameConfiguration)) {
                    Element e = (Element) nodeLst.item(i);
                    e.getParentNode().removeChild(e);  
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTareas));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
    }
    
    public LinkedHashMap<String, String> fillConnections(){
        try {
            LinkedHashMap<String, String> nomConnections = new LinkedHashMap<>();
            
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConnections = pf.getPathFileModule() + "/databases/connections.xml";
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
    public static Result saveProgTaskBD(){
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Guardando Programaci\u00F3n de tarea de Bases de datos";
        generateLineLog(lineLog);
        
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
            
        Form<ProgTaskDatabasesForm> filledFormProgTaskBD = progTaskDatabasesForm.bindFromRequest();
        
        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        Databases databases = new Databases();
        arrListDaysWeek = fillLists.fillDayWeek();
        
        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();
        
        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();
        
        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();
        
        LinkedHashMap<String, String> nomConnections;
        nomConnections = databases.fillConnections();
        
        if (filledFormProgTaskBD.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(databases_prog_task.render("Programaci\u00F3n Tarea Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormProgTaskBD, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
        } else {
            try {
                String folderRoot = pf.getPathFileModule()+"/databases/";
                String pathTasks = folderRoot+"task.xml";
                ProgTaskDatabasesForm created = filledFormProgTaskBD.get();
                createRootProgTask(pathTasks, created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, created.hourTask, created.minuteTask, created.selConnection);
                new ProgrammingTaskBD(created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, Integer.parseInt(created.hourTask), Integer.parseInt(created.minuteTask), created.selConnection, folderRoot).iniciarTarea();
                return redirect(routes.Application.profile());
            } catch (SchedulerException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
        
    public static String checkTask(LinkedHashMap<String, String> tasks, String connection){
        if(tasks.containsKey(connection)){
            return connection.trim();
        }
        return "";
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
    public static Result loadProgTaskBD() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/databases/task.xml";
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
    public static Result editTaskDatabases(){
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Modificando Programaci\u00E3n de tarea de Bases de datos";
        generateLineLog(lineLog);
        
        String requestTask = request().body().asText();
        String[] splitRequest = requestTask.split("<;>",-1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule()+"/databases/task.xml";
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
                    String folderRoot = pf.getPathFileModule()+"/databases/";
                    new ProgrammingTaskBD(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], Integer.parseInt(splitRequest[6]), Integer.parseInt(splitRequest[7]), splitRequest[8], folderRoot).iniciarTarea();
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
    public static Result deleteTasksBD() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Tarea de Base de datos";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule() + "/databases/task.xml";
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
    
    public static LinkedHashMap<String, String> fillTasks(){
        try {
            LinkedHashMap<String, String> nomTasks = new LinkedHashMap<>();
            
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathTasks = pf.getPathFileModule() + "/databases/task.xml";
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            
            for (int s = 0; s < nodeLst.getLength(); s++) {
                NodeList nodeLstInt = nodeLst.item(s).getChildNodes();
                for (int a = 0; a < nodeLstInt.getLength(); a++) {
                    switch (a) {
                        case 6:
                            if(!nodeLstInt.item(a).getTextContent().equals("")){
                                nomTasks.put(nodeLstInt.item(a).getTextContent(),nodeLstInt.item(a).getTextContent());
                            }else{
                                nomTasks.put("","");
                            }     
                        break;
                    }
                }
            }
            return nomTasks;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public String loadPathService(String pathConexiones, String selConexion){         
        String pathService="",hiddenCleanCore="";
        try {
            File file = new File(pathConexiones);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Element docEle = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");
            
            for (int i=0; i<nodeLst.getLength(); i++) {
                //System.out.println("nodo: "+nodeToString(docEle.getElementsByTagName("conexion").item(i)));
                Element conexion = (Element)docEle.getElementsByTagName("conexion").item(i);
                if (conexion.getElementsByTagName("nomConexion").item(0).getTextContent().equals(selConexion)) {
                    pathService = conexion.getElementsByTagName("pathService").item(0).getTextContent();
                    hiddenCleanCore = conexion.getElementsByTagName("hiddenCleanCore").item(0).getTextContent();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
        return pathService+"<;>"+hiddenCleanCore;
    }
    
    public static Result loadConfigurationsDB(){         
        String concatArr="";
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        ArrayList<String> arrConfigurations = new ArrayList<>();
        File fRaiz = new File(pf.getPathFileModule() + "/databases/filesViews/");
        if(fRaiz.exists() && fRaiz.isDirectory()){
            String[] files = fRaiz.list();
            if (files.length > 0) {
               for (String archivo : files) {
                    File f = new File(pf.getPathFileModule() + "/databases/filesViews/"+archivo);
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
    
    public static Result loadConfigsDBReady(){         
        String concatArr="";
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        ArrayList<String> arrConfigurations = new ArrayList<>();
        File fRaiz = new File(pf.getPathFileModule() + "/databases/filesViews/");
        if(fRaiz.exists() && fRaiz.isDirectory()){
            String[] files = fRaiz.list();
            if (files.length > 0) {
               for (String directoryDB : files) {
                    File f = new File(pf.getPathFileModule() + "/databases/filesViews/"+directoryDB);
                    if(f.isDirectory() && !f.isHidden()){
                        File python = new File(pf.getPathFileModule()+"/databases/filesViews/"+directoryDB+"/"+directoryDB+".py");
                        if(python.exists()){
                            arrConfigurations.add(directoryDB);
                        }
                    }
               }
           }
        }
        
        for(int i=0; i<arrConfigurations.size(); i++){
            concatArr += arrConfigurations.get(i)+",";
        }
        
        return ok(concatArr);
    }
    
    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
}
