/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import play.db.DB;

/**
 *
 * @author atovar
 */
public class ConnectionDB {
    
    static Logger logger = Logger.getLogger(ConnectionDB.class);
    
    public static Connection getConector() throws ClassNotFoundException, SQLException {
        Connection con;
        //con = DB.getConnection("org.sqlite.JDBC");
        con = DB.getConnection();
        //logger.info("Conexi\u00F3n exitosa");      
        return con;
    }
      
    public static void ejecutarSentencia(String sentence, Connection conector) throws SQLException {
        Statement state;
        try {
            state = conector.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            state.executeUpdate(sentence);
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        cerrarConexion(conector);
    }
    
    public static ResultSet RealizarConsulta(String sentence, Connection conector) throws SQLException {
        Statement state;
        ResultSet result = null;
        try {
            state = conector.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            result = state.executeQuery(sentence);
        } catch (SQLException ex) {
            logger.error(ex.toString());
        }
        cerrarConexion(conector);
        return result;
    }
    
    public static int countRecords(String consultaCount, Connection conector) throws ClassNotFoundException {
        int tamDB = 0;
        ResultSet tamDBresult = null;
        try {
            tamDBresult = RealizarConsulta(consultaCount, conector);
            while (tamDBresult.next()) {
                tamDB = tamDBresult.getInt(1);
                logger.info("Tama\u00F1o de registros " + tamDB);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        cerrarConexion(conector);
        return tamDB;
    }
    
    public static void cerrarConexion(Connection conector){
        if (conector != null) {
            try {
                conector.close();
                //logger.info("Se cerro la conexi\u00F3n...");
            } catch (SQLException ex) {
                logger.error(ex.toString());
            }
        }
    }
}
