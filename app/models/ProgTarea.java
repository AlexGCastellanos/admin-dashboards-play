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
public class ProgTarea {
    
    public LinkedHashMap<String, String> fillDiaSemana() {
        LinkedHashMap<String, String> arrListaSemanas = new LinkedHashMap<>();

        arrListaSemanas.put(" ", " ");
        arrListaSemanas.put("Lunes", "Lunes");
        arrListaSemanas.put("Martes", "Martes");
        arrListaSemanas.put("Miercoles", "Miercoles");
        arrListaSemanas.put("Jueves", "Jueves");
        arrListaSemanas.put("Viernes", "Viernes");
        arrListaSemanas.put("Sabado", "Sabado");
        arrListaSemanas.put("Domingo", "Domingo");

        return arrListaSemanas;
    }

    public LinkedHashMap<String, String> fillDiasMes() {
        LinkedHashMap<String, String> arrListaDiaMes = new LinkedHashMap<>();
        arrListaDiaMes.put(" "," ");
        for (int i = 1; i < 32; i++) {
            arrListaDiaMes.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListaDiaMes;
    }
    
    public LinkedHashMap<String, String> fillHoras() {
        LinkedHashMap<String, String> arrListaHoras = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            arrListaHoras.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListaHoras;
    }
    
    public LinkedHashMap<String, String> fillMinutos() {
        LinkedHashMap<String, String> arrListaMinutos = new LinkedHashMap<>();
        for (int i = 0; i < 60; i++) {
            arrListaMinutos.put(String.valueOf(i),String.valueOf(i));
        }
        return arrListaMinutos;
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
