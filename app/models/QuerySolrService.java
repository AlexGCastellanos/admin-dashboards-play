/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 *
 * @author bgarzon
 */
public class QuerySolrService {

    static Logger logger = Logger.getLogger(QuerySolrService.class);

//    private static String[] idsArray(String ids) {
//
//        //Genero un arreglo a partir de la cadena limpia
//        String[] idsArray = ids.split("\\|\\|");
//
//        for (int i = 0; i < idsArray.length; i++) {
//            //Limpio la cadena de texto de los ids
//            idsArray[i] = idsArray[i].trim();
//        }
//
//        return idsArray;
//
//    }

    public static String collectionQuery(String ip, String port, String collection, String ids) {

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadConfiguracionPruebaIndexar();

        String urlApiSolr = pf.getUrlApiSolr();

        try {

            URL urlQueryCollection = new URL(urlApiSolr);
            HttpURLConnection conn = (HttpURLConnection) urlQueryCollection.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoOutput(true);

            JSONObject queryData = new JSONObject();
            queryData.put("ip", ip);
            queryData.put("port", port);
            queryData.put("collection", collection);
            queryData.put("ids", ids);

            JSONObject queryCollection = new JSONObject();
            queryCollection.put("queryCollection", queryData);
            logger.info("Los datos para la validacion de ids de coleccion son: " + queryCollection.toString());

            OutputStream wr = conn.getOutputStream();
            wr.write(queryCollection.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            logger.info("Response Code de la validacion de la coleccion con la url del servlet API es: " + responseCode);

            StringBuilder response = new StringBuilder();

            if (responseCode == 200) {

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                conn.disconnect();
                return response.toString();
            }
        } catch (IOException ex) {
            logger.error("Error en consulta de la url de la API:" + ex.getMessage());
        }
        return null;
    }

    public static String schemaQuery(String ip, String port, String collection) {

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadConfiguracionPruebaIndexar();

        String urlApiSolr = pf.getUrlApiSolr();

        try {
            URL urlQuerySchema = new URL(urlApiSolr);
            HttpURLConnection conn = (HttpURLConnection) urlQuerySchema.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoOutput(true);

            JSONObject queryData = new JSONObject();
            queryData.put("ip", ip);
            queryData.put("port", port);
            queryData.put("collection", collection);

            JSONObject querySchema = new JSONObject();
            querySchema.put("querySchema", queryData);
            logger.info("Los datos para la validacion a Schema son: " + querySchema.toString());

            OutputStream wr = conn.getOutputStream();
            wr.write(querySchema.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            logger.info("Response Code de la validacion del schema con la url del servlet API es: " + responseCode);

            StringBuilder response = new StringBuilder();

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    logger.info("El response de la consulta al schema es:" + response.toString());
                }
                conn.disconnect();
                logger.info("El response de la consulta al schema es:" + response.toString());
                return response.toString();
            }            
        } catch (IOException ex) {
            logger.error("Error en consulta del schema:" + ex.getMessage());
        }
        return null;
    }

}
