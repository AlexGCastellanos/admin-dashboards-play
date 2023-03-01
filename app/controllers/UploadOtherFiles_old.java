package controllers;
import java.io.BufferedReader;
import views.html.upLoadOthersFile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import models.PropertiesFile;
import play.*;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Controller.request;
import views.formdata.uploadFileForm;
import org.apache.log4j.Logger;

public class UploadOtherFiles_old extends Controller {
    static Form<uploadFileForm> uploadFileForm = form(uploadFileForm.class);
    
    static Logger logger = Logger.getLogger(UploadOtherFiles_old.class);
    
    
    @Security.Authenticated(Secured.class)
    public static Result upLoadOtherFile() { 
                /////////////////////////////////        
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Indexando Directamente Un Archivo";
        generateLineLog(lineLog);
        
        Form<uploadFileForm> filledFormConfig = uploadFileForm.bindFromRequest();
        
        BufferedWriter writer;

        if (filledFormConfig.hasErrors()) {
            logger.error("Errores encontrados.");
//            return badRequest(upLoadOthersFile.render("Carga De Archivos.", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormConfig));
        } else {
            try {
                uploadFileForm created = filledFormConfig.get();
                //String pathApp = Play.application().path().getAbsolutePath();
                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                pf.loadOtherPropertiesBD();
                
                String pathModuleDatabases = pf.getPathModuleDatabases(); 
                String fileNameToUp = "";
               
                Http.MultipartFormData body = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart fileToUp = body.getFile("pathFile");
                if (fileToUp != null ) {
                    fileNameToUp = fileToUp.getFilename();
                    File fileCrt = fileToUp.getFile();  
                    byte[] fileContentCrt = Files.readAllBytes(fileCrt.toPath());
                    try (OutputStream outCrt = new FileOutputStream(new File(pf.getPathFileModule() + "/filesToUp/" + fileNameToUp))) {
                        outCrt.write(fileContentCrt);
                        outCrt.flush();
                    }
                    /////////////////////////////////////////////
                String reqUrl;
                String filePath =pf.getPathFileModule() + "/filesToUp/" + fileNameToUp;
                String solr =created.buscador ;
//                 reqUrl = pathModuleDatabases+"executeDirectIndex?selConexion="+URLEncoder.encode(datos[0], "UTF-8")+"&folderRaiz="+URLEncoder.encode(datos[1], "UTF-8");
                reqUrl = pathModuleDatabases+"executeDirectIndex?solr="+solr+"&file="+filePath;
                logger.info("indexacion directa hacia => "+reqUrl);
                
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
                ////////////////////////////////////////////
                } 
                
                
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
                /////////////////////////////////
}
