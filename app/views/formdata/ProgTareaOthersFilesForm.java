/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import static controllers.UploadOtherFiles.fillTareasOthersFiles;
import static controllers.UploadOtherFiles.verificarTareaOthersFiles;
import java.util.ArrayList;
import java.util.List;
import models.PropertiesFile;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class ProgTareaOthersFilesForm {
    public String idTarea;
    public String porMinutosTarea;
    public String porHorasTarea;
    public String diaTarea;
    public String diaSemanaTarea;
    public String diaMesTarea;
    public String horaTarea;
    public String minTarea;
    public String conexionProg;
    
    public String  hiddenCheckedMinutos, hiddenCheckedHoras, hiddenCheckedDias, hiddenCheckedDiaSemana, hiddenCheckedDiaMes;
    
    public String selConexion;
    
    /**
     * Validates Form<ProgTareaOthersFilesForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
    
        List<ValidationError> errors = new ArrayList<>();
         
        if(hiddenCheckedMinutos != null && hiddenCheckedMinutos.equals("true")){
            if(porMinutosTarea == null || porMinutosTarea.trim().isEmpty()){
                errors.add(new ValidationError("porMinutosTarea", "Debe ingresar la frecuencia en minutos en que se ejecutar\u00E1 la tarea"));
            }
            porHorasTarea="";
            diaTarea="";
            diaSemanaTarea="";
            diaMesTarea="";
        }else if(hiddenCheckedHoras != null && hiddenCheckedHoras.equals("true")){
            if(porHorasTarea == null || porHorasTarea.trim().isEmpty()){
                errors.add(new ValidationError("porHorasTarea", "Debe ingresar la frecuencia en horas en que se ejecutar\u00E1 la tarea"));
            }
            porMinutosTarea="";
            diaTarea="";
            diaSemanaTarea="";
            diaMesTarea="";
        }else if(hiddenCheckedDias != null && hiddenCheckedDias.equals("true")){
            if(diaTarea == null || diaTarea.trim().isEmpty()){
                errors.add(new ValidationError("diaTarea", "Debe ingresar el n\uu00F3mero de d\u00EDas de frecuencia en que se ejecutar\u00E1 la tarea"));
            }
            porMinutosTarea="";
            porHorasTarea="";
            diaSemanaTarea="";
            diaMesTarea="";
        }else if(hiddenCheckedDiaSemana != null && hiddenCheckedDiaSemana.equals("true")){
            if(diaSemanaTarea == null || diaSemanaTarea.trim().isEmpty()){
                errors.add(new ValidationError("diaSemanaTarea", "Debe seleccionar el d\u00EDa de la semana en que se ejecutar\u00E1 la tarea"));
            }
            porMinutosTarea="";
            porHorasTarea="";
            diaTarea="";
            diaMesTarea="";
        }else if(hiddenCheckedDiaMes != null && hiddenCheckedDiaMes.equals("true")){
            if(diaMesTarea == null || diaMesTarea.trim().isEmpty()){
                errors.add(new ValidationError("diaMesTarea", "Debe seleccionar el d\u00EDa del mes en que se ejecutar\u00E1 la tarea"));
            }
            porMinutosTarea="";
            porHorasTarea="";
            diaSemanaTarea="";
            diaTarea="";
        }
        
        if((hiddenCheckedMinutos == null || hiddenCheckedMinutos.equals("false")) && (hiddenCheckedHoras == null || hiddenCheckedHoras.equals("false")) && (hiddenCheckedDias == null || hiddenCheckedDias.equals("false")) && (hiddenCheckedDiaSemana == null || hiddenCheckedDiaSemana.equals("false")) && (hiddenCheckedDiaMes == null || hiddenCheckedDiaMes.equals("false"))){
            errors.add(new ValidationError("otrosErrores", "Debe seleccionar una opci\u00F3n para programar la tarea"));
        }
        
        if(hiddenCheckedDias != null && hiddenCheckedDias.equals("true") || (hiddenCheckedDiaSemana != null && hiddenCheckedDiaSemana.equals("true")) || (hiddenCheckedDiaMes != null && hiddenCheckedDiaMes.equals("true"))){
            if(horaTarea == null || horaTarea.isEmpty())
            {
                errors.add(new ValidationError("horaTarea", "Debe ingresar la hora en que se ejecutar\u00E1 la tarea"));
            }

            if(minTarea == null || minTarea.isEmpty())
            {
                errors.add(new ValidationError("horaTarea", "Debe ingresar el minuto en que se ejecutar\u00E1 la tarea"));
            }
        }
        
        if(selConexion == null || selConexion.isEmpty()){
            errors.add(new ValidationError("selConexion", "Debe seleccionar la conexi\u00F3n que se programar\u00E1"));
        }
        
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        if(pf.getPathModuleDatabases()==null || pf.getPathModuleDatabases().isEmpty()){
            errors.add(new ValidationError("otrosErrores", "Debe configurar el servicio de Indexaci\u00F3n en el Item (Otras configuraciones) de esta secci\u00F3n"));
        }
        
        if(pf.getNameCertificate()==null || pf.getNameCertificate().isEmpty()){
            errors.add(new ValidationError("otrosErrores", "Debe configurar los certificados en el Item (Configuraci\u00F3n b\u00E1sica del m\u00F3dulo) en la secci\u00F3n \"Configuraci\u00F3n\""));
        }
        
        if(pf.getNameCertificateP12()==null || pf.getNameCertificateP12().isEmpty()){
            errors.add(new ValidationError("otrosErrores", "Debe configurar los certificados en el Item (Configuraci\u00F3n b\u00E1sica del m\u00F3dulo) en la secci\u00F3n \"Configuraci\u00F3n\""));
        }
        
        if(pf.getPasswordCertificate()==null || pf.getPasswordCertificate().isEmpty()){
            errors.add(new ValidationError("otrosErrores", "Debe configurar la contrase\u00F1a de los certificados en el Item (Configuraci\u00F3n b\u00E1sica del m\u00F3dulo) en la secci\u00F3n \"Configuraci\u00F3n\""));
        }        
        
        String tareaResp = verificarTareaOthersFiles(fillTareasOthersFiles(),selConexion);
        if(!tareaResp.equals("")){
            errors.add(new ValidationError("otrosErrores", "Esta programaci\u00F3n ya existe para esta conexi\u00F3n!!! Si desea puede modificarla en el siguiente item (Editar programaci\u00F3n de tareas)..."));
        }

        return errors.isEmpty() ? null : errors;
    }
}
