/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import models.PropertiesFile;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.redirect;
import views.formdata.ConfigurationForm;
import org.apache.log4j.Logger;

import views.html.*;

/**
 *
 * @author atovar
 */
public class Configuration extends Controller {

    static Form<ConfigurationForm> configurationForm = form(ConfigurationForm.class);
    
    static Logger logger = Logger.getLogger(Configuration.class);

    @Security.Authenticated(Secured.class)
    public static Result saveConfiguration() {
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Guardando configuracion del Modulo";
        generateLineLog(lineLog);
        
        Form<ConfigurationForm> filledFormConfig = configurationForm.bindFromRequest();
        
        BufferedWriter writer;

        if (filledFormConfig.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(configuration_module.render("Configuracion Modulo", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormConfig));
        } else {
            try {
                ConfigurationForm created = filledFormConfig.get();
                //String pathApp = Play.application().path().getAbsolutePath();
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                
                String fileNameCrt = "";
                String fileNameP12 = "";                
                                    
                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart certificate = body.getFile("certificate");
                Http.MultipartFormData.FilePart certificateP12 = body.getFile("certificateP12");
                
                if (certificate != null && certificateP12 != null) {
                    fileNameCrt = certificate.getFilename();
                    fileNameP12 = certificateP12.getFilename();
                    File fileCrt = certificate.getFile();  
                    File fileP12 = certificateP12.getFile();
                    byte[] fileContentCrt = Files.readAllBytes(fileCrt.toPath());
                    byte[] fileContentP12 = Files.readAllBytes(fileP12.toPath());
                    try (OutputStream outCrt = new FileOutputStream(new File(pf.getPathFileModule() + "/cert/" + fileNameCrt))) {
                        outCrt.write(fileContentCrt);
                        outCrt.flush();
                    }
                    try (OutputStream outP12 = new FileOutputStream(new File(pf.getPathFileModule() + "/cert/" + fileNameP12))) {
                        outP12.write(fileContentP12);
                        outP12.flush();
                    }
                }
                File f = new File(pf.getPathFileModule()+"/temp/configuration.properties");
                writer = new BufferedWriter(new FileWriter(f));
                writer.append("pathFileModule="+created.pathFileModule.replace("\\", "\\\\")+"\n");
                writer.append("nameCertificate="+fileNameCrt+"\n");
                writer.append("nameCertificateP12="+fileNameP12+"\n");
                writer.append("passwordCertificate="+created.passwordCertificate+"\n");
                writer.flush();
                writer.close();
                
                return redirect(routes.Application.profile());
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return null;
    }
    
    public static void generateLineLog(String lineLog){
        logger.info(lineLog);
    }
}
