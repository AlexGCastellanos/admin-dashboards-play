/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import static controllers.BackUp.doQuery;
import static controllers.BackUp.retrievePublicIP;
import static controllers.BackUp.retrieveSolrPort;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import static play.mvc.Controller.request;
import play.mvc.Result;
import static play.mvc.Results.ok;
import static play.mvc.Results.status;
import play.mvc.Security;

/**
 *
 * @author agarzon
 */
public class CopyCollectionsInfo {

    public HashMap<String, String> fillDirectories() {

        String absoluteDiskPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/copyCollection";
        File copyCollection = new File(absoluteDiskPath);

        String[] directoriesNames = copyCollection.list();
        HashMap<String, String> arrDirectories = new HashMap<>();

        for (String directoryName : directoriesNames) {
            arrDirectories.put(directoryName, directoryName);
        }
        
        return arrDirectories;
    }

    public static File getJsonFile(String directory, String fileName) {

        String absoluteFilePath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/copyCollection/"+directory+"/"+fileName;
        File json = new File(absoluteFilePath);        

        return json;
    }

    @Security.Authenticated(Secured.class)
    public static Result fillFiles() {

        //http://192.168.1.36:8783/solr/admin/cores?action=STATUS&indexInfo=false
        String collection = request().body().asText();

        String absoluteFilesPath = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/copyCollection/" + collection;
        File files = new File(absoluteFilesPath);

        String[] filesNames = files.list();
        String concat = "";

        for (int i = 0; i < filesNames.length; i++) {
            if (i == (filesNames.length - 1)) {
                concat = concat + filesNames[i];
            } else {
                concat = concat + filesNames[i] + ";";
            }
        }

        return ok(concat);
    }

    public LinkedHashMap<String, String> sortHashMapByValues(HashMap<String, String> passedMap) {

        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
