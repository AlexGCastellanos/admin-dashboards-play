package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import models.OperationsModel;
import models.PropertiesFile;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Results.badRequest;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import views.formdata.QueryAndIndexingForm;

import views.html.*;

/**
 *
 * @author agarzon
 */
public class ServiceSolrApi extends Controller {

    static Form<QueryAndIndexingForm> queryAndIndexingForm = form(QueryAndIndexingForm.class);

    static Logger logger = Logger.getLogger(ServiceSolrApi.class);

    @Security.Authenticated(Secured.class)
    public static Result sendDataToApi() {

        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }

        String lineLog = ipAddress + "<;>" + username + "<;>Haciendo pruebas del metodo de validacion";
        generateLineLog(lineLog);

        //Obtengo formulario de consulta e indexacion
        Form<QueryAndIndexingForm> filledQueryAndIndexForm = queryAndIndexingForm.bindFromRequest();

        HashMap<String, String> arrOperations;
        HashMap<String, String> arrOperationsSort;
        OperationsModel operations = new OperationsModel();
        arrOperations = operations.fillOperations();
        arrOperationsSort = operations.sortHashMapByValues(arrOperations);

        HashMap<String, String> arrDirectories;
        HashMap<String, String> arrDirectoriesSort;
        CopyCollectionsInfo copyCollections = new CopyCollectionsInfo();
        arrDirectories = copyCollections.fillDirectories();
        arrDirectoriesSort = copyCollections.sortHashMapByValues(arrDirectories);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadUrlApiConfig();

        String urlApiSolr = pf.getUrlApiSolr();

        logger.info("El valor de la variable urlApiSolr es --> " + urlApiSolr);

        if (filledQueryAndIndexForm.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(admin_collections_solr.render("Consultar, guardar e indexar", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledQueryAndIndexForm, arrOperationsSort, arrDirectoriesSort));
        } else {

            QueryAndIndexingForm created = filledQueryAndIndexForm.get();
            StringBuilder response = new StringBuilder("");

            try {

                //Obtengo los campos del formulario                
                String ipOrigin = created.ipOrigin;
                String portOrigin = created.portOrigin;
                String originCollectionName = created.originCollectionName;
                String idsQuery = (created.idsQuery == null || created.idsQuery.isEmpty()) ? " " : created.idsQuery;
                String ipDestination = created.ipDestination;
                String portDestination = created.portDestination;
                String destinationCollectionName = created.destinationCollectionName;
                String operationSelector = created.operationSelector;
                String directoryName = created.directorySelector;
                String jsonName = created.jsonSelected;

                logger.info("El nombre del archivo json seleccionado, es: " + jsonName);

                String jsonToText = " ";

                if (operationSelector.equals("Indexar Archivo JSON")) {

                    if (!(jsonName.isEmpty())) {

                        File jsonFile = CopyCollectionsInfo.getJsonFile(directoryName, jsonName);
                        StringBuilder textoJsonLeido = new StringBuilder();

                        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {

                            String linea;
                            while ((linea = reader.readLine()) != null) {
                                textoJsonLeido.append(linea);
                            }

                            jsonToText = textoJsonLeido.toString();

                        }
                    }
                }

                JSONObject formData = new JSONObject();

                formData.put("ip", ipOrigin);
                formData.put("puerto", portOrigin);
                formData.put("origen", originCollectionName);
                formData.put("id", idsQuery);
                formData.put("ipD", ipDestination);
                formData.put("puertoD", portDestination);
                formData.put("destino", destinationCollectionName);
                formData.put("operacion", operationSelector);
                formData.put("archivoJson", jsonToText);

                logger.info("Los datos del formulario en formato json son: " + formData.toString());

                logger.info("La url de la API es: " + urlApiSolr + "   +++......+++");

                URL url = new URL(urlApiSolr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-type", "application/json");
                con.setDoOutput(true);

                logger.info("formData --->" + formData);

                OutputStream wr = con.getOutputStream();

                wr.write(formData.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                logger.info("Response Code consulta a API: " + responseCode);

                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    }
                } else {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getErrorStream()))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    }
                    return badRequest(response.toString());
                }

                con.disconnect();
                logger.info("El codigo de respuesta es: " + responseCode + "\n" + "El cuerpo de la respuesta es:\n\n" + response.toString());
                return redirect(routes.Application.profile());

            } catch (IOException ex) {
                logger.error("La URL a la que se intenta enviar el formulario, no existe:\n\n" + ex.getMessage());
            }

            return null;
        }
    }

    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }
}
