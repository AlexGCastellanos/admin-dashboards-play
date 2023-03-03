/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author bgarzon
 */
public class ConsultaSolrService {
    
    static Logger logger = Logger.getLogger(ConsultaSolrService.class);

    private static String[] idsArray(String ids) {

        //Genero un arreglo a partir de la cadena limpia
        String[] idsArray = ids.split("\\|\\|");

        for (int i = 0; i < idsArray.length; i++) {
            //Limpio la cadena de texto de los ids
            idsArray[i] = idsArray[i].trim();
        }

        return idsArray;

    }

    public static String consultarColeccion(String ip, String puerto, String origen, String ids) {

        try {

            //Formando la url de origen de consulta
            String urlSolr = "http://" + ip + ":" + puerto + "/solr/" + origen + "/select?";

            //Genero un arreglo a partir de la cadena limpia
            String[] idsArray = idsArray(ids);

            StringBuilder queryParam = new StringBuilder();

            //Doy formato URL por cada id en el arreglo
            for (int i = 0; i < idsArray.length; i++) {
                if (i == (idsArray.length - 1)) {
                    queryParam.append("id:").append(idsArray[i]);
                } else {
                    queryParam.append("id:").append(idsArray[i]).append("%20OR%20");
                }
            }
            
            logger.info("Los parametros id quedaron como: " + queryParam.toString());
            URL solrOrigen = new URL(urlSolr + "fq=" + queryParam.toString() + "&q=*:*&wt=json");

            HttpURLConnection connOrigen = (HttpURLConnection) solrOrigen.openConnection();
            connOrigen.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connOrigen.getInputStream()));

            StringBuilder resultado = new StringBuilder();
            String linea;

            while ((linea = rd.readLine()) != null) {
                resultado.append(linea);
            }

            connOrigen.disconnect();

            return resultado.toString();

        } catch (IOException ex) {
            logger.error("Error en consulta de coleccion:" + ex.getMessage());
            return "error";
        }
        
    }

    public static String consultarSchema(String ip, String puerto, String coleccion) {

        try {
            //Obteniendo url para consultar schema Origen
            String schema = "http://" + ip + ":" + puerto + "/solr/" + coleccion + "/schema";
            URL urlSchema = new URL(schema);

            HttpURLConnection connOrigen = (HttpURLConnection) urlSchema.openConnection();
            connOrigen.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(connOrigen.getInputStream()));

            StringBuilder resultado = new StringBuilder();
            String linea;

            while ((linea = rd.readLine()) != null) {
                resultado.append(linea);
            }

            connOrigen.disconnect();
            return resultado.toString();

        } catch (IOException ex) {
            logger.error("Error en consulta de coleccion:" + ex.getMessage());
            return "error";
        }
    }

}
