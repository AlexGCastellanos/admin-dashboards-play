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
public class GroupsForm {
    public String nomGrupo;
    public String hiddenProfileGroup;
    
    /**
     * Validates Form<AdministradorForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(nomGrupo==null || nomGrupo.length()==0){
            errors.add(new ValidationError("nomGrupo", "Debe ingresar el nombre del nuevo grupo"));
        }
        
        if(hiddenProfileGroup==null || hiddenProfileGroup.length()==0){
            errors.add(new ValidationError("hiddenProfileGroup", "Debe seleccionar al menos un perfil para el grupo"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
}
