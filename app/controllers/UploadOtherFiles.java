package controllers;

import java.io.BufferedReader;
import views.html.upLoadOthersFile;
import views.html.upLoadOthersFile_new_config;
import views.html.upLoadOthersFile_prog_tarea;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import models.PropertiesFile;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import views.formdata.uploadFileForm;
import org.apache.log4j.Logger;
import org.apache.commons.io.FilenameUtils;
//////////////////
import java.io.FileInputStream;
import java.io.*;
import org.apache.poi.ss.usermodel.*;
import java.util.Iterator;
import java.io.FileWriter;
import com.opencsv.CSVWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.google.common.hash.Hashing;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import models.ConfigurationsOthersFiles;
import models.ProgTarea;
import models.othersFiles.ProgramacionTareaOthersFiles;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.json.XML;
import org.quartz.SchedulerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static play.mvc.Controller.request;
import views.formdata.ProgTareaOthersFilesForm;
import views.formdata.uploadFileIndexerForm;

public class UploadOtherFiles extends Controller {

    static Form<uploadFileForm> uploadFileForm = form(uploadFileForm.class);
    static Form<uploadFileIndexerForm> uploadFileIndexerForm = form(uploadFileIndexerForm.class);
    static Form<ProgTareaOthersFilesForm> progTareaOthersFilesForm = form(ProgTareaOthersFilesForm.class);

    static Logger logger = Logger.getLogger(UploadOtherFiles.class);
    
    @Security.Authenticated(Secured.class)
    public static Result saveAddScriptOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Agregando script para limpieza de datos";
        generateLineLog(lineLog);
        
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadOtherPropertiesBD();
            pf.loadProperties();
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart pathFileClean = body.getFile("pathFileClean");
            String fileNameClean="";
            if (pathFileClean != null) {
                fileNameClean = pathFileClean.getFilename();
                File fileClean = pathFileClean.getFile();
                byte[] fileContentClean = Files.readAllBytes(fileClean.toPath());
                File fPython = new File(pf.getPathFileModule() + "filesToUp/scripts/" + fileNameClean);
                try (OutputStream outClean = new FileOutputStream(fPython)) {
                    outClean.write(fileContentClean);
                    outClean.flush();
                }
                fPython.setReadable(true, false);
                fPython.setExecutable(true, false);
                fPython.setWritable(true, false);
            }
        } catch (IOException ex) {
            logger.error("Error: ", ex);
        }
        return redirect(routes.Application.profile());
    }
    
    @Security.Authenticated(Secured.class)
    public static Result saveNewConfigOthersFiles() {

        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Configuracion de otros tipos de archivos";
        generateLineLog(lineLog);

        Form<uploadFileForm> filledFormUploadFileForm = uploadFileForm.bindFromRequest();

        HashMap<String, String> arrScripts;
        arrScripts = UploadOtherFiles.fillScripts();

        if (filledFormUploadFileForm.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(upLoadOthersFile_new_config.render("Crear configuracion otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormUploadFileForm, arrScripts));
        } else {
            uploadFileForm created = filledFormUploadFileForm.get();
            String nameScript = created.nameScript != null ? created.nameScript : "clean_data_excel.py";
            crearRaizConexion(created.nomConexion, created.typeUpload, created.pathFileURL, created.pathFileSFTPHost, created.pathFileSFTPUsername, created.pathFileSFTPPass, created.pathFileSFTP, created.checkedCleanDataExcel, nameScript, created.checkedCleanDataCore, created.buscador);
            try{
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                String path = pf.getPathFileModule() + "/filesToUp/configs/" + created.nomConexion.trim();
                File dir = new File(path);
                dir.mkdirs();
                dir.setReadable(true, false);
                dir.setExecutable(true, false);
                dir.setWritable(true, false);
                File fileJupyterOA = new File(path + "/serviceCleanData.ipynb");
                if (!fileJupyterOA.exists()) {
                    File f = new File(pf.getPathFileModule() + "/filesToUp/configBasic/serviceCleanData.ipynb");
                    File fPipes = new File(pf.getPathFileModule() + "/filesToUp/configBasic/pipes_optimus.py");
                    File fDest = new File(path + "/" + created.nomConexion.trim() + ".ipynb");
                    File fPipesDest = new File(path + "/pipes_optimus.py");
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
                logger.error("Error: ", ex);
            }
            return redirect(routes.Application.profile());
        }
    }
    
    public static void crearRaizConexion(String nomConexion, String typeUpload, String pathFileURL, String pathFileSFTPHost, String pathFileSFTPUsername, String pathFileSFTPPass, String pathFileSFTP, String checkedCleanDataExcel, String nameScript, String checkedCleanDataCore, String buscador) {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathConexiones = pf.getPathFileModule() + "/filesToUp/connections.xml";
            File file = new File(pathConexiones);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("conexion");
            NodeList nodeLstInt;
            String ultimoID = "";
            //Obtener ultimo Nodo
            if (nodeLst.getLength() - 1 >= 0) {
                nodeLstInt = nodeLst.item(nodeLst.getLength() - 1).getChildNodes();
                //Obtener ultimo ID
                ultimoID = nodeLstInt.item(0).getTextContent();
            } else if (nodeLst.getLength() - 1 < 0) {
                ultimoID = "0";
            }

            // Nos devuelve el nodo ra\u00EDz del documento XML.
            Node nodoRaiz = doc.getDocumentElement();
            Element nuevoNodo = doc.createElement("conexion");

            int nuevoID = Integer.parseInt(ultimoID) + 1;
            Element nodoID = doc.createElement("id");
            nodoID.setTextContent(String.valueOf(nuevoID));

            Element nodoNomCon = doc.createElement("nomConexion");
            nodoNomCon.setTextContent(nomConexion);

            Element nodoTypeUpload = doc.createElement("typeUpload");
            nodoTypeUpload.setTextContent(typeUpload);
            
            Element nodoPathFileURL = doc.createElement("pathFileURL");
            nodoPathFileURL.setTextContent(pathFileURL);
            
            Element nodoPathFileSFTPHost = doc.createElement("pathFileSFTPHost");
            nodoPathFileSFTPHost.setTextContent(pathFileSFTPHost);

            Element nodoPathFileSFTPUsername = doc.createElement("pathFileSFTPUsername");
            nodoPathFileSFTPUsername.setTextContent(pathFileSFTPUsername);

            Element nodoPathFileSFTPPass = doc.createElement("pathFileSFTPPass");
            nodoPathFileSFTPPass.setTextContent(pathFileSFTPPass);

            Element nodoPathFileSFTP = doc.createElement("pathFileSFTP");
            nodoPathFileSFTP.setTextContent(pathFileSFTP);

            Element nodoCheckedCleanDataExcel = doc.createElement("checkedCleanDataExcel");
            nodoCheckedCleanDataExcel.setTextContent(checkedCleanDataExcel);

            Element nodoNameScript = doc.createElement("nameScript");
            nodoNameScript.setTextContent(nameScript);

            Element nodoCheckedCleanDataCore = doc.createElement("checkedCleanDataCore");
            nodoCheckedCleanDataCore.setTextContent(checkedCleanDataCore);

            Element nodoBuscador = doc.createElement("buscador");
            nodoBuscador.setTextContent(buscador);

            nuevoNodo.appendChild(nodoID);
            nuevoNodo.appendChild(nodoNomCon);
            nuevoNodo.appendChild(nodoTypeUpload);
            nuevoNodo.appendChild(nodoPathFileURL);
            nuevoNodo.appendChild(nodoPathFileSFTPHost);
            nuevoNodo.appendChild(nodoPathFileSFTPUsername);
            nuevoNodo.appendChild(nodoPathFileSFTPPass);
            nuevoNodo.appendChild(nodoPathFileSFTP);
            nuevoNodo.appendChild(nodoCheckedCleanDataExcel);
            nuevoNodo.appendChild(nodoNameScript);
            nuevoNodo.appendChild(nodoCheckedCleanDataCore);
            nuevoNodo.appendChild(nodoBuscador);
            nodoRaiz.appendChild(nuevoNodo);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathConexiones));
            transformer.transform(source, result);

        } catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            logger.error(e);
        }

    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public static Result loadConexionesOthersFiles() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/filesToUp/connections.xml";
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
    public static Result saveScriptConOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando configuracion Otros archivos";
        generateLineLog(lineLog);
        
        String requestConexion = request().body().asText();
        String[] splitRequest = requestConexion.split("<;>", -1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathConexiones = pf.getPathFileModule() + "/filesToUp/connections.xml";
                
        try {
            File file = new File(pathConexiones);
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
                            case 10:
                                nodeLstInt.item(j).setTextContent(splitRequest[9]);
                                break;
                            case 11:
                                nodeLstInt.item(j).setTextContent(splitRequest[10]);
                                break;
                        }
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathConexiones));
            transformer.transform(source, result);
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteConOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Configuraciones de otros archivos";
        generateLineLog(lineLog);

        String idConexion = request().body().asText().split("<;>")[0];
        String nameConfiguration = request().body().asText().split("<;>")[1];

        logger.info(request().body().asText().split("<;>")[0] + "<;>" + request().body().asText().split("<;>")[1]);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathConexiones = pf.getPathFileModule() + "/filesToUp/connections.xml";
        try {
            File file = new File(pathConexiones);
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
            StreamResult result = new StreamResult(new File(pathConexiones));
            transformer.transform(source, result);

            //Eliminar directorio de la configuracion
            File dirConfig = new File(pf.getPathFileModule() + "/filesToUp/configs/" + nameConfiguration);
            if (dirConfig.exists()) {
                FileUtils.deleteDirectory(dirConfig);
            }

            //Eliminar tarea (Si existe)
            deleteTaskOthersFiles(ipAddress, username, nameConfiguration);

            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static void deleteTaskOthersFiles(String ipAddress, String username, String nameConfiguration) {
        /*Lineas para Guardar en LOG */
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Programacion de otros archivos";
        generateLineLog(lineLog);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTareas = pf.getPathFileModule() + "/filesToUp/task.xml";
        try {
            File file = new File(pathTareas);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            Element docEle = doc.getDocumentElement();
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element tarea = (Element) docEle.getElementsByTagName("tarea").item(i);
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

    @Security.Authenticated(Secured.class)
    public static Result upLoadOtherFile() {
        /////////////////////////////////        
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Indexando Directamente Un Archivo";
        generateLineLog(lineLog);

        Form<uploadFileIndexerForm> filledFormConfig = uploadFileIndexerForm.bindFromRequest();
        HashMap<String, String> arrScripts;
        arrScripts = fillScripts();

        if (filledFormConfig.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(upLoadOthersFile.render("Carga De Archivos.", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), arrScripts, filledFormConfig));
        } else {
            try {
                uploadFileIndexerForm created = filledFormConfig.get();
                String tipo = created.typeUploadIndexer;
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                pf.loadOtherPropertiesBD();
                logger.info("Indexar excel  a traves de " + tipo);

                Http.MultipartFormData body = request().body().asMultipartFormData();
                
                String filePath="";
                String pathSolr = loadSolrDataBusc(created.buscador);
                String nameScript = created.nameScript != null ? created.nameScript : "clean_data_excel.py";

                switch (tipo) {
                    case "file":
                        {
                            String fileNameToUp;
                            Http.MultipartFormData.FilePart fileToUp = body.getFile("pathFile");
                            if (fileToUp != null) {
                                fileNameToUp = fileToUp.getFilename();
                                File fileCrt = fileToUp.getFile();
                                byte[] fileContentCrt = Files.readAllBytes(fileCrt.toPath());
                                File fPython = new File(pf.getPathFileModule() + "filesToUp/tempFiles/local/" + fileNameToUp);
                                try (OutputStream outCrt = new FileOutputStream(fPython)) {
                                    outCrt.write(fileContentCrt);
                                    outCrt.flush();
                                }
                                fPython.setReadable(true, false);
                                fPython.setExecutable(true, false);
                                fPython.setWritable(true, false);
                                
                                filePath = pf.getPathFileModule() + "filesToUp/tempFiles/local/" + fileNameToUp;
                            }       
                            break;
                        }
                    case "http":
                        {
                            String fileNameToUp = uploadFromUrl(created.pathFileURL,"");
                            filePath = pf.getPathFileModule() + "filesToUp/tempFiles/urls/" + fileNameToUp;
                            break;
                        }
                    case "sftp":
                        {
                            String[] splitNameDoc = created.pathFileSFTP.replaceAll("\\\\","\\/").split("\\/");
                            String fileNameToUp = splitNameDoc[splitNameDoc.length-1];
                            uploadFromSFTP(pf.getPathFileModule() + "filesToUp/tempFiles/sftp/"+fileNameToUp, created.pathFileSFTP, created.pathFileSFTPUsername, created.pathFileSFTPPass, created.pathFileSFTPHost);
                            filePath = pf.getPathFileModule() + "filesToUp/tempFiles/sftp/" + fileNameToUp;
                            break;
                        }
                    default:
                        break;
                }
                
                if (created.checkedCleanDataExcel.equals("")) {
                    processFileNoClean(filePath, pathSolr, created.checkedCleanDataCore);
                    logger.info("indexacion directa sin limpiar datos");
                } else {
                    processFileClean(filePath, pathSolr, nameScript, created.checkedCleanDataCore);
                    logger.info("indexacion con limpieza de datos");
                }
                return redirect(routes.Application.profile());
            } catch (IOException ex) {
                logger.error(ex);
            }

            return null;
        }
    }
    
    public static void uploadFromSFTP(String localFilePath, String remoteFile, String username, String password, String host) {

        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch(username, password, host);
        } catch (JSchException e) {
            logger.error("Error generando sesion: "+e.getMessage());
        }
        try {
            channelSftp.connect();
        } catch (JSchException e) {
            logger.error("Error conectando: "+e.getMessage());
        }
        try {
            channelSftp.get(remoteFile, localFilePath);
            logger.info("Download Complete");
        } catch (SftpException e) {
            logger.error("Error de archivo: "+e.getMessage());
        }
        channelSftp.exit();

    }

    private static ChannelSftp setupJsch(String username, String password, String host) throws JSchException {
        JSch jsch = new JSch();
        Session jschSession = jsch.getSession(username, host);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }
    
    public static String uploadFromUrl(String URLIn, String nameConfig) {
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadOtherPropertiesBD();
        String path = !nameConfig.equals("") ? pf.getPathFileModule() + "/filesToUp/configs/"+nameConfig+"/files/" : pf.getPathFileModule() + "/filesToUp/tempFiles/urls/";
        File fDir = new File(path);
        if(!fDir.exists()){
            fDir.mkdirs();
        }
        fDir.setReadable(true, false);
        fDir.setExecutable(true, false);
        fDir.setWritable(true, false);
        String fileNameToUp="";
        URL url = null;
        try {
            url = new URL(URLIn);
        } catch (MalformedURLException ex) {
            logger.error("Error: "+ex.getMessage());
        }
        try (InputStream in = url.openStream()) {
            
            fileNameToUp = Hashing.sha256().hashString(URLIn, StandardCharsets.UTF_8).toString()+".csv";
            Files.copy(in, Paths.get(path + fileNameToUp), StandardCopyOption.REPLACE_EXISTING);
            File f = new File(path + fileNameToUp);
            f.setReadable(true, false);
            f.setExecutable(true, false);
            f.setWritable(true, false);
        } catch (Exception e) {
            logger.error(e);
        }
        return fileNameToUp;
    }

    public static LinkedHashMap<String, String> fillScripts() {
        LinkedHashMap<String, String> arrScript = new LinkedHashMap<>();

        try {

            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            pf.loadOtherPropertiesBD();
            String filePath = pf.getPathFileModule() + "/filesToUp/scripts/";
            File[] directories = new File(filePath).listFiles();

            for (File directorie : directories) {
                boolean check1 = directorie.getName().endsWith(".py");
                if (check1) {
                    arrScript.put(directorie.getName(), directorie.getName());
                }
            }

        } catch (Exception ex) {
            logger.error("Error cargando archivos de limpieza:" + ex);
        }

        return arrScript;
    }

    public static String loadSolrDataBusc(String buscador) {
        String pathConfig = "/opt/data/IFindIt/Buscadores/" + buscador + "/Buscador/temp/temp.config";
        String solrInfo = "";
        String ipSolr, portSolr, coreSolr;
        ipSolr = "";
        portSolr = "";
        coreSolr = "";
        BufferedReader br;
        FileReader fr;
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();

            fr = new FileReader(pathConfig);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.contains("*****")) {
                    if (sCurrentLine.contains("ipSolr")) {
                        ipSolr = sCurrentLine.split("=")[1].trim();
                    } else if (sCurrentLine.contains("portSolr")) {
                        portSolr = sCurrentLine.split("=")[1].trim();
                    } else if (sCurrentLine.contains("coreSolr")) {
                        coreSolr = sCurrentLine.split("=")[1].trim();
                    }
                }
            }
            solrInfo = "http://" + ipSolr + ":" + portSolr + "/solr/" + coreSolr;
            return solrInfo;
        } catch (IOException e) {
            logger.error("Error: " + e);
        }
        return solrInfo;
    }
    
    public static void convertToCSV(String file){
        String fileExtension = FilenameUtils.getExtension(file);
        try (FileInputStream input_document = new FileInputStream(new File(file)); XSSFWorkbook my_xls_workbook = new XSSFWorkbook(input_document)) {
            XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
            Iterator<Row> rowIterator = my_worksheet.iterator();
            logger.info("Conviriendo archivo: "+file);
            FileWriter my_csv = new FileWriter(file.replace(fileExtension, "csv"));
            try (CSVWriter my_csv_output = new CSVWriter(my_csv)) {
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    int i = 0;
                    String[] csvdata = new String[2];
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next(); //Fetch CELL
                        switch (cell.getCellTypeEnum()) { //Identify CELL type
                            //you need to add more code here based on
                            //your requirement / transformations
                            case BOOLEAN:
                                csvdata[i] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case STRING:
                                String media = cell.getStringCellValue();
                                // if (media.indexOf(',')== -1) {
                                media = media.replaceAll("\"", "");
                                //}
                                csvdata[i] = media;
                                break;
                            case FORMULA:
                                csvdata[i] = cell.getCellFormula();
                                break;
                            case NUMERIC:
                                csvdata[i] = String.valueOf(((int) cell.getNumericCellValue()));
                                break;
                        }
                        i = i + 1;
                    }
                    csvdata[0] = csvdata[0].replaceAll("\"", "");
                    my_csv_output.writeNext(csvdata, false);
                }
                //close the CSV file
            }

        } catch (FileNotFoundException ex) {
            logger.error("Error: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error: " + ex.getMessage());
        }
    }

    public static void processFileNoClean(String file, String solr, String cleanCore) {
        if(cleanCore.equals("true"))
            deleteCore(solr);
        
        String fileExtension = FilenameUtils.getExtension(file);
        if (fileExtension.equals("xlsx") || fileExtension.equals("xls") || fileExtension.equals("xml")) {
            convertToCSV(file);
            indexar(file.replace(fileExtension, "csv"), solr);
        } else {
            indexar(file, solr);
        }
    }

    public static void deleteCore(String pathSolr) {
        try {
            String curl_process_command = "curl -g -X POST -H 'Content-Type: text/xml' " + pathSolr + "/update?commit=true --data-binary '<delete><query>*:*</query></delete>'";
            logger.info("Curl de borrado--> " + curl_process_command);
            String[] args_arr = curl_process_command.split(" ");
            Process process = Runtime.getRuntime().exec(args_arr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder responseCurl = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseCurl.append(line);
            }
        } catch (IOException ex) {
            logger.error("Error: "+ex.getMessage());
        }
    }
    
    public static void indexar(String pathFileOut, String pathSolr) {
        try {
            String curl_process_command = "curl -g -X POST -H Content-type:application/csv " + pathSolr + "/update?commit=true --data-binary @" + pathFileOut;
            logger.info("Curl de indexacion directa--> " + curl_process_command);
            String[] args_arr = curl_process_command.split(" ");
            Process process = Runtime.getRuntime().exec(args_arr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder responseCurl = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseCurl.append(line);
            }
        } catch (IOException ex) {

        }
    }

    public static void processFileClean(String file, String solr, String nameScript, String cleanCore) {
        try {
            String fileExtension = FilenameUtils.getExtension(file);
            if (fileExtension.equals("xlsx") || fileExtension.equals("xls") || fileExtension.equals("xml")) {
                convertToCSV(file);
                file = file.replace(fileExtension, "csv");
            }
            
            String reqUrl;
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();

            String pathModuleIndexer = pf.getPathModuleDatabases();

            //System.out.println(""+pathModuleIndexer);
            logger.info("" + pathModuleIndexer);
            reqUrl = pathModuleIndexer + "indexExcel?pathFile=" + file + "&pathSolr=" + solr + "&nameScript=" + nameScript + "&cleanCore=" + cleanCore;

            logger.info(reqUrl);

            URL urlGet = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestMethod("GET");
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
        } catch (Exception ex) {
            logger.error(ex);
        }

    }
    
    public static String verificarTareaOthersFiles(LinkedHashMap<String, String> tareas, String conexion) {
        if (tareas.containsKey(conexion)) {
            return conexion.trim();
        }
        return "";
    }
    
    public static LinkedHashMap<String, String> fillTareasOthersFiles() {
        try {
            LinkedHashMap<String, String> nomTareas = new LinkedHashMap<>();

            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathTareas = pf.getPathFileModule() + "/filesToUp/task.xml";
            File file = new File(pathTareas);
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
                            if (!nodeLstInt.item(a).getTextContent().equals("")) {
                                nomTareas.put(nodeLstInt.item(a).getTextContent(), nodeLstInt.item(a).getTextContent());
                            } else {
                                nomTareas.put("", "");
                            }
                            break;
                    }
                }
            }
            return nomTareas;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result saveProgTareaOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Programaci\u00F3n de tarea de otros archivos";
        generateLineLog(lineLog);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();

        Form<ProgTareaOthersFilesForm> filledFormProgTareaOthersFiles = progTareaOthersFilesForm.bindFromRequest();

        LinkedHashMap<String, String> arrListaSemanas;
        ProgTarea progTarea = new ProgTarea();
        arrListaSemanas = progTarea.fillDiaSemana();

        LinkedHashMap<String, String> arrListaDiaMes;
        arrListaDiaMes = progTarea.fillDiasMes();

        LinkedHashMap<String, String> arrListaHoras;
        arrListaHoras = progTarea.fillHoras();

        LinkedHashMap<String, String> arrListaMinutos;
        arrListaMinutos = progTarea.fillMinutos();

        HashMap<String, String> arrConfigurations;
        HashMap<String, String> arrConfigurationsSort;
        ConfigurationsOthersFiles configurationsOthersFiles = new ConfigurationsOthersFiles();
        arrConfigurations = configurationsOthersFiles.fillConfigurations();
        arrConfigurationsSort = configurationsOthersFiles.sortHashMapByValues(arrConfigurations);

        if (filledFormProgTareaOthersFiles.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(upLoadOthersFile_prog_tarea.render("Programaci\u00F3n Tarea Otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormProgTareaOthersFiles, arrListaSemanas, arrListaDiaMes, arrListaHoras, arrListaMinutos, arrConfigurationsSort));
        } else {
            try {
                String folderRaiz = pf.getPathFileModule() + "/filesToUp/";
                String pathTareas = folderRaiz + "task.xml";
                ProgTareaOthersFilesForm created = filledFormProgTareaOthersFiles.get();
                crearRaizProgTarea(pathTareas, created.porMinutosTarea, created.porHorasTarea, created.diaTarea, created.diaSemanaTarea, created.diaMesTarea, created.horaTarea, created.minTarea, created.selConexion);
                new ProgramacionTareaOthersFiles(created.porMinutosTarea, created.porHorasTarea, created.diaTarea, created.diaSemanaTarea, created.diaMesTarea, Integer.parseInt(created.horaTarea), Integer.parseInt(created.minTarea), created.selConexion, folderRaiz).iniciarTarea();
                return redirect(routes.Application.profile());
            } catch (SchedulerException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
    
    public static void crearRaizProgTarea(String pathTareas, String porMinutosTarea, String porHorasTarea, String dia, String diaSemana, String diaMes, String hora, String min, String con) {
        try {
            File file = new File(pathTareas);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            NodeList nodeLstInt;
            String ultimoID = "";
            //Obtener ultimo Nodo
            if (nodeLst.getLength() - 1 >= 0) {
                nodeLstInt = nodeLst.item(nodeLst.getLength() - 1).getChildNodes();
                //Obtener ultimo ID
                ultimoID = nodeLstInt.item(0).getTextContent();
            } else if (nodeLst.getLength() - 1 < 0) {
                ultimoID = "0";
            }

            // Nos devuelve el nodo ra\u00EDz del documento XML.
            Node nodoRaiz = doc.getDocumentElement();
            Element nuevoNodo = doc.createElement("tarea");

            int nuevoID = Integer.parseInt(ultimoID) + 1;
            Element nodoID = doc.createElement("id");
            nodoID.setTextContent(String.valueOf(nuevoID));

            Element nodoPorMinutos = doc.createElement("porMinutos");
            nodoPorMinutos.setTextContent(porMinutosTarea);

            Element nodoPorHoras = doc.createElement("porHoras");
            nodoPorHoras.setTextContent(porHorasTarea);

            Element nodoDia = doc.createElement("dia");
            nodoDia.setTextContent(dia);

            Element nodoDiaSemana = doc.createElement("diaSemana");
            nodoDiaSemana.setTextContent(diaSemana);

            Element nodoDiaMes = doc.createElement("diaMes");
            nodoDiaMes.setTextContent(diaMes);

            Element nodoHora = doc.createElement("hora");
            nodoHora.setTextContent(hora);

            Element nodoMin = doc.createElement("minuto");
            nodoMin.setTextContent(min);

            Element nodoConec = doc.createElement("conexion");
            nodoConec.setTextContent(con);

            nuevoNodo.appendChild(nodoID);
            nuevoNodo.appendChild(nodoPorMinutos);
            nuevoNodo.appendChild(nodoPorHoras);
            nuevoNodo.appendChild(nodoDia);
            nuevoNodo.appendChild(nodoDiaSemana);
            nuevoNodo.appendChild(nodoDiaMes);
            nuevoNodo.appendChild(nodoHora);
            nuevoNodo.appendChild(nodoMin);
            nuevoNodo.appendChild(nodoConec);
            nodoRaiz.appendChild(nuevoNodo);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTareas));
            transformer.transform(source, result);

        } catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            logger.error(e);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result loadProgTareaOthersFiles() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/filesToUp/task.xml";
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
    public static Result editTaskOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando Programaci\u00E3n de tarea de otros archivos";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        String[] splitRequest = requestTask.split("<;>", -1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTareas = pf.getPathFileModule() + "/filesToUp/task.xml";

        try {
            File file = new File(pathTareas);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                NodeList nodeLstInt = nodeLst.item(i).getChildNodes();
                if (nodeLstInt.item(0).getTextContent().equals(splitRequest[0])) {
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
                    String folderRaiz = pf.getPathFileModule() + "/filesToUp/";
                    new ProgramacionTareaOthersFiles(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], Integer.parseInt(splitRequest[6]), Integer.parseInt(splitRequest[7]), splitRequest[8], folderRaiz).iniciarTarea();
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTareas));
            transformer.transform(source, result);
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException | SchedulerException ex) {
            logger.error(ex);
        }
        
        return null;
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteProgTareaOthersFiles() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Tarea de otros archivos";
        generateLineLog(lineLog);

        String idTarea = request().body().asText().split("<;>")[0];
        String selConexion = request().body().asText().split("<;>")[1];
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTareas = pf.getPathFileModule() + "/filesToUp/task.xml";
        try {
            File file = new File(pathTareas);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element e = (Element) nodeLst.item(i);
                String valueID = nodeLst.item(i).getFirstChild().getTextContent();
                if (valueID.equals(idTarea)) {
                    e.getParentNode().removeChild(e);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTareas));
            transformer.transform(source, result);

            //DeleteJob
            ProgramacionTareaOthersFiles progOthersFiles = new ProgramacionTareaOthersFiles();
            progOthersFiles.detenerTarea(selConexion);
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException | SchedulerException ex) {
            logger.error(ex);
        }
        return null;
    }
    
    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
    /////////////////////////////////
}
