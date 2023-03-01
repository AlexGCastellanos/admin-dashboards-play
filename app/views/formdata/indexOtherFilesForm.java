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
public class indexOtherFilesForm {
    public String columnUrl;
    public String separator;
    public String buscador_indexCSV;
    public String numHilos;
    public String hiddenMetaCSV;
    
    /**
     * Validates Form<indexOtherFilesForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        if(separator==null || separator.length()==0){
            errors.add(new ValidationError("separator", "Debe ingresar el separador de columnas utilizado en el archivo CSV."));
        }else if(separator.length()>=2){
            errors.add(new ValidationError("separator", "El separador debe ser de un solo car\u00E1cter."));
        }
        
        if(buscador_indexCSV==null || buscador_indexCSV.length()==0){
            errors.add(new ValidationError("buscador_indexCSV", "Debe seleccionar un buscador con el que se asociar\u00E1 el volcado de datos del CSV."));
        }
        
        return errors.isEmpty() ? null : errors;
    }
}
