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
public class UsersForm {
    public String nomUser;
    public String email;
    public String password;
    public String hiddenGroup;
    
    /**
     * Validates Form<UserForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(nomUser==null || nomUser.length()==0){
            errors.add(new ValidationError("nomUser", "Debe ingresar el nombre del nuevo usuario"));
        }
        
        if(email==null || email.length()==0){
            errors.add(new ValidationError("email", "Debe ingresar el correo electr\u00F3nico del nuevo usuario"));
        }else if(!email.contains("@")){
            errors.add(new ValidationError("email", "Debe ingresar un correo electr\u00F3nico valido"));
        }
        
        if(password==null || password.length()==0){
            errors.add(new ValidationError("password", "Debe ingresar la contrase\u00FA nuevo usuario"));
        }
        
        if(hiddenGroup==null || hiddenGroup.length()==0){
            errors.add(new ValidationError("hiddenGroup", "Debe seleccionar un grupo al que pertenecer\u00E1 el usuario"));
        }
        
        return errors.isEmpty() ? null : errors;
    }
}
