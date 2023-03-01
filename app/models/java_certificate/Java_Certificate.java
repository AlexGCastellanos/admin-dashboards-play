/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models.java_certificate;
/**
 *
 * @author dduque
 */
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import models.PropertiesFile;

public class Java_Certificate{
    
    public static boolean check()throws Exception {   
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        PublicKey llaveCert=null;
        PublicKey llaveCreangel=null;
        String pass = pf.getPasswordCertificate();
        int diasRes=0;
        boolean exec;
        
        InputStream dirCert;
        dirCert = new FileInputStream(pf.getPathFileModule()+"/cert/"+pf.getNameCertificateP12());

        KeyStore p12 = KeyStore.getInstance("PKCS12");
        p12.load(dirCert, pass.toCharArray());
        Enumeration e = p12.aliases();
            while (e.hasMoreElements()) {
                    String alias = (String) e.nextElement();
                    X509Certificate c = (X509Certificate) p12.getCertificate(alias);
                    llaveCreangel = c.getPublicKey();
                    
        //------Calculo dias restantes del certificado
            Date fechaActual = new Date();
            Date fechaFin = c.getNotAfter();
            long fIni = fechaActual.getTime();
            long fFin = fechaFin.getTime();
            long diferencia = fFin - fIni;
            double dif = Math.floor(diferencia / (1000 * 60 * 60 * 24));
            diasRes =(int) dif;
        }
            
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
	BufferedInputStream in; 
        InputStream ap;
        ap = new FileInputStream(pf.getPathFileModule()+"/cert/"+pf.getNameCertificate());
        in = new BufferedInputStream(ap);
        while (in.available() > 0) {
	    Certificate cert = cf.generateCertificate(in);
            llaveCert = cert.getPublicKey();
        }
        boolean key; //validar que la llave coincide
        boolean certif;//validar que certificado esta vigente
        
        //-----------Validacion
        if(llaveCert == llaveCreangel){
            key = true; //la llave coincide
        }else{
            key = false;
        }

        if(diasRes  > 0){ 
            certif = true;//el certificado esta vigente 
        }else{
            certif = false;
        }
        
        if(key && certif){
            exec = true; //la 2 validaciones son correctas. codigo se ejecuta
        }
        else{
            exec = false; //alguna (o las dos) validaciones no son correctas.
        }
        return exec; //retorna variable que permite ejecutar o no el codigo
    }
    
}