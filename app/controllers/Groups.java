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
import views.formdata.GroupsForm;

/**
 *
 * @author atovar
 */
public class Groups extends Controller{
    static Form<GroupsForm> groupsForm = form(GroupsForm.class);
    
    static Logger logger = Logger.getLogger(Groups.class);
    
     @Security.Authenticated(Secured.class)
    public static Result saveProfile(){
            
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Grupo";
        generateLineLog(lineLog);
        
        Form<GroupsForm> filledFormGroup = groupsForm.bindFromRequest();
        
        if (filledFormGroup.hasErrors()) {
            logger.error("Errores encontrados.");
            logger.error(filledFormGroup.errors());
            return badRequest(filledFormGroup.errorsAsJson());// badRequest(admin_profiles.render("Agregar perfiles", Boolean.TRUE, null, filledFormAdministrador));
        } else {
            try {
                GroupsForm created = filledFormGroup.get();
                Connection con; 
                con = ConnectionDB.getConector();
                String selCount = "SELECT count(*) FROM group_service where NAME_GROUP='"+created.nomGrupo.toLowerCase()+"'";
                int tamDB = ConnectionDB.countRecords(selCount, con);
                logger.info("Buscando grupo");
                if(tamDB>0){
                    logger.info("El grupo ya existe!!!");
                    return badRequest("Este grupo ya existe!!!");
                }else{
                    con = ConnectionDB.getConector();
                    ConnectionDB.ejecutarSentencia("INSERT INTO group_service (NAME_GROUP) VALUES('"+created.nomGrupo.toLowerCase()+"');", con);
                    logger.info("Creando nuevo grupo");
                    
                    con = ConnectionDB.getConector();
                    String select= "SELECT ID_GROUP FROM group_service where NAME_GROUP='"+created.nomGrupo.toLowerCase()+"'";
                    ResultSet rs = ConnectionDB.RealizarConsulta(select,con);
                    int idGroup=0;
                    while (rs.next()) {
                        idGroup = rs.getInt("ID_GROUP");
                    }
                    logger.info("Traer ID del nuevo grupo");
                    
                    String[] splitProfiles = created.hiddenProfileGroup.split("<;>",-1);
                    for(int i=0; i<(splitProfiles.length); i++){   
                        con = ConnectionDB.getConector();
                        ConnectionDB.ejecutarSentencia("INSERT INTO profile_group (ID_GROUP,ID_PROFILE) VALUES('"+idGroup+"','"+splitProfiles[i]+"');", con);
                    }
                    logger.info("Creando perfiles del nuevo grupo");
                    
                    return redirect(routes.Application.profile());
                }
                
            } catch (ClassNotFoundException | SQLException ex) {
                logger.error(ex);
                return badRequest("Error: "+ex);
            }
        }
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadGroups() {
        try {
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT * FROM group_service",con);
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
    
    public static Result loadProfilesGroups() {
        try {
            String requestGroup = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT ID_PROFILE FROM profile_group where ID_GROUP='"+requestGroup+"';",con);
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
    public static Result saveEditGroup(){
            
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Modificando Grupo";
            generateLineLog(lineLog);
            
            String requestGroup = request().body().asText();
            String[] splitRequest = requestGroup.split("<;;>",-1);
            
            Connection con;
            con = ConnectionDB.getConector();
            String delete = "DELETE FROM profile_group WHERE ID_GROUP='"+splitRequest[0]+"';";
            ConnectionDB.ejecutarSentencia(delete, con);
            logger.info("Borrando permisos para el grupo "+splitRequest[0]);
            
            String[] splitProfiles = splitRequest[2].split("<;>",-1);
            for(int i=0; i<(splitProfiles.length); i++){   
                con = ConnectionDB.getConector();
                ConnectionDB.ejecutarSentencia("INSERT INTO profile_group (ID_GROUP,ID_PROFILE) VALUES('"+splitRequest[0]+"','"+splitProfiles[i]+"');", con);
            }
            logger.info("Insertando permisos para el grupo "+splitRequest[0]);

            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteGroup() {
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Grupo";
            generateLineLog(lineLog);
            
            String requestGroup = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            String update = "DELETE FROM group_service WHERE ID_GROUP = '"+requestGroup+"';";
            ConnectionDB.ejecutarSentencia(update, con);
            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }     
    }
    
    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
}
