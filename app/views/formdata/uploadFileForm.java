/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class uploadFileForm {
    public String nomConexion;
    public String pathFile;
    public String buscador;
    public String checkedCleanDataExcel;
    public String checkedCleanDataCore;
    public String pathFileClean;
    public String nameScript;
    public String pathFileURL;
    public String pathFileSFTPHost;
    public String pathFileSFTPUsername;
    public String pathFileSFTPPass;
    public String pathFileSFTP;
    public String typeUpload;
    public String typeUploadIndexer;
    
    
    
     /**
     * Validates Form<uploadFileForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(nomConexion==null || nomConexion.length()==0){
            errors.add(new ValidationError("nomConexion", "Debe ingresar un nombre para la nueva conexi\u00F3n."));
        }else if(nomConexion.contains(" ")){
            errors.add(new ValidationError("nomConexion", "Debe ingresar un nombre sin espacios"));
        }else if(!nomConexion.matches("^[a-zA-Z]*$")){
            errors.add(new ValidationError("nomConexion", "Debe ingresar un nombre sin caracteres especiales"));
        }
        
        if(buscador==null || buscador.length()==0){
            errors.add(new ValidationError("buscador", "Debe seleccionar un buscador"));
        }
        
        if(checkedCleanDataExcel==null || checkedCleanDataExcel.length()==0){
            checkedCleanDataExcel = "";
        }
        
        if(checkedCleanDataCore==null || checkedCleanDataCore.length()==0){
            checkedCleanDataCore = "";
        }
        
        return errors.isEmpty() ? null : errors;
    }
    
}
