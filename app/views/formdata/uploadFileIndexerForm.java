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
public class uploadFileIndexerForm {
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
    public String typeUploadIndexer;
    
    
    
     /**
     * Validates Form<uploadFileIndexerForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
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
