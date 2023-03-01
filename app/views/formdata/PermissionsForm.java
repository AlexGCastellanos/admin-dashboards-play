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
public class PermissionsForm {
    public String idPermission;
    public String permission;
    /**
     * Validates Form<AdministradorForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(idPermission==null || idPermission.length()==0){
            errors.add(new ValidationError("idPermission", "Debe ingresar el ID del nuevo permiso"));
        }else if(idPermission.matches("(?i)[a-z][a-z0-9_]*")){
            errors.add(new ValidationError("idPermission", "El ID del permiso debe ser n\u00FAmerico"));
        }
        if(permission==null || permission.length()==0){
            errors.add(new ValidationError("permission", "Debe ingresar la descripci\u00F3n del nuevo permiso"));
        }
        
        
        return errors.isEmpty() ? null : errors;
    }
}
