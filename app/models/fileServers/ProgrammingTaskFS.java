/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.fileServers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author atovar
 */

public class ProgrammingTaskFS {
    /**
     * Creates a new instance of ProgrammingTaskFS
     */
    //El horario de la tarea
    private Scheduler horario;
    String nameTrigger = "triggerFS";
    String nameTarea = "taskFS";
     
    String selConexion;
    int hora, min,formatoDia;
    String porMinutos, porHoras, dia, diaSemana, diaMes;
    String folderRaiz;
    
    static Logger logger = Logger.getLogger(InvokeTaskFS.class);

    public ProgrammingTaskFS(String porMinutos, String porHoras, String dia, String diaSemana, String diaMes, int hora, int min, String selConexion, String folderRaiz) {
        this.porMinutos = porMinutos;
        this.porHoras = porHoras;
        this.dia = dia;
        this.diaSemana = diaSemana;
        this.diaMes = diaMes;
        this.hora = hora;
        this.min = min;
        this.selConexion = selConexion;
        this.folderRaiz = folderRaiz;
    }

    //Metodo que crea el horario
    private void crearProgramacion() {

        try {
            //Creando la programacion
            SchedulerFactory factoria = new StdSchedulerFactory();
            //Obteniendo el horario
            horario = factoria.getScheduler();
            //Damos inicio al horario
            horario.start();
        } catch (SchedulerException ex) {
            logger.error("Se genero error programando la tarea para Servidor de archivos:" + ex.getMessage());
        }

    }
    
    //Hora de inicio de la tarea y cada cuanto tiempo se ejecuta

    public void iniciarTarea() throws SchedulerException {
        if (this.horario == null) {
            this.crearProgramacion();
        } 
        
        nameTarea += "-"+selConexion;
        nameTrigger += "-"+selConexion;
        
        //JobDetail job = new JobDetail(nameTarea, null, TareaInvocar.class);
        JobDetail job = new JobDetail(nameTarea, InvokeTaskFS.class);
        // Se envian datos para hacer split con # en la tarea
        job.setDescription(selConexion + "#" + folderRaiz);
        
        Trigger trigger = null;
        
        if(porMinutos!=null && !porMinutos.equals("")){
            logger.info("Programacion del Proceso para Servidor de archivos --> Cada "+porMinutos+" minuto(s).");
            trigger = TriggerUtils.makeMinutelyTrigger(Integer.parseInt(porMinutos));
            trigger.setName(nameTrigger); 
        }else if(porHoras!=null && !porHoras.equals("")){
            logger.info("Programacion del Proceso para Servidor de archivos --> Cada "+porHoras+" hora(s).");
            trigger = TriggerUtils.makeHourlyTrigger(Integer.parseInt(porHoras));
            trigger.setName(nameTrigger); 
        }else{
        
            if(dia==null){
                dia="";
            }

            if(diaSemana==null){
                diaSemana="";
            }

            if(diaMes==null){
                diaMes="";
            }

        logger.info("Programacion del Proceso para Servidor de archivos --> Cada "+dia+diaSemana+diaMes+" a las "+hora+":"+min);
        
        //En que momento va a iniciar la tarea
        Calendar startTime = new GregorianCalendar();
        startTime.set(Calendar.HOUR_OF_DAY ,hora);
	startTime.set(Calendar.MINUTE , min);
	startTime.set(Calendar.SECOND , 0);
        
        if(!dia.trim().equals("")){
            int horas = Integer.parseInt(dia.trim())*24;
            trigger = TriggerUtils.makeHourlyTrigger(horas);
            trigger.setStartTime(startTime.getTime());
            trigger.setName(nameTrigger);    
        }else if(!diaSemana.trim().equals("")){
            trigger = TriggerUtils.makeWeeklyTrigger(nameTrigger, dayOfWeek(diaSemana.trim()), hora, min);
        }else if(!diaMes.trim().equals("")){
            trigger = TriggerUtils.makeMonthlyTrigger(nameTrigger, Integer.parseInt(diaMes.trim()), hora, min);
        }
        }
  
        try {
            if (this.horario.getJobDetail(nameTarea, null) != null) {
                if (this.horario.getJobDetail(nameTarea, null).equals(job)) {
                    this.horario.deleteJob(nameTarea, null);
                    this.horario.scheduleJob(job, trigger);
                }
            } else {
                this.horario.scheduleJob(job, trigger);
            }

        } catch (SchedulerException ex) {
            logger.error("Se genero error con el horario del proceso para el Servidor de archivos: " + ex.getMessage());
        }
    }
    
    public int dayOfWeek(String day){
        switch(day){
            case "Lunes":
                return TriggerUtils.MONDAY;
            case "Martes":
                return TriggerUtils.TUESDAY;
            case "Miercoles":
                return TriggerUtils.WEDNESDAY;
            case "Jueves":
                return TriggerUtils.THURSDAY;
            case "Viernes":
                return TriggerUtils.FRIDAY;
            case "Sabado":
                return TriggerUtils.SATURDAY;
            case "Domingo":
                return TriggerUtils.SUNDAY;
            case "":
                return 0;
        }
        return 0;
    }
}