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
public class FillLists {
    public LinkedHashMap<String, String> fillDayWeek() {
        LinkedHashMap<String, String> arrListDayWeeks = new LinkedHashMap<>();

        arrListDayWeeks.put(" ", " ");
        arrListDayWeeks.put("Lunes", "Lunes");
        arrListDayWeeks.put("Martes", "Martes");
        arrListDayWeeks.put("Miercoles", "Miercoles");
        arrListDayWeeks.put("Jueves", "Jueves");
        arrListDayWeeks.put("Viernes", "Viernes");
        arrListDayWeeks.put("Sabado", "Sabado");
        arrListDayWeeks.put("Domingo", "Domingo");

        return arrListDayWeeks;
    }

    public LinkedHashMap<String, String> fillDayMonth() {
        LinkedHashMap<String, String> arrListDayMonth = new LinkedHashMap<>();
        arrListDayMonth.put(" "," ");
        for (int i = 1; i < 32; i++) {
            arrListDayMonth.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListDayMonth;
    }
    
    public LinkedHashMap<String, String> fillHours() {
        LinkedHashMap<String, String> arrListHours = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            arrListHours.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListHours;
    }
    
    public LinkedHashMap<String, String> fillMinutes() {
        LinkedHashMap<String, String> arrListMinutes = new LinkedHashMap<>();
        for (int i = 0; i < 60; i++) {
            arrListMinutes.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListMinutes;
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
