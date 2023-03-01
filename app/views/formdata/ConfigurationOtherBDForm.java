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
public class ConfigurationOtherBDForm {
    public String pathModuleDatabases;
    
    /**
     * Validates Form<ConfigurationOtherBDForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(pathModuleDatabases==null || pathModuleDatabases.length()==0){
            errors.add(new ValidationError("pathModuleDatabases", "Debe seleccionar un crawler."));
        }
        return errors.isEmpty() ? null : errors;
    }
}
