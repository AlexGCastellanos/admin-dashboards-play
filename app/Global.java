import java.io.File;
import java.io.IOException;
import models.PropertiesFile;
import models.databases.ProgrammingTaskBD;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.quartz.SchedulerException;
import play.Application;
import play.GlobalSettings;

/**
 * Provide initialization code for the digits application.
 *
 * @author Philip Johnson
 */
public class Global extends GlobalSettings {

    /**
     * Initialize the system with some sample contacts.
     *
     * @param app The application.
     */
        
    @Override
    public void onStart(Application app) {                
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
                
        File f = new File(pf.getPathFileModule() + "/logs/");
        if(!f.exists()){
            f.mkdirs();
        }
        File f2 = new File(pf.getPathFileModule() + "/logs/log4j.properties");
        if(f2.exists()){
            try {
                PropertyConfigurator.configure(pf.getPathFileModule() + "/logs/log4j.properties");
                               
                String pathXMLTasksBD = pf.getPathFileModule()+"/databases/task.xml";
                String folderRoot = pf.getPathFileModule()+"/databases/";
                String xmlTasksBD = FileUtils.readFileToString(new File(pathXMLTasksBD));
                
                JSONObject jsonObjectTasksBD = XML.toJSONObject(xmlTasksBD);
                try{
                    JSONArray jsonArrayTasksBD = jsonObjectTasksBD.getJSONObject("tareas").getJSONArray("tarea");
                    System.out.println("Nro. Tareas BD --> "+jsonArrayTasksBD.length());
                    for(int i=1; i< jsonArrayTasksBD.length(); i++){
                        JSONObject jsonObject1 = jsonArrayTasksBD.getJSONObject(i);
                        String porMinutos = jsonObject1.optString("porMinutos");
                        String porHoras = jsonObject1.optString("porHoras");
                        String dia = jsonObject1.optString("dia");
                        String diaSemana = jsonObject1.optString("diaSemana");
                        String diaMes = jsonObject1.optString("diaMes");
                        String hora = jsonObject1.optString("hora");
                        String minuto = jsonObject1.optString("minuto");
                        String connection = jsonObject1.optString("conexion");
                        new ProgrammingTaskBD(porMinutos, porHoras, dia, diaSemana, diaMes, Integer.parseInt(hora), Integer.parseInt(minuto),connection,folderRoot).iniciarTarea();
                    }
                }catch(JSONException ex){
                    System.out.println("Ninguna tarea programada para Bases de datos "+ex);
                } catch (SchedulerException ex) {
                    System.out.println("Error: "+ex);
                }
                
            } catch (IOException ex) {
                System.out.println("Error en clase Global"+ex);
            }
        }
    }
}
