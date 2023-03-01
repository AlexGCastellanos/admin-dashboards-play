/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.othersFiles;

import static controllers.UploadOtherFiles.loadSolrDataBusc;
import static controllers.UploadOtherFiles.processFileClean;
import static controllers.UploadOtherFiles.processFileNoClean;
import static controllers.UploadOtherFiles.uploadFromSFTP;
import static controllers.UploadOtherFiles.uploadFromUrl;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import models.PropertiesFile;
import models.java_certificate.Java_Certificate;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author atovar
 */

public class InvocarTareaOthersFiles implements Job {

    static Logger logger = Logger.getLogger(InvocarTareaOthersFiles.class);
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    private String nomConexion="", typeUpload="", pathFileURL="", pathFileSFTPHost="", pathFileSFTPUsername="", pathFileSFTPPass="", pathFileSFTP="", checkedCleanDataExcel="", nameScript="", checkedCleanDataCore="", buscador="";
    
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
            // Se hace split con caracter# para los datos de sincronizacion
            String[] datos = jec.getJobDetail().getDescription().split("#",-1);

            if(datos.length>0){
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                pf.loadOtherPropertiesBD();
                String pathConexiones = datos[1]+"connections.xml";
                cargarConexion(pathConexiones, datos[0]);

                String filePath="";
                String pathSolr = loadSolrDataBusc(buscador);
                String nameScriptInt = nameScript != null ? nameScript : "clean_data_excel.py";

                switch (typeUpload) {
                    case "http":
                        {
                            String fileNameToUp = uploadFromUrl(pathFileURL,nomConexion);
                            filePath = pf.getPathFileModule() + "filesToUp/configs/"+nomConexion+"/files/"+ fileNameToUp;
                            break;
                        }
                    case "sftp":
                        {
                            String[] splitNameDoc = pathFileSFTP.replaceAll("\\\\","\\/").split("\\/");
                            String fileNameToUp = splitNameDoc[splitNameDoc.length-1];
                            String path = pf.getPathFileModule() + "/filesToUp/configs/"+nomConexion+"/files/";
                            File fDir = new File(path);
                            if(!fDir.exists()){
                                fDir.mkdirs();
                            }
                            fDir.setReadable(true, false);
                            fDir.setExecutable(true, false);
                            fDir.setWritable(true, false);
                            uploadFromSFTP(pf.getPathFileModule() + "filesToUp/configs/"+nomConexion+"/files/"+fileNameToUp, pathFileSFTP, pathFileSFTPUsername, pathFileSFTPPass, pathFileSFTPHost);
                            filePath = pf.getPathFileModule() + "filesToUp/configs/"+nomConexion+"/files/"+fileNameToUp;
                            break;
                        }
                    default:
                        break;
                }

                if (checkedCleanDataExcel.equals("")) {
                    processFileNoClean(filePath, pathSolr, checkedCleanDataCore);
                    logger.info("indexacion directa sin limpiar datos");
                } else {
                    processFileClean(filePath, pathSolr, nameScriptInt, checkedCleanDataCore);
                    logger.info("indexacion con limpieza de datos");
                }
            } 
        }
    }
    
    public void cargarConexion(String pathConexiones, String selConexion){         
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
                    nomConexion = conexion.getElementsByTagName("nomConexion").item(0).getTextContent();
                    typeUpload = conexion.getElementsByTagName("typeUpload").item(0).getTextContent();
                    pathFileURL = conexion.getElementsByTagName("pathFileURL").item(0).getTextContent();
                    pathFileSFTPHost = conexion.getElementsByTagName("pathFileSFTPHost").item(0).getTextContent();
                    pathFileSFTPUsername = conexion.getElementsByTagName("pathFileSFTPUsername").item(0).getTextContent();
                    pathFileSFTPPass = conexion.getElementsByTagName("pathFileSFTPPass").item(0).getTextContent();
                    pathFileSFTP = conexion.getElementsByTagName("pathFileSFTP").item(0).getTextContent();
                    checkedCleanDataExcel = conexion.getElementsByTagName("checkedCleanDataExcel").item(0).getTextContent();
                    nameScript = conexion.getElementsByTagName("nameScript").item(0).getTextContent();
                    checkedCleanDataCore = conexion.getElementsByTagName("checkedCleanDataCore").item(0).getTextContent();
                    buscador = conexion.getElementsByTagName("buscador").item(0).getTextContent();
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex);
        }
    }   
}
