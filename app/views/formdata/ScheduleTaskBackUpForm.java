/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.formdata;

import static controllers.Databases.fillTasks;
import static controllers.Databases.checkTask;
import java.util.ArrayList;
import java.util.List;
import models.PropertiesFile;
import play.data.validation.ValidationError;

/**
 *
 * @author atovar
 */
public class ScheduleTaskBackUpForm {

    public String idTask;
    public String perMinutesTask;
    public String perHoursTask;
    public String dayTask;
    public String dayWeekTask;
    public String dayMonthTask;
    public String hourTask;
    public String minuteTask;
    public String connectionProg;
    
    public String  hiddenCheckedMinutes, hiddenCheckedHours, hiddenCheckedDays, hiddenCheckedDayWeek, hiddenCheckedDayMonth;
    
    public String instHiddenBackUp, coresHiddenBackUp;
    
    /**
     * Validates Form<ProgTareaBasedatosForm>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
    
        List<ValidationError> errors = new ArrayList<>();
         
        if(hiddenCheckedMinutes != null && hiddenCheckedMinutes.equals("true")){
            if(perMinutesTask == null || perMinutesTask.trim().isEmpty()){
                errors.add(new ValidationError("perMinutesTask", "Debe ingresar la frecuencia en minutos en que se ejecutar\u00E1 la tarea"));
            }
            perHoursTask="";
            dayTask="";
            dayWeekTask="";
            dayMonthTask="";
        }else if(hiddenCheckedHours != null && hiddenCheckedHours.equals("true")){
            if(perHoursTask == null || perHoursTask.trim().isEmpty()){
                errors.add(new ValidationError("perHoursTask", "Debe ingresar la frecuencia en horas en que se ejecutar\u00E1 la tarea"));
            }
            perMinutesTask="";
            dayTask="";
            dayWeekTask="";
            dayMonthTask="";
        }else if(hiddenCheckedDays != null && hiddenCheckedDays.equals("true")){
            if(dayTask == null || dayTask.trim().isEmpty()){
                errors.add(new ValidationError("dayTask", "Debe ingresar el n\uu00F3mero de d\u00EDas de frecuencia en que se ejecutar\u00E1 la tarea"));
            }
            perMinutesTask="";
            perHoursTask="";
            dayWeekTask="";
            dayMonthTask="";
        }else if(hiddenCheckedDayWeek != null && hiddenCheckedDayWeek.equals("true")){
            if(dayWeekTask == null || dayWeekTask.trim().isEmpty()){
                errors.add(new ValidationError("dayWeekTask", "Debe seleccionar el d\u00EDa de la semana en que se ejecutar\u00E1 la tarea"));
            }
            perMinutesTask="";
            perHoursTask="";
            dayTask="";
            dayMonthTask="";
        }else if(hiddenCheckedDayMonth != null && hiddenCheckedDayMonth.equals("true")){
            if(dayMonthTask == null || dayMonthTask.trim().isEmpty()){
                errors.add(new ValidationError("dayMonthTask", "Debe seleccionar el d\u00EDa del mes en que se ejecutar\u00E1 la tarea"));
            }
            perMinutesTask="";
            perHoursTask="";
            dayWeekTask="";
            dayTask="";
        }
        
        if((hiddenCheckedMinutes == null || hiddenCheckedMinutes.equals("false")) && (hiddenCheckedHours == null || hiddenCheckedHours.equals("false")) && (hiddenCheckedDays == null || hiddenCheckedDays.equals("false")) && (hiddenCheckedDayWeek == null || hiddenCheckedDayWeek.equals("false")) && (hiddenCheckedDayMonth == null || hiddenCheckedDayMonth.equals("false"))){
            errors.add(new ValidationError("otherErrors", "Debe seleccionar una opci\u00F3n para programar la tarea"));
        }
        
        if(hiddenCheckedDays != null && hiddenCheckedDays.equals("true") || (hiddenCheckedDayWeek != null && hiddenCheckedDayWeek.equals("true")) || (hiddenCheckedDayMonth != null && hiddenCheckedDayMonth.equals("true"))){
        if(hourTask == null || hourTask.isEmpty())
        {
            errors.add(new ValidationError("hourTask", "Debe ingresar la hora en que se ejecutar\u00E1 la tarea"));
        }
        
        if(minuteTask == null || minuteTask.isEmpty())
        {
            errors.add(new ValidationError("minuteTask", "Debe ingresar el minuto en que se ejecutar\u00E1 la tarea"));
        }
        }
        
        
        
       
        
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        
        if(pf.getNameCertificate()==null || pf.getNameCertificate().isEmpty()){
            errors.add(new ValidationError("otherErrors", "Debe configurar el certificado en el menu \"Configuraci\u00F3n --> Configuraci\u00F3n b\u00E1sica del m\u00F3dulo\""));
        }
        return errors.isEmpty() ? null : errors;
    }
}
