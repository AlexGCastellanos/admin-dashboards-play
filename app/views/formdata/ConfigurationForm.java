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
public class ConfigurationForm {
    
    public String pathFileModule;
    public String passwordCertificate;
    
     /**
     * Validates Form<ConfigurationForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(pathFileModule==null || pathFileModule.length()==0){
            errors.add(new ValidationError("pathFileModule", "Debe ingresar un path o ruta donde se guardaran los archivos necesarios para el modulo de administracion."));
        }
        
        if(passwordCertificate==null || passwordCertificate.length()==0){
            errors.add(new ValidationError("passwordCertificate", "Debe ingresar la contrase\u00F1a del certificado que utilizar\u00E1 el administrador"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
    
}
