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
public class ProfilesForm {
 
    public String nomPerfil;
    public String hiddenPermissions;
    
    /**
     * Validates Form<ProfilesForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(nomPerfil==null || nomPerfil.length()==0){
            errors.add(new ValidationError("nomPerfil", "Debe ingresar el nombre del nuevo perfil"));
        }
        
        if(hiddenPermissions==null || hiddenPermissions.length()==0){
            errors.add(new ValidationError("hiddenPermissions", "Debe seleccionar al menos un permiso para el perfil"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
}
