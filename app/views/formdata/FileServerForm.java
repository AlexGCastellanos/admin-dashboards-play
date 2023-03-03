/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import controllers.FileServers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class FileServerForm {
    public String nomConnection;
    public String ipServerFS;
    public String portServerFS;
    public String pathFileServer;
    public String userFS;
    public String domainFS;
    public String passFS;
    public String pathService;
    public String hiddenCheckedCleanCore;
    
    static Logger logger = Logger.getLogger(FileServerForm.class);
    /**
     * Validates Form<FileServerForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        
        List<ValidationError> errors = new ArrayList<>();
        
        if(nomConnection==null || nomConnection.length()==0){
            errors.add(new ValidationError("nomConnection", "Debe ingresar un nombre para la nueva conexi\u00F3n."));
        }else if(nomConnection.contains(" ")){
            errors.add(new ValidationError("nomConnection", "Debe ingresar un nombre sin espacios"));
        }
        
        if(ipServerFS==null || ipServerFS.length()==0){
            errors.add(new ValidationError("ipServerFS", "Debe ingresar el host o IP del servidor de archivos."));
        }
        
        if(pathFileServer==null || pathFileServer.length()==0){
            errors.add(new ValidationError("pathFileServer", "Debe ingresar el path o ruta de la carpeta donde se encuentran los archivos a indexar."));
        }
        
        if(pathFileServer==null || pathFileServer.length()==0){
            errors.add(new ValidationError("pathFileServer", "Debe ingresar el path o ruta de la carpeta donde se encuentran los archivos a indexar."));
        }
        
        if(userFS==null || userFS.length()==0){
            errors.add(new ValidationError("userFS", "Debe ingresar un usuario del servidor de archivos."));
        }
        
        if(passFS==null || passFS.length()==0){
            errors.add(new ValidationError("passFS", "Debe ingresar la contrase\u00F1a de usuario del servidor de archivos"));
        }
        
        if(pathService == null || pathService.isEmpty()){
            errors.add(new ValidationError("pathService", "Debe ingresar una URL o path del servicio donde se realizar\u00E1 la indexaci\u00F3n"));
        }
               
        /*FileServers fs = new FileServers();
        String outValidate = fs.validateFileServer(ipServerFS, portServerFS, pathFileServer, userFS, domainFS, passFS);
        if(!outValidate.equals("Conectado")){
            errors.add(new ValidationError("otherErrors", outValidate));
        }*/
        
        return errors.isEmpty() ? null : errors;
    }
}
