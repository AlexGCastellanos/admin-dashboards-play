/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import java.util.ArrayList;
import java.util.List;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class DatabasesForm {
    public String nomConnection;
    public String diascampoFecha;
    public String separatorBD;
    public String query;
    public String fieldURL;
    public String date1;
    public String date2;
    public String fieldDate;
    public String hiddenChecked;
public String databases;
    public String serverBD;
    public String portBD;
    public String nameBD;
    public String instanceBD;
    public String userBD;
    public String passBD;
    public String disFracView;
    public String numFracView;
    public String fieldOrderBy;
    public String pathService;
    public String hiddenCheckedCleanCore;
    public String cleanType;
    public String hiddenAllFile;
    
    /**
     * Validates Form<BasesdatosForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        
        //System.out.println(nomConexion);
        if(nomConnection==null || nomConnection.length()==0){
            errors.add(new ValidationError("nomConnection", "Debe ingresar un nombre para la nueva conexi\u00F3n."));
        }else if(nomConnection.contains(" ")){
            errors.add(new ValidationError("nomConnection", "Debe ingresar un nombre sin espacios"));
        }
        
        if(separatorBD==null || separatorBD.length()==0){
            errors.add(new ValidationError("separatorBD", "Debe ingresar un separador de campos con el que se generar\u00E1 el volcado de la Base de datos"));
        }
        if(cleanType==null || cleanType.length()==0){
            errors.add(new ValidationError("cleanType", "Debe seleccionar el tipo de limpieza de datos"));
        }
        
        if(query==null || query.length()==0){
            errors.add(new ValidationError("query", "Debe ingresar una consulta para ser ejecutada en la Base de Datos"));
        }
        
        switch (hiddenChecked) {
            case "false":
                if(date1 == null || date1.isEmpty() || date1.length()==0){
                    errors.add(new ValidationError("date1", "Debe ingresar una fecha Inicial"));
                }else if(date2 == null || date2.isEmpty() || date2.length()==0){
                    errors.add(new ValidationError("date2", "Debe ingresar una fecha Final"));
                }else if(fieldDate == null || fieldDate.isEmpty() || fieldDate.length()==0){
                    errors.add(new ValidationError("fieldDate", "Debe ingresar un nombre de campo fecha"));
                }break;
            case "true":
                date1="";
                date2="";
               
                break;
        }
        
        if(hiddenAllFile.equals("false")){
                if(diascampoFecha == null || diascampoFecha.isEmpty() || diascampoFecha.length()==0){
                    errors.add(new ValidationError("diascampoFecha", "Debe ingresar una n\u00FAmero de dias"));
                }else if(fieldDate == null || fieldDate.isEmpty() || fieldDate.length()==0){
                    errors.add(new ValidationError("fieldDate", "Debe ingresar un nombre de campo fecha"));
                }
        }
          
        if(databases == null || databases.isEmpty()){
            errors.add(new ValidationError("databases", "Debe seleccionar alg\u00FAn tipo de Base de Datos"));
        } 

        if(serverBD == null || serverBD.isEmpty()){
            errors.add(new ValidationError("serverBD", "Debe ingresar un nombre de host o IP de la Base de datos"));
        }

        if(portBD == null || portBD.isEmpty()){
            portBD = "";
        }

        if(nameBD == null || nameBD.isEmpty()){
            errors.add(new ValidationError("nameBD", "Debe ingresar el nombre de la Base de datos"));
        }

        if(instanceBD == null || instanceBD.isEmpty()){
            instanceBD = "";
        }

        if(userBD == null || userBD.isEmpty()){
            errors.add(new ValidationError("userBD", "Debe ingresar un nombre de usuario de la Base de datos"));
        }

        if(passBD == null || passBD.isEmpty()){
            errors.add(new ValidationError("passBD", "Debe ingresar la contrase\u00F1a de usuario de la Base de datos"));
        }

        switch (disFracView) {
            case "true":
                if(numFracView == null || numFracView.isEmpty()){
                    errors.add(new ValidationError("numFracView", "Debe ingresar un n\u00FAmero de registros por el que desea fraccionar la vista"));
                }
                if(fieldOrderBy == null || fieldOrderBy.isEmpty()){
                    errors.add(new ValidationError("fieldOrderBy", "Debe ingresar un campo de ordenamiento para poder realizar el fraccionamiento de la vista"));
                }
                break;
        }
        
        if(pathService == null || pathService.isEmpty()){
            errors.add(new ValidationError("pathService", "Debe ingresar una URL o path del servicio donde se realizar\u00E1 la indexaci\u00F3n"));
        }
        
         return errors.isEmpty() ? null : errors;
    }
    
}