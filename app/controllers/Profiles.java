/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.ConnectionDB;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Controller;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import views.formdata.ProfilesForm;

/**
 *
 * @author atovar
 */
public class Profiles extends Controller{
    static Form<ProfilesForm> profileForm = form(ProfilesForm.class);
    
    static Logger logger = Logger.getLogger(Profiles.class);
    
    @Security.Authenticated(Secured.class)
    public static Result saveProfile(){
            
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Perfil";
        generateLineLog(lineLog);
        
        Form<ProfilesForm> filledFormProfile = profileForm.bindFromRequest();
        
        if (filledFormProfile.hasErrors()) {
            logger.error("Errores encontrados.");
            logger.error(filledFormProfile.errors());
            return badRequest(filledFormProfile.errorsAsJson());// badRequest(admin_profiles.render("Agregar perfiles", Boolean.TRUE, null, filledFormAdministrador));
        } else {
            try {
                ProfilesForm created = filledFormProfile.get();
                Connection con; 
                con = ConnectionDB.getConector();
                String selCount = "SELECT count(*) FROM profile_service where NAME_PROFILE='"+created.nomPerfil.toLowerCase()+"'";
                int tamDB = ConnectionDB.countRecords(selCount, con);
                logger.info("Buscando perfil");
                if(tamDB>0){
                    logger.info("El perfil ya existe!!!");
                    return badRequest("Este perfil ya existe!!!");
                }else{
                    con = ConnectionDB.getConector();
                    ConnectionDB.ejecutarSentencia("INSERT INTO profile_service (NAME_PROFILE) VALUES('"+created.nomPerfil.toLowerCase()+"');", con);
                    logger.info("Creando nuevo perfil");
                    
                    con = ConnectionDB.getConector();
                    String select= "SELECT ID_PROFILE FROM profile_service where NAME_PROFILE='"+created.nomPerfil.toLowerCase()+"'";
                    ResultSet rs = ConnectionDB.RealizarConsulta(select,con);
                    int idProfile=0;
                    while (rs.next()) {
                        idProfile = rs.getInt("ID_PROFILE");
                    }
                    logger.info("Traer ID del nuevo perfil");
                    
                    String[] splitPermissions = created.hiddenPermissions.split("<;>",-1);
                    for(int i=0; i<(splitPermissions.length); i++){   
                        con = ConnectionDB.getConector();
                        ConnectionDB.ejecutarSentencia("INSERT INTO permission_profile (ID_PROFILE,ID_PERMISSION) VALUES('"+idProfile+"','"+splitPermissions[i]+"');", con);
                    }
                    logger.info("Creando permisos del nuevo perfil");
                    
                    return redirect(routes.Application.profile());
                }
                
            } catch (ClassNotFoundException | SQLException ex) {
                logger.error(ex);
                return badRequest("Error: "+ex);
            }
        }
    }
    
    public static Result loadPermissionsProfiles() {
        try {
            String requestProfile = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT ID_PERMISSION FROM permission_profile where ID_PROFILE='"+requestProfile+"';",con);
            //logger.info("Consultando permisos del perfil "+requestProfile);
            
            int total_columns = result.getMetaData().getColumnCount();
            
            JSONArray jsonArray = new JSONArray();
            while (result.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < total_columns; i++) {
                    jsonObject.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                }
                jsonArray.put(jsonObject);
            }
            String jsonString = jsonArray.toString();
            
            return ok(jsonString);
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }
    } 
    
    
    
    @Security.Authenticated(Secured.class)
    public static Result saveEditProfile(){
            
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Modificando Perfil";
            generateLineLog(lineLog);
            
            String requestProfile = request().body().asText();
            String[] splitRequest = requestProfile.split("<;;>",-1);
            
            Connection con;
            con = ConnectionDB.getConector();
            String delete = "DELETE FROM permission_profile WHERE ID_PROFILE='"+splitRequest[0]+"';";
            ConnectionDB.ejecutarSentencia(delete, con);
            logger.info("Borrando permisos para el perfil "+splitRequest[0]);
            
            String[] splitPermissions = splitRequest[2].split("<;>",-1);
            for(int i=0; i<(splitPermissions.length); i++){   
                con = ConnectionDB.getConector();
                ConnectionDB.ejecutarSentencia("INSERT INTO permission_profile (ID_PROFILE,ID_PERMISSION) VALUES('"+splitRequest[0]+"','"+splitPermissions[i]+"');", con);
            }
            logger.info("Insertando permisos para el perfil "+splitRequest[0]);

            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteProfile() {
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Perfil";
            generateLineLog(lineLog);
            
            String requestProfile = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            String update = "DELETE FROM profile_service WHERE ID_PROFILE = '"+requestProfile+"';";
            ConnectionDB.ejecutarSentencia(update, con);
            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }     
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadProfiles() {
        try {
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT * FROM profile_service",con);
            int total_columns = result.getMetaData().getColumnCount();
            
            JSONArray jsonArray = new JSONArray();
            while (result.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < total_columns; i++) {
                    jsonObject.put(result.getMetaData().getColumnLabel(i + 1).toLowerCase(), result.getObject(i + 1));
                }
                jsonArray.put(jsonObject);
            }
            String jsonString = jsonArray.toString();
            
            return ok(jsonString);
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }
    } 
    
    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
    
    
}
