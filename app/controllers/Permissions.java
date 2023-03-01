/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ConnectionDB;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import play.data.Form;
import static play.data.Form.form;
import play.data.validation.ValidationError;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Controller;
import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import views.formdata.PermissionsForm;
import views.html.admin_add_permissions;

/**
 *
 * @author atovar
 */
public class Permissions extends Controller{
    static Form<PermissionsForm> permissionsForm = form(PermissionsForm.class);
    
    static Logger logger = Logger.getLogger(Permissions.class);
    
    @Security.Authenticated(Secured.class)
    public static Result savePermission(){
            
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Permisos";
        generateLineLog(lineLog);
        
        Form<PermissionsForm> filledFormPermissions = permissionsForm.bindFromRequest();
        List<ValidationError> errors = new ArrayList<>();
        
        if (filledFormPermissions.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(admin_add_permissions.render("Agregar permiso", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormPermissions));
        } else {
            try {
                PermissionsForm created = filledFormPermissions.get();
                Connection con; 
                con = ConnectionDB.getConector();
                String selCount = "SELECT count(*) FROM permission_service where ID_PERMISSION='"+created.idPermission.toLowerCase()+"'";
                int tamDB = ConnectionDB.countRecords(selCount, con);
                if(tamDB>0){
                    filledFormPermissions.reject(new ValidationError("idPermission", "Este permiso ya existe..."));
                    return badRequest(admin_add_permissions.render("Agregar permiso", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormPermissions));
                }else{
                    con = ConnectionDB.getConector();
                    ConnectionDB.ejecutarSentencia("INSERT INTO permission_service (ID_PERMISSION,DESC_PERMISSION) VALUES('"+created.idPermission.toLowerCase()+"','"+created.permission.toLowerCase()+"');", con);
                    return redirect(routes.Application.profile());
                }
                
            } catch (ClassNotFoundException | SQLException ex) {
                logger.error(ex);
                return badRequest("Error: "+ex);
            }
        }
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadPermission() {
        try {
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT * FROM permission_service",con);
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
    public static Result saveEditPermission(){
            
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Modificando Permiso";
            generateLineLog(lineLog);
            
            String requestPermission = new String(request().body().asText().getBytes("ISO-8859-1"), "UTF-8");
            
            String[] splitRequest = requestPermission.split("<;>",-1);
            Connection con;
            con = ConnectionDB.getConector();
            String update = "UPDATE permission_service SET \"DESC_PERMISSION\" = '"+splitRequest[1]+"' WHERE ID_PERMISSION = "+splitRequest[0]+";";
            ConnectionDB.ejecutarSentencia(update, con);
            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException | UnsupportedEncodingException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }        
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deletePermission() {
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Permiso";
            generateLineLog(lineLog);
            
            String requestPermission = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            String update = "DELETE FROM permission_service WHERE ID_PERMISSION = "+requestPermission+";";
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
