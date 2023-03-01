/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.backup;

import controllers.BackUp;
import models.databases.*;
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

public class InvokeTaskBackUp implements Job {

    static Logger logger = Logger.getLogger(InvokeTaskBackUp.class);
    
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
                String solr = URLEncoder.encode(datos[0].split("-")[0], "UTF-8");
                String core = URLEncoder.encode(datos[0].split("-")[1], "UTF-8");
                
                BackUp backup = new BackUp();

                logger.info("Resultado:"+ backup.backUp(solr, core));      
                
               
            } catch (IOException ex) {
                logger.error(ex);
            }
 
        }
    }
    
    
}
