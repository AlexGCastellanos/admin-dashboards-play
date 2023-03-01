package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import models.PropertiesFile;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Controller.request;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import static play.mvc.Http.HeaderNames.USER_AGENT;
import views.formdata.LoadSolrToSolrForm;
import views.formdata.uploadSolrToSolrForm;
import views.html.loadCopySolrToSolr;

public class SolrToSolr extends Controller {

    static Form<LoadSolrToSolrForm> loadURLForm = form(LoadSolrToSolrForm.class);
    static Form<uploadSolrToSolrForm> uploadURLForm = form(uploadSolrToSolrForm.class);

    static Logger logger = Logger.getLogger(SolrToSolr.class);

    public static Result getFields() {

        String req = request().body().asText();
        logger.info("req:" + req);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadOtherPropertiesBD();
        String pathModuleDatabases = pf.getPathModuleDatabases();
        // JsonNode json = request().body().asJson();
        String urlIn = req;

        String reqUrl = pathModuleDatabases + "executeSolrSolr?urlor=" + urlIn;

        JSONObject response = doQuery(reqUrl, "", "POST");
        JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
        JSONArray res = new JSONArray();
        res.put(jsonObj);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonResponse = mapper.readTree(res.toString());
            return ok(jsonResponse);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SolrToSolr.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ok(res.toString());

    }

    public static JSONObject doQuery(String reqUrl, String data, String method) {
        System.out.println("reqUrl " + reqUrl);
        logger.info("reqUrl " + reqUrl);
        JSONObject respuesta = new JSONObject();
        int responseCode;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setDoOutput(true);

            if (!data.equals("")) {
                logger.info("Data--> " + data);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] postData = data.getBytes(StandardCharsets.UTF_8);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }
            }

            responseCode = con.getResponseCode();
            logger.info("Response Code doQuery: " + responseCode);

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
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
            }
            respuesta.put("responseCode", String.valueOf(responseCode));
            respuesta.put("responseSolr", response.toString());
        } catch (IOException ex) {
            logger.error(ex);
        }
        return respuesta;
    }

    @Security.Authenticated(Secured.class)
    public static Result uploadSolrToSolr() {

        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Copiando de Solr a Solr";
        generateLineLog(lineLog);

        Form<uploadSolrToSolrForm> filledFormConfig = uploadURLForm.bindFromRequest();

        if (filledFormConfig.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(loadCopySolrToSolr.render("Copia de Solr a Solr", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormConfig));
        } else {
            try {
                uploadSolrToSolrForm created = filledFormConfig.get();

                PropertiesFile pf = new PropertiesFile();
                pf.loadProperties();
                pf.loadOtherPropertiesBD();
                String pathModuleDatabases = pf.getPathModuleDatabases();
                // JsonNode json = request().body().asJson();
                String urlIn;
                if ((created.dnsInSolrToSolr == null || created.dnsInSolrToSolr.length() == 0 || created.dnsInSolrToSolr.equals(""))) {
                    urlIn = "urlor=http://" + created.ipInSolrToSolr + ":" + created.portInSolrToSolr + "/solr/" + created.collectionNameInSolrToSolr;
                } else {
                    urlIn = "urlor=" + created.dnsInSolrToSolr + "/" + created.collectionNameInSolrToSolr;
                }

                String urlDes;

                if ((created.dnsOutSolrToSolr == null || created.dnsOutSolrToSolr.length() == 0 || created.dnsOutSolrToSolr.equals(""))) {
                    urlDes = "&urldes=http://" + created.ipOutSolrToSolr + ":" + created.portOutSolrToSolr + "/solr/" + created.collectionNameOutSolrToSolr;
                } else {
                    urlDes = "&urldes=" + created.dnsInSolrToSolr + "/" + created.collectionNameInSolrToSolr;
                }

                String fields = "&fields=" + created.fieldsdHiddenSolrToSolr.substring(1, created.fieldsdHiddenSolrToSolr.length());
                String reqUrl = pathModuleDatabases + "executeSolrSolr?" + urlIn + urlDes + "&rows=" + created.rowsOutSolrToSolr + fields + "&numRecordsFrac=" + created.numRecordsFrac;
                JSONObject response = doQuery(reqUrl, "", "POST");
                logger.info(response.toString());
                return redirect(routes.Application.profile());
            } catch (Exception ex) {
                logger.error(ex);
            }

            return null;
        }

    }

    static public String postJSONUrl(HttpURLConnection conn, String json) {
        String resultado = "";
        try {

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html");

            String input = json;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                resultado += output;
            }

        } catch (MalformedURLException e) {
            logger.error("Error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }

        return resultado;
    }

    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }

}
