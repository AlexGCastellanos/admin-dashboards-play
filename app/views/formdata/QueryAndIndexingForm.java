package views.formdata;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.ValidationError;

public class QueryAndIndexingForm {

    public String ipOrigin;
    public String portOrigin;
    public String originCollectionName;
    public String idsQuery;
    public String ipDestination;
    public String portDestination;
    public String destinationCollectionName;
    public String operationSelector;
    public String saveLocation;
    public String jsonFileToIndex;
    
    /**
     * Validates Form<QueryAndIndexingForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    
    public List<ValidationError> validate() {
        
        List<ValidationError> errors = new ArrayList<>();

        String regexIp = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String regexPuerto = "^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[0-5]?([0-9]){0,3}[0-9])$";
        
        if (operationSelector == null || operationSelector.isEmpty()) {
            errors.add(new ValidationError("operationSelector", "Por favor seleccione una operacion a realizar"));
        }
        
        if (operationSelector.equals("Indexar") || operationSelector.equals("Guardar") || operationSelector.equals("Guardar e Indexar")) {
            if (idsQuery == null || idsQuery.isEmpty()) {
                errors.add(new ValidationError("idsQuery", "El campo id no puede ser vacio"));
            } 
        }
        
        if (operationSelector.equals("Indexar Archivo JSON")) {

            if (jsonFileToIndex == null || jsonFileToIndex.isEmpty()) {
                errors.add(new ValidationError("jsonFileToIndex", "El archivo Json no puede ser vacio"));
            }

        } else {

            if (ipOrigin == null || ipOrigin.isEmpty()) {
                errors.add(new ValidationError("ipOrigin", "El campo ip origen no puede ser vacio"));
            } else if (!ipOrigin.matches(regexIp)) {
                errors.add(new ValidationError("ipOrigin", "La ip no tiene el formato correcto xxx.xxx.xxx.xxx"));
            }

            if (portOrigin == null || portOrigin.isEmpty()) {
                errors.add(new ValidationError("portOrigin", "El campo puerto origen no puede ser vacio"));
            } else if (!portOrigin.matches(regexPuerto)) {
                errors.add(new ValidationError("portOrigin", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
            }

            if (originCollectionName == null || originCollectionName.isEmpty()) {
                errors.add(new ValidationError("originCollectionName", "El nombre de la coleccion no puede estar vacio"));
            }
        }

        if (operationSelector.equals("Guardar")) {
            
            if (saveLocation == null || saveLocation.isEmpty()) {
                errors.add(new ValidationError("saveLocation", "La ubicacion no puede ser vacia"));
            }
            
        } else {
            
            if (ipDestination.isEmpty()) {
                errors.add(new ValidationError("ipDestination", "El campo ip destino no puede ser vacio"));
            } else if (!ipDestination.matches(regexIp)) {
                errors.add(new ValidationError("ipDestination", "La IP no tiene el formato correcto xxx.xxx.xxx.xxx"));
            }

            if (portDestination.isEmpty()) {
                errors.add(new ValidationError("portDestination", "El campo puerto destino no puede ser vacio"));
            } else if (!portDestination.matches(regexPuerto)) {
                errors.add(new ValidationError("portDestination", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
            }

            if (destinationCollectionName.isEmpty()) {
                errors.add(new ValidationError("destinationCollectionName", "El nombre de la coleccion no puede ser vacio"));
            }
        }

        if (operationSelector.equals("Guardar e Indexar")) {
            if (saveLocation == null || saveLocation.isEmpty()) {
                errors.add(new ValidationError("saveLocation", "La ubicacion no puede ser vacia"));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

}