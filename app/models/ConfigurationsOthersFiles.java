/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author atovar
 */
public class ConfigurationsOthersFiles {
    static Logger logger = Logger.getLogger(ConfigurationsOthersFiles.class);
    
    public HashMap<String, String> fillConfigurations() {
        HashMap<String, String> arrConfigurations = new HashMap<>();

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        //arrConfigurations.put("", "Seleccione una configuracion");
                
        File fRaiz = new File(pf.getPathFileModule() + "/filesToUp/configs/");
        if(fRaiz.exists() && fRaiz.isDirectory()){
            File[] files = fRaiz.listFiles(new FileFilter() { @Override public boolean accept(File file) { return file.isDirectory(); } });
            for (File file : files) {
                arrConfigurations.put(file.getName(), file.getName());
            }
        }
        return arrConfigurations;
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
