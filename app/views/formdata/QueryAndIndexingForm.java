package views.formdata;

import java.util.ArrayList;
import java.util.List;
import models.ConsultaSolrService;
import org.json.JSONObject;
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
    
    /**
     * Validates Form<QueryAndIndexingForm>. Called automatically in the
     * controller by bindFromRequest(). Checks to see that email and password
     * are valid credentials.
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

        if (operationSelector.equals("Indexar")) {

            boolean ipValid = true, portValid = true, collectionNameValid = true, ipDestinationValid = true, portDestinationValid = true;

            if (ipOrigin == null || ipOrigin.isEmpty()) {
                errors.add(new ValidationError("ipOrigin", "El campo ip origen no puede ser vacio"));
                ipValid = false;
            } else if (!ipOrigin.matches(regexIp)) {
                errors.add(new ValidationError("ipOrigin", "La ip no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipValid = false;
            }

            if (portOrigin == null || portOrigin.isEmpty()) {
                errors.add(new ValidationError("portOrigin", "El campo puerto origen no puede ser vacio"));
                portValid = false;
            } else if (!portOrigin.matches(regexPuerto)) {
                errors.add(new ValidationError("portOrigin", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portValid = false;
            }

            if (originCollectionName == null || originCollectionName.isEmpty()) {
                errors.add(new ValidationError("originCollectionName", "El nombre de la coleccion no puede estar vacio"));
                collectionNameValid = false;
            } else if ((ipValid && portValid)) {

                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipOrigin, portOrigin, originCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");

                    if (status != 0) {
                        errors.add(new ValidationError("originCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                        collectionNameValid = false;
                    }

                } else {
                    errors.add(new ValidationError("originCollectionName", "Error en la consulta, verifica si el puerto, la ip y la coleccion son correctos"));
                    collectionNameValid = false;
                }
            }

            if (idsQuery == null || idsQuery.isEmpty()) {
                errors.add(new ValidationError("idsQuery", "El campo id no puede ser vacio"));
            } else if ((ipValid && portValid && collectionNameValid)) {

                String respuestaConsulta = ConsultaSolrService.consultarColeccion(ipOrigin, portOrigin, originCollectionName, idsQuery);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject jsonConsulta = new JSONObject(respuestaConsulta);
                    JSONObject responseHeader = jsonConsulta.getJSONObject("responseHeader");
                    Integer status = responseHeader.getInt("status");
                    JSONObject respuesta = jsonConsulta.getJSONObject("response");
                    Integer numFound = respuesta.getInt("numFound");
                    if (status != 0 || numFound == 0) {
                        errors.add(new ValidationError("idsQuery", "El id ingresado no existe en la coleccion de origen"));
                    }

                } else {
                    errors.add(new ValidationError("idsQuery", "Error en la consulta, verifica si los datos de id, ip, puerto o coleccion son correctos"));
                }
            }

            if (ipDestination.isEmpty()) {
                errors.add(new ValidationError("ipDestination", "El campo ip destino no puede ser vacio"));
                ipDestinationValid = false;
            } else if (!ipDestination.matches(regexIp)) {
                errors.add(new ValidationError("ipDestination", "La IP no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipDestinationValid = false;
            }

            if (portDestination.isEmpty()) {
                errors.add(new ValidationError("portDestination", "El campo puerto destino no puede ser vacio"));
                portDestinationValid = false;
            } else if (!portDestination.matches(regexPuerto)) {
                errors.add(new ValidationError("portDestination", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portDestinationValid = false;
            }

            if (destinationCollectionName.isEmpty()) {
                errors.add(new ValidationError("destinationCollectionName", "El nombre de la coleccion no puede ser vacio"));
            } else if ((ipDestinationValid && portDestinationValid)) {
                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipDestination, portDestination, destinationCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");
                    if (status != 0) {
                        errors.add(new ValidationError("destinationCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                    }

                } else {
                    errors.add(new ValidationError("destinationCollectionName", "Error en la consulta, verifica si el puerto, la ip y la coleccion son correctos"));
                }
            }
        }

        if (operationSelector.equals("Guardar")) {

            boolean ipValid = true, portValid = true, collectionNameValid = true;

            if (ipOrigin == null || ipOrigin.isEmpty()) {
                errors.add(new ValidationError("ipOrigin", "El campo ip origen no puede ser vacio"));
                ipValid = false;
            } else if (!ipOrigin.matches(regexIp)) {
                errors.add(new ValidationError("ipOrigin", "La ip no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipValid = false;
            }

            if (portOrigin == null || portOrigin.isEmpty()) {
                errors.add(new ValidationError("portOrigin", "El campo puerto origen no puede ser vacio"));
                portValid = false;
            } else if (!portOrigin.matches(regexPuerto)) {
                errors.add(new ValidationError("portOrigin", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portValid = false;
            }

            if (originCollectionName == null || originCollectionName.isEmpty()) {
                errors.add(new ValidationError("originCollectionName", "El nombre de la coleccion no puede estar vacio"));
                collectionNameValid = false;
            } else if ((ipValid && portValid)) {

                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipOrigin, portOrigin, originCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");

                    if (status != 0) {
                        errors.add(new ValidationError("originCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                        collectionNameValid = false;
                    }

                } else {
                    errors.add(new ValidationError("originCollectionName", "Error en la consulta, verifica si el puerto, la ip y la coleccion son correctos"));
                    collectionNameValid = false;
                }
            }

            if (idsQuery == null || idsQuery.isEmpty()) {
                errors.add(new ValidationError("idsQuery", "El campo id no puede ser vacio"));
            } else if ((ipValid && portValid && collectionNameValid)) {
                String respuestaConsulta = ConsultaSolrService.consultarColeccion(ipOrigin, portOrigin, originCollectionName, idsQuery);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject jsonConsulta = new JSONObject(respuestaConsulta);
                    JSONObject responseHeader = jsonConsulta.getJSONObject("responseHeader");
                    Integer status = responseHeader.getInt("status");
                    JSONObject respuesta = jsonConsulta.getJSONObject("response");
                    Integer numFound = respuesta.getInt("numFound");
                    if (status != 0 || numFound == 0) {
                        errors.add(new ValidationError("idsQuery", "El id ingresado no existe en la coleccion de origen"));
                    }

                } else {
                    errors.add(new ValidationError("idsQuery", "Error en la consulta, verifica si los datos de id, ip, puerto o coleccion son correctos"));
                }
            }
        }

        if (operationSelector.equals("Guardar e Indexar")) {

            boolean ipValid = true, portValid = true, collectionNameValid = true, ipDestinationValid = true, portDestinationValid = true;

            if (ipOrigin == null || ipOrigin.isEmpty()) {
                errors.add(new ValidationError("ipOrigin", "El campo ip origen no puede ser vacio"));
                ipValid = false;
            } else if (!ipOrigin.matches(regexIp)) {
                errors.add(new ValidationError("ipOrigin", "La ip no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipValid = false;
            }

            if (portOrigin == null || portOrigin.isEmpty()) {
                errors.add(new ValidationError("portOrigin", "El campo puerto origen no puede ser vacio"));
                portValid = false;
            } else if (!portOrigin.matches(regexPuerto)) {
                errors.add(new ValidationError("portOrigin", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portValid = false;
            }

            if (originCollectionName == null || originCollectionName.isEmpty()) {
                errors.add(new ValidationError("originCollectionName", "El nombre de la coleccion no puede estar vacio"));
                collectionNameValid = false;
            } else if ((ipValid && portValid)) {

                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipOrigin, portOrigin, originCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");

                    if (status != 0) {
                        errors.add(new ValidationError("originCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                        collectionNameValid = false;
                    }

                } else {
                    errors.add(new ValidationError("originCollectionName", "Error en la consulta, verifica si el puerto, la ip y la coleccion son correctos"));
                    collectionNameValid = false;
                }
            }

            if (idsQuery == null || idsQuery.isEmpty()) {
                errors.add(new ValidationError("idsQuery", "El campo id no puede ser vacio"));
            } else if ((ipValid && portValid && collectionNameValid)) {

                String respuestaConsulta = ConsultaSolrService.consultarColeccion(ipOrigin, portOrigin, originCollectionName, idsQuery);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject jsonConsulta = new JSONObject(respuestaConsulta);
                    JSONObject responseHeader = jsonConsulta.getJSONObject("responseHeader");
                    Integer status = responseHeader.getInt("status");
                    JSONObject respuesta = jsonConsulta.getJSONObject("response");
                    Integer numFound = respuesta.getInt("numFound");
                    if (status != 0 || numFound == 0) {
                        errors.add(new ValidationError("idsQuery", "El id ingresado no existe en la coleccion de origen"));
                    }

                } else {
                    errors.add(new ValidationError("idsQuery", "Error en la consulta, verifica si los datos de id, ip, puerto o coleccion son correctos"));
                }
            }

            if (ipDestination.isEmpty()) {
                errors.add(new ValidationError("ipDestination", "El campo ip destino no puede ser vacio"));
                ipDestinationValid = false;
            } else if (!ipDestination.matches(regexIp)) {
                errors.add(new ValidationError("ipDestination", "La IP no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipDestinationValid = false;
            }

            if (portDestination.isEmpty()) {
                errors.add(new ValidationError("portDestination", "El campo puerto destino no puede ser vacio"));
                portDestinationValid = false;
            } else if (!portDestination.matches(regexPuerto)) {
                errors.add(new ValidationError("portDestination", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portDestinationValid = false;
            }

            if (destinationCollectionName.isEmpty()) {
                errors.add(new ValidationError("destinationCollectionName", "El nombre de la coleccion no puede ser vacio"));
            } else if ((ipDestinationValid && portDestinationValid)) {
                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipDestination, portDestination, destinationCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");
                    if (status != 0) {
                        errors.add(new ValidationError("destinationCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                    }

                } else {
                    errors.add(new ValidationError("destinationCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                }
            }
        }

        if (operationSelector.equals("Indexar Archivo JSON")) {
            
            boolean ipDestinationValid = true, portDestinationValid = true;
            
            if (ipDestination.isEmpty()) {
                errors.add(new ValidationError("ipDestination", "El campo ip destino no puede ser vacio"));
                ipDestinationValid = false;
            } else if (!ipDestination.matches(regexIp)) {
                errors.add(new ValidationError("ipDestination", "La IP no tiene el formato correcto xxx.xxx.xxx.xxx"));
                ipDestinationValid = false;
            }

            if (portDestination.isEmpty()) {
                errors.add(new ValidationError("portDestination", "El campo puerto destino no puede ser vacio"));
                portDestinationValid = false;
            } else if (!portDestination.matches(regexPuerto)) {
                errors.add(new ValidationError("portDestination", "El puerto no tiene el formato correcto, debe ser un numero entre 0 y 65535"));
                portDestinationValid = false;
            }

            if (destinationCollectionName.isEmpty()) {
                errors.add(new ValidationError("destinationCollectionName", "El nombre de la coleccion no puede ser vacio"));
            } else if ((ipDestinationValid && portDestinationValid)) {
                String respuestaConsulta = ConsultaSolrService.consultarSchema(ipDestination, portDestination, destinationCollectionName);

                if (!respuestaConsulta.equals("error")) {

                    JSONObject consulta = new JSONObject(respuestaConsulta);
                    Integer status = consulta.getJSONObject("responseHeader").getInt("status");
                    if (status != 0) {
                        errors.add(new ValidationError("destinationCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                    }

                } else {
                    errors.add(new ValidationError("destinationCollectionName", "Error consultando esta coleccion, verifica si existe o si esta escrita correctamente"));
                }
            }
        }

        return errors.isEmpty() ? null : errors;

    }

}
