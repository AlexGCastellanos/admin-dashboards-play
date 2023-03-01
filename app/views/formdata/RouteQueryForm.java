package views.formdata;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.ValidationError;

public class RouteQueryForm {
    
    public String urlApiSolr;
    
    /**
     * Validates Form<RouteQueryForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    
    public List<ValidationError> validate() {       
        
        List<ValidationError> errors = new ArrayList<>();
        
        if(urlApiSolr==null || urlApiSolr.length()==0){
            errors.add(new ValidationError("urlApiSolr", "El campo de la url no puede ser vacio"));
        }

        return errors.isEmpty() ? null : errors;
    }

}