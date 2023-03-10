package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import models.PropertiesFile;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Results.badRequest;
import org.apache.log4j.Logger;
import static play.mvc.Controller.request;
import views.formdata.RouteQueryForm;

import views.html.*;

/**
 *
 * @author agarzon
 */

public class QueryRouteConfig extends Controller {

    static Form<RouteQueryForm> routeQueryForm = form(RouteQueryForm.class);
    
    static Logger logger = Logger.getLogger(QueryRouteConfig.class);

    @Security.Authenticated(Secured.class)
    public static Result addConfig() {
        /*Lineas para Guardar en LOG */
        String username = request().username(); 
        String ipAddress = request().getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
          ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress+"<;>"+username+"<;>Guardando url de la API de consultas e indexacion de Solr";
        generateLineLog(lineLog);
        
        Form<RouteQueryForm> filledRouteQueryForm = routeQueryForm.bindFromRequest();
        
        BufferedWriter writer;
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();

        if (filledRouteQueryForm.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(config_admin_colecciones.render("Configurar URL de la API", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledRouteQueryForm));
        } else {
            
            RouteQueryForm created = filledRouteQueryForm.get();
            
            try {
                
                File f = new File(pf.getPathFileModule() + "/temp/configuration_admin_collections.properties");
                writer = new BufferedWriter(new FileWriter(f));
                writer.append("urlApiSolr=" + created.urlApiSolr+"\n");
                writer.flush();
                writer.close();
                
                String okResponse = "Se ha agregado correctamente a la configuracion, la URL: " + created.urlApiSolr;
                
                logger.info("Se ha agregado correctamente a la configuracion, la URL: " + created.urlApiSolr);
                return ok(response_admin_colecciones.render("Configurar URL de la API", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), okResponse));
                
            } catch (IOException ex) {
                logger.error(ex);
            }
            
            return null;
        }        
    }
    
    public static void generateLineLog(String lineLog){
        logger.info(lineLog);
    }
}
