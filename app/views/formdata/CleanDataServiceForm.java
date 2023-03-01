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
public class CleanDataServiceForm {
    public String ipServiceCleanData;
    public String portServiceCleanData;
    public String portBootleCleanData;
    
    /**
     * Validates Form<CleanDataServiceForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();      

        
        if(ipServiceCleanData==null || ipServiceCleanData.length()==0){
            errors.add(new ValidationError("ipServiceCleanData", "Debe ingresar la IP del servicio de limpieza de datos."));
        }
        
        if(portServiceCleanData==null || portServiceCleanData.length()==0){
            errors.add(new ValidationError("portServiceCleanData", "Debe ingresar el puerto del servicio de limpieza de datos."));
        }
        
        if(portBootleCleanData==null || portBootleCleanData.length()==0){
            errors.add(new ValidationError("portBootleCleanData", "Debe ingresar el segundo puerto del servicio de limpieza de datos."));
        }
        
        return errors.isEmpty() ? null : errors;
    }
}
