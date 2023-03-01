/*
 * To change this license header, choose License Headers in Project PropertiesFile.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author atovar
 */

public class PropertiesFile {
    
    private String pathFileModule;
    private String nameCertificate;
    private String nameCertificateP12;
    private String passwordCertificate;
    private String pathModuleDatabases;
    private String ipServiceCleanData;
    private String portServiceCleanData;
    private String portBootleCleanData;
    private String pathServiceCleanData;
    private String urlApiSolr;
    
    static Logger logger = Logger.getLogger(PropertiesFile.class);
    
    /*
     * Funcion que carga los valores iniciales del archivo de configuracion configuration.properties
     */
    
    public boolean loadProperties() {
        
        String absoluteDiskPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/temp/configuration.properties";
        Properties props = new Properties();
        FileInputStream is;
        try {
            is = new FileInputStream(absoluteDiskPath);
            props.load(is);
        } catch (IOException e1) {
            logger.error("Error al abrir configfile: " + e1.getMessage());
        }
        
        pathFileModule = props.getProperty("pathFileModule");
        nameCertificate = props.getProperty("nameCertificate");
        nameCertificateP12 = props.getProperty("nameCertificateP12");
        passwordCertificate = props.getProperty("passwordCertificate");
                
        return true;
    }
    
    /*
     * Funcion que carga los valores iniciales del archivo de configuracion other_configuration_BD.properties
     */
    
    public boolean loadOtherPropertiesBD() {      
        String absoluteDiskPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/temp/other_configuration_BD.properties";
        
        Properties props = new Properties();
        FileInputStream is;
        try {
            is = new FileInputStream(absoluteDiskPath);
            props.load(is);
        } catch (IOException e1) {
            logger.error("Error al abrir configfile: " + e1.getMessage());
        }
        
        pathModuleDatabases = props.getProperty("pathModuleDatabases");
        
        return true;
    }
    
    /*
     * Funcion que carga los valores iniciales del archivo de configuracion other_configuration_CleanData.properties
     */
    public boolean loadOtherPropertiesCleanData() {      
        String absoluteDiskPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/temp/other_configuration_CleanData.properties";
        
        Properties props = new Properties();
        FileInputStream is;
        try {
            is = new FileInputStream(absoluteDiskPath);
            props.load(is);
        } catch (IOException e1) {
            logger.error("Error al abrir configfile: " + e1.getMessage());
        }
        
        ipServiceCleanData = props.getProperty("ipServiceCleanData");
        portServiceCleanData = props.getProperty("portServiceCleanData");
        portBootleCleanData = props.getProperty("portBootleCleanData");
        pathServiceCleanData = "http://"+ipServiceCleanData+":"+portServiceCleanData;
        
        return true;
    }
    
     /*
     * Funcion que carga los valores iniciales del archivo de configuracion configuration_prueba_indexar.properties
     */
    
    public boolean loadConfiguracionPruebaIndexar() {
        
        String absoluteDiskPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/temp/configuration_prueba_indexar.properties";
        
        Properties props = new Properties();
        FileInputStream is;
        
        try {
            is = new FileInputStream(absoluteDiskPath);
            props.load(is);
        } catch (IOException e1) {
            logger.error("Error al abrir configfile: " + e1.getMessage());
        }
        
        urlApiSolr = props.getProperty("urlApiSolr");
        
        return true;
    }

    public String getPathFileModule() {
        return pathFileModule;
    }

    public void setPathFileModule(String pathFileModule) {
        this.pathFileModule = pathFileModule;
    } 

    public String getNameCertificate() {
        return nameCertificate;
    }

    public void setNameCertificate(String nameCertificate) {
        this.nameCertificate = nameCertificate;
    }

    public String getNameCertificateP12() {
        return nameCertificateP12;
    }

    public void setNameCertificateP12(String nameCertificateP12) {
        this.nameCertificateP12 = nameCertificateP12;
    }

    public String getPasswordCertificate() {
        return passwordCertificate;
    }

    public void setPasswordCertificate(String passwordCertificate) {
        this.passwordCertificate = passwordCertificate;
    }

    public String getPathModuleDatabases() {
        return pathModuleDatabases;
    }

    public void setPathModuleDatabases(String pathModuleDatabases) {
        this.pathModuleDatabases = pathModuleDatabases;
    }

    public String getPathServiceCleanData() {
        return pathServiceCleanData;
    }

    public void setPathServiceCleanData(String pathServiceCleanData) {
        this.pathServiceCleanData = pathServiceCleanData;
    }

    public String getIpServiceCleanData() {
        return ipServiceCleanData;
    }

    public void setIpServiceCleanData(String ipServiceCleanData) {
        this.ipServiceCleanData = ipServiceCleanData;
    }

    public String getPortServiceCleanData() {
        return portServiceCleanData;
    }

    public void setPortServiceCleanData(String portServiceCleanData) {
        this.portServiceCleanData = portServiceCleanData;
    }

    public String getPortBootleCleanData() {
        return portBootleCleanData;
    }

    public void setPortBootleCleanData(String portBootleCleanData) {
        this.portBootleCleanData = portBootleCleanData;
    }

    public String getUrlApiSolr() {
        return urlApiSolr;
    }

    public void setUrlApiSolr(String urlApiSolr) {
        this.urlApiSolr = urlApiSolr;
    }    
    
}
