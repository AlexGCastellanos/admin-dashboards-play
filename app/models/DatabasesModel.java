/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author atovar
 */
public class DatabasesModel {

    public HashMap<String, String> fillDatabases() {
        HashMap<String, String> arrDatabases = new HashMap<>();

        arrDatabases.put("Oracle", "Oracle");
        //arrDatabases.put("Oracle Colciencias", "Oracle Colciencias");
        arrDatabases.put("Oracle Cluster", "Oracle Cluster");
        arrDatabases.put("MySQL", "MySQL");
        arrDatabases.put("SQL Server", "SQL Server");
        arrDatabases.put("Postgrest", "Postgrest");
        arrDatabases.put("Informix", "Informix");
        arrDatabases.put("JTDS", "JTDS");

        return arrDatabases;
    }

    public LinkedHashMap<String, String> sortHashMapByValues(
            HashMap<String, String> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, String> sortedMap
                = new LinkedHashMap<>();

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
