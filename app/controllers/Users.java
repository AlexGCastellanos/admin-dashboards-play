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
import views.formdata.UsersForm;

/**
 *
 * @author atovar
 */
public class Users extends Controller{
    static Form<UsersForm> usersForm = form(UsersForm.class);
    
    static Logger logger = Logger.getLogger(Users.class);
    
    @Security.Authenticated(Secured.class)
    public static Result saveUser(){
            
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Usuario";
        generateLineLog(lineLog);
        
        Form<UsersForm> filledFormUser = usersForm.bindFromRequest();
        
        if (filledFormUser.hasErrors()) {
            logger.error("Errores encontrados.");
            logger.error(filledFormUser.errors());
            return badRequest(filledFormUser.errorsAsJson());// badRequest(admin_profiles.render("Agregar perfiles", Boolean.TRUE, null, filledFormAdministrador));
        } else {
            try {
                UsersForm created = filledFormUser.get();
                Connection con; 
                con = ConnectionDB.getConector();
                String selCount = "SELECT count(*) FROM user_service where ADDRESS='"+created.email.toLowerCase()+"'";
                int tamDB = ConnectionDB.countRecords(selCount, con);
                logger.info("Buscando usuario");
                if(tamDB>0){
                    logger.info("El usuario ya existe!!!");
                    return badRequest("Este usuario ya existe!!!");
                }else{
                    con = ConnectionDB.getConector();
                    ConnectionDB.ejecutarSentencia("INSERT INTO user_service (NAME_USER,ADDRESS,PASSWORD,ID_GROUP) VALUES('"+created.nomUser+"','"+created.email.toLowerCase()+"','"+created.password+"','"+created.hiddenGroup+"');", con);
                    logger.info("Creando nuevo usuario");

                    return redirect(routes.Application.profile());
                }
                
            } catch (ClassNotFoundException | SQLException ex) {
                logger.error(ex);
                return badRequest("Error: "+ex);
            }
        }
    }
    
    @BodyParser.Of(Json.class)
    public static Result loadUsers() {
        try {
            Connection con;
            con = ConnectionDB.getConector();
            ResultSet result = ConnectionDB.RealizarConsulta("select u.*, g.NAME_GROUP from user_service u, group_service g where g.ID_GROUP = u.ID_GROUP;",con);
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
    public static Result saveEditUser(){
            
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Modificando usuarios";
            generateLineLog(lineLog);
            
            String requestUser = request().body().asText();
            String[] splitRequest = requestUser.split("<;>",-1);

            Connection con;
            con = ConnectionDB.getConector();
            ConnectionDB.ejecutarSentencia("UPDATE user_service SET \"ADDRESS\" = '"+splitRequest[2]+"', \"PASSWORD\" = '"+splitRequest[3]+"' , \"ID_GROUP\" = '"+splitRequest[4]+ "' WHERE ID_USER = '"+splitRequest[0]+"';", con);

            logger.info("Modificando usuario "+splitRequest[0]);

            return redirect(routes.Application.profile());
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex);
            return badRequest("Error: "+ex);
        }
    }
    
    @Security.Authenticated(Secured.class)
    public static Result deleteUser() {
        try {
            /*Lineas para Guardar en LOG */
            String username = request().username();
            String ipAddress = request().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request().remoteAddress();
            }
            String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Usuario";
            generateLineLog(lineLog);
            
            String requestGroup = request().body().asText();
            Connection con;
            con = ConnectionDB.getConector();
            String update = "DELETE FROM user_service WHERE ID_USER = '"+requestGroup+"';";
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
