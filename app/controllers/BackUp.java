package controllers;

import static controllers.BackUp.doBackUp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import views.html.doBackUp;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import models.PropertiesFile;
import org.apache.commons.io.FileUtils;
import play.*;
import play.data.Form;
import static play.data.Form.form;
import play.mvc.*;
import static play.mvc.Controller.request;
import views.formdata.doBackUpForm;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.zip.GZIPOutputStream;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import models.FillLists;
import models.backup.ProgrammingTaskBackUp;
import models.databases.ProgrammingTaskBD;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.json.XML;
import org.quartz.SchedulerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static play.mvc.Controller.request;
import static play.mvc.Http.HeaderNames.USER_AGENT;
import views.formdata.ScheduleTaskBackUpForm;
import views.formdata.ScheduleTaskBackUpForm;
import views.html.databases_prog_task;
import views.html.scheduleBackUp;

public class BackUp extends Controller {

    static Form<ScheduleTaskBackUpForm> scheduleTaskBackUp = form(ScheduleTaskBackUpForm.class);
    static Logger logger = Logger.getLogger(BackUp.class);

    @Security.Authenticated(Secured.class)
    public static Result doBackUp() {
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }

        logger.info(ipAddress + "<;>" + username + "<;>Haciendo copia de seguridad");
        String req = request().body().asText();
        String[] reqArr = req.split("-");
        String solr = reqArr[0];
        String[] cores = reqArr[1].split(";");
        String res = "";

        for (int i = 0; i < cores.length; i++) {

            if (auxBackUp(solr, cores[i])) {
                res += cores[i] + ";";

            }
        }
        logger.info("Colecciones copiadas correctamente:" + res);
        return ok(res);
    }

    @Security.Authenticated(Secured.class)
    public static Result restoreBackUp() {
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }

        logger.info(ipAddress + "<;>" + username + "<;>Restaurando copia de seguridad");

        String req = request().body().asText();
        String[] reqArr = req.split("\\+");
        String solrIn = reqArr[0];
        String solrOut = reqArr[1];
        String[] cores = reqArr[2].split(";");
        String res = "";

        for (int i = 0; i < cores.length; i++) {

            if (auxRestore(solrIn, solrOut, cores[i])) {
                res += cores[i] + ";";
            }
            logger.info("Colecciones restauradas correctamente:" + res);
        }
        return ok(res);
    }

    public static boolean auxBackUp(String solr, String core) {

        logger.info("Inicio copia de solr: " + solr + " a la coleccion " + core);
        String url_solr = getBaseURL(solr, core) + "/replication?command=backup";
        JSONObject response = doQuery(url_solr, "", "GET");
        try {
            JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
            if (jsonObj.getString("status").equals("OK")) {
                logger.info("Backup interno realizado con exito: " + solr + " a la coleccion " + core);
                String pathBackUp = "/opt/data/IFindIt/Solr/" + solr;
                boolean check1 = new File(pathBackUp + "/solr/", "solr.xml").exists();
                if (check1) {
                    String pDataSolr = pathBackUp + "/solr/" + core + "/data/";
                    String pDataSolrConf = pathBackUp + "/solr/" + core + "/conf/";
                    File fDataSolr = new File(pDataSolr);
                    File fDataSolrConf = new File(pDataSolrConf);
                    fDataSolr.setReadable(true, false);
                    fDataSolr.setExecutable(true, false);
                    fDataSolr.setWritable(true, false);
                    fDataSolrConf.setReadable(true, false);
                    fDataSolrConf.setExecutable(true, false);
                    fDataSolrConf.setWritable(true, false);
                    File[] directories = new File(pDataSolr).listFiles(File::isDirectory);
                    String newestBackup = "";
                    for (int i = 0; i < directories.length; i++) {
                        if (directories[i].getName().startsWith("snapshot.")) {
                            try {
                                String[] snapSplit = directories[i].getName().split("\\.");
                                String snapSplitDate = snapSplit[1];
                                Date dateSnapshot = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(snapSplitDate);
                                if (!newestBackup.equals("")) {
                                    Date dateNewestSnapshot = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(newestBackup);
                                    if (dateSnapshot.after(dateNewestSnapshot)) {
                                        newestBackup = snapSplitDate;
                                    }
                                } else {
                                    newestBackup = snapSplitDate;
                                }
                            } catch (ParseException ex) {

                            }
                        }
                    }

                    Date dateNewest = new SimpleDateFormat("yyyyMMddHHmmssSSS").parse(newestBackup);
                    DateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd-HH:mmss");

                    logger.info("Fecha ultimo backup: " + newestBackup);
                    String pathOutBackUp = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups/backup." + solr + "." + core + "." + dateFormat.format(dateNewest);

                    if (!newestBackup.equals("")) {
                        String pathLastBackUp = pathBackUp + "/solr/" + core + "/data/" + "snapshot." + newestBackup;
                        String pathConfBackUp = pathBackUp + "/solr/" + core + "/conf";
                        logger.info("Creacion de archivo en: " + pathOutBackUp);
                        createTarFile(pathOutBackUp, pathConfBackUp, pathLastBackUp);

                    }
                    return true;
                }
                logger.error("No existe una coleccion en la ubicacion " + pathBackUp + "/solr/" + "solr.xml");
                return false;
            }
            logger.error("Backup interno no fue realizado con exito: " + solr + " a la coleccion " + core);
            return false;
        } catch (ParseException ex) {
            return false;
        } catch (JSONException ex) {
            logger.error("Error: " + ex.getMessage());
            return false;
        }

    }

    public static boolean auxRestore(String solrOut, String solrIn, String coreIn) {
        String[] data = coreIn.split(",");
        String core = data[0];
        String date = data[1];

        logger.info("Inicio de restauracion de copia de solr: " + solrIn + " a la coleccion " + core + " en el solr: " + solrOut);
        try {
            File[] backups = new File("/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups").listFiles();
            File backup = null;
            for (int i = 0; i < backups.length; i++) {
                boolean check1 = backups[i].getName().startsWith("backup." + solrIn + "." + core + "." + date);
                if (check1) {
                    backup = backups[i];
                }
            }
            if (backup == null) {
                return false;
            }
            String pathSolrOut = "/opt/data/IFindIt/Solr/" + solrOut + "/solr/" + core;
            File fileSolrOut = new File(pathSolrOut);
            fileSolrOut.mkdir();
            Files.setPosixFilePermissions(Paths.get(pathSolrOut), PosixFilePermissions.fromString("rwxrwxrwx"));
            FileUtils.copyFileToDirectory(backup, fileSolrOut);
            uncompressTarGZ(backup, fileSolrOut);
            perms(pathSolrOut);
            String url_solr = getSolrURL(solrOut) + "admin/cores?action=CREATE&name=" + core + "&instanceDir=" + core;
            JSONObject response = doQuery(url_solr, "", "GET");
            JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
            if (jsonObj.getJSONObject("responseHeader").getInt("status") == 0) {
                url_solr = getSolrURL(solrOut) + core + "/replication?command=restore";
                response = doQuery(url_solr, "", "GET");
                jsonObj = new JSONObject(response.getString("responseSolr"));
                if (jsonObj.getJSONObject("responseHeader").getInt("status") == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

    }

    public static void perms(String path) {
        File[] perms = new File(path).listFiles();
        for (int i = 0; i < perms.length; i++) {
            try {
                Files.setPosixFilePermissions(Paths.get(perms[i].getAbsolutePath()), PosixFilePermissions.fromString("rwxrwxrwx"));
                if (perms[i].isDirectory()) {
                    perms(perms[i].getAbsolutePath());
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(BackUp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
        dest.mkdir();
        TarArchiveInputStream tarIn = null;

        tarIn = new TarArchiveInputStream(
                new GzipCompressorInputStream(
                        new BufferedInputStream(
                                new FileInputStream(
                                        tarFile
                                )
                        )
                )
        );

        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        // tarIn is a TarArchiveInputStream
        while (tarEntry != null) {// create a file with the same name as the tarEntry
            File destPath = new File(dest, tarEntry.getName());

            System.out.println("working: " + destPath.getCanonicalPath());
            if (tarEntry.isDirectory()) {
                destPath.mkdirs();
            } else {
                destPath.createNewFile();
                //byte [] btoRead = new byte[(int)tarEntry.getSize()];
                byte[] btoRead = new byte[1024];
                //FileInputStream fin 
                //  = new FileInputStream(destPath.getCanonicalPath());
                BufferedOutputStream bout
                        = new BufferedOutputStream(new FileOutputStream(destPath));
                int len = 0;

                while ((len = tarIn.read(btoRead)) != -1) {
                    bout.write(btoRead, 0, len);

                }

                bout.close();
                btoRead = null;

            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    }

    private static void createTarFile(String sourceDir, String conf, String data) {
        TarArchiveOutputStream tarOs = null;
        try {
            File source = new File(sourceDir);
            source.setWritable(true);
            // Using input name to create output name
            FileOutputStream fos = new FileOutputStream(source.getAbsolutePath().concat(".tar.gz"));
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            tarOs = new TarArchiveOutputStream(gos);
            addFilesToTarGZ(conf, "", tarOs);
            addFilesToTarGZ(data, "data/", tarOs);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Error en creacion de archivo : " + sourceDir + " Mensaje: " + e.getMessage());
        } finally {
            try {
                tarOs.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("Error general en creacion de archivo : " + sourceDir + " Mensaje: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void addFilesToTarGZ(String filePath, String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(filePath);
        file.setWritable(true);
        // Create entry name relative to parent file path 
        String entryName = parent + file.getName();
        // add tar ArchiveEntry
        tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Write file content to archive
            IOUtils.copy(bis, tarArchive);
            tarArchive.closeArchiveEntry();
            bis.close();
        } else if (file.isDirectory()) {
            // no need to copy any content since it is
            // a directory, just close the outputstream
            tarArchive.closeArchiveEntry();
            // for files in the directories
            for (File f : file.listFiles()) {
                // recursively call the method for all the subdirectories
                addFilesToTarGZ(f.getAbsolutePath(), entryName + File.separator, tarArchive);
            }
        }
    }

    public static void generateLineLog(String lineLog) {
        logger.info(lineLog);
    }

    public static Result loadSolrInsts() {
        try {
            String SolrInsts = "";
            File[] directories = new File("/opt/data/IFindIt/Solr/").listFiles(File::isDirectory);
            String[] names = new String[directories.length];
            for (int i = 0; i < directories.length; i++) {
                boolean check1 = new File(directories[i].getAbsolutePath() + "/solr/", "solr.xml").exists();
                if (check1) {
                    names[i] = directories[i].getName();
                }
            }
            for (int i = 0; i < names.length; i++) {
                SolrInsts += names[i] + ";";
            }
            logger.info("Instancias cargadas: " + SolrInsts);
            return ok(SolrInsts);
        } catch (Exception ex) {
            logger.error("Error cargando instancias de solr:" + ex);
        }
        return null;
    }

    public static Result loadSolrBackUpInsts() {

        try {
            String SolrInstsCores = "";
            logger.info("1");
            File[] backups = new File("/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups").listFiles();
            logger.info("2");
            String[] names = new String[backups.length];
            logger.info("3");
            for (int i = 0; i < backups.length; i++) {
                boolean check1 = backups[i].getName().startsWith("backup.");
                logger.info("Archivo : " + backups[i].getName());
                if (check1) {
                    names[i] = backups[i].getName().split("\\.")[1];
                    logger.info("Instancias cargada: " + names[i]);
                }
            }
            Set<String> set = new HashSet<String>();

            for (int i = 0; i < names.length; i++) {
                set.add(names[i]);

            }

            for (String e : set) {
                SolrInstsCores += e + ";";
            }
            logger.info("Instancias cargadas: " + SolrInstsCores);
            return ok(SolrInstsCores);
        } catch (Exception ex) {
            logger.error("Error cargando instancias con copia de solr:" + ex);
        }
        return null;

    }

    public static String getBaseURL(String solr, String core) {
        String ipSolr, portSolr, coreSolr;
        ipSolr = retrievePublicIP();

        portSolr = retrieveSolrPort(solr);
        coreSolr = core;
        String solrInfo = "http://" + ipSolr + ":" + portSolr + "/solr/" + coreSolr;
        return solrInfo;
    }

    public static String getSolrURL(String solr) {
        String ipSolr, portSolr;
        ipSolr = retrievePublicIP();

        portSolr = retrieveSolrPort(solr);

        String solrInfo = "http://" + ipSolr + ":" + portSolr + "/solr/";
        return solrInfo;
    }

    public static Result loadSolrBackUps() {
        try {
            String req = request().body().asText();
            String[] reqArr = req.split(";");
            String solr = reqArr[0];
            String core = reqArr[1];
            String SolrInstsCores = "";

            File[] backups = new File("/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups").listFiles();

            String[] names = new String[backups.length];

            for (int i = 0; i < backups.length; i++) {
                boolean check1 = backups[i].getName().startsWith("backup." + solr + "." + core);
                logger.info("Archivo : " + backups[i].getName());
                if (check1) {
                    names[i] = backups[i].getName().split("\\.")[3];
                    logger.info("Instancias cargada: " + names[i]);
                }
            }
            Set<String> set = new HashSet<String>();

            for (int i = 0; i < names.length; i++) {
                if (names[i] != null && !names[i].equals("null") && !names[i].equals("")) {
                    set.add(names[i]);
                }

            }

            for (String e : set) {
                SolrInstsCores += e + ";";
            }
            logger.info("Instancias cargadas: " + SolrInstsCores);
            return ok(SolrInstsCores);
        } catch (Exception ex) {
            logger.error("Error cargando instancias con copia de solr:" + ex);
        }
        return null;
    }

    public static Result loadSolrLastBackUp() {
        //http://192.168.1.36:8783/solr/admin/cores?action=STATUS&indexInfo=false
        String req = request().body().asText();
        String[] reqArr = req.split(";");
        String solr = reqArr[0];
        String core = reqArr[1];
        String url_solr = getBaseURL(solr, core) + "/replication?command=details";
        JSONObject response = doQuery(url_solr, "", "GET");
        try {
            JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
            JSONObject details = jsonObj.getJSONObject("details");

            JSONArray lastBackupJSON = details.getJSONArray("backup");
            String lastBackup = lastBackupJSON.getString(7);
            return ok(lastBackup);
        } catch (JSONException ex) {
            logger.error(ex);
            return ok("-");
        }

    }

    public static Result loadSolrCores() {
        //http://192.168.1.36:8783/solr/admin/cores?action=STATUS&indexInfo=false
        String solr = request().body().asText();

        String ipSolr = retrievePublicIP();
        String portSolr = retrieveSolrPort(solr);
        String url_solr = "http://" + ipSolr + ":" + portSolr + "/solr/admin/cores?action=STATUS&indexInfo=false";
        JSONObject response = doQuery(url_solr, "", "GET");
        if (response.getString("responseCode").equals("200")) {
            try {
                JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
                JSONObject status = jsonObj.getJSONObject("status");
                String cores = String.join(";", status.keySet());
                return ok(cores);
            } catch (JSONException ex) {
                logger.error(ex);
                return status(580, "Error");
            }
        } else {
            return status(580, response.getString("responseSolr"));
        }
    }

    public static Result loadSolrBackUpCores() {
        String core = request().body().asText();

        try {
            String SolrInstsCores = "";
            File[] backups = new File("/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups").listFiles();
            String[] names = new String[backups.length];
            for (int i = 0; i < backups.length; i++) {
                boolean check1 = backups[i].getName().startsWith("backup." + core + ".");
                if (check1) {
                    names[i] = backups[i].getName().split("\\.")[2];
                }
            }
            Set<String> set = new HashSet<String>();

            for (int i = 0; i < names.length; i++) {
                set.add(names[i]);
            }
            for (String e : set) {

                SolrInstsCores += e + ";";
            }
            return ok(SolrInstsCores);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }

    public static String retrieveSolrPort(String solr) {
        String solrPort = "";
        BufferedReader br;
        FileReader fr;
        String setupPathconf = "/opt/data/IFindIt/Solr/dockers.config";
        try {
            fr = new FileReader(setupPathconf);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.contains("<;>") && sCurrentLine.split("<;>", -1).length >= 4) {
                    if (sCurrentLine.split("<;>", -1)[2].equals(solr)) {
                        Pattern pattern = Pattern.compile(":\\d+->8983");
                        Matcher matcher = pattern.matcher(sCurrentLine.split("<;>", -1)[3]);
                        if (matcher.find()) {
                            solrPort = matcher.group(0).replace(":", "").replace("->8983", "");
                        }
                    }
                }
            }
            return solrPort;
        } catch (IOException e) {
            logger.error("Error: " + e);
        }
        return solrPort;
    }

    @Security.Authenticated(Secured.class)
    public static Result saveProgTaskBackUp() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Guardando Programaci\u00F3n de tarea de copia de seguridad";
        generateLineLog(lineLog);

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();

        Form<ScheduleTaskBackUpForm> filledFormBackUp = scheduleTaskBackUp.bindFromRequest();

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        BackUp backup = new BackUp();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        if (filledFormBackUp.hasErrors()) {
            logger.error("Errores encontrados.");
            return badRequest(scheduleBackUp.render("Programaci\u00F3n Tarea Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledFormBackUp, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes));
        } else {
            try {
                String folderRoot = pf.getPathFileModule() + "/backups/";
                String pathTasks = folderRoot + "task.xml";
                ScheduleTaskBackUpForm created = filledFormBackUp.get();
                String[] cores = created.coresHiddenBackUp.split(";");
                for (int i = 0; i < cores.length; i++) {
                    createRootProgTask(pathTasks, created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, created.hourTask, created.minuteTask, created.instHiddenBackUp, cores[i]);
                    new ProgrammingTaskBackUp(created.perMinutesTask, created.perHoursTask, created.dayTask, created.dayWeekTask, created.dayMonthTask, Integer.parseInt(created.hourTask), Integer.parseInt(created.minuteTask), created.instHiddenBackUp, cores[i], folderRoot).iniciarTarea();
                }

                return redirect(routes.Application.profile());
            } catch (SchedulerException ex) {
                logger.error(ex);
            }
        }
        return null;
    }

    public static LinkedHashMap<String, String> fillInsts() {
        try {
            LinkedHashMap<String, String> nomInsts = new LinkedHashMap<>();

            File[] directories = new File("/opt/data/IFindIt/Solr/").listFiles(File::isDirectory);
            String[] names = new String[directories.length];
            for (int i = 0; i < directories.length; i++) {
                boolean check1 = new File(directories[i].getAbsolutePath() + "/solr/", "solr.xml").exists();
                if (check1) {
                    names[i] = directories[i].getName();
                }
            }
            for (int i = 0; i < names.length; i++) {
                nomInsts.put(names[i], names[i]);
            }

            return nomInsts;
        } catch (Exception ex) {
            logger.error(ex);
        }
        return null;
    }

    public LinkedHashMap<String, String> fillCores(String solr) {

        LinkedHashMap<String, String> nomCores = new LinkedHashMap<>();

        String ipSolr = retrievePublicIP();
        String portSolr = retrieveSolrPort(solr);
        String url_solr = "http://" + ipSolr + ":" + portSolr + "/solr/admin/cores?action=STATUS&indexInfo=false";
        JSONObject response = doQuery(url_solr, "", "GET");
        if (response.getString("responseCode").equals("200")) {
            try {
                JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
                JSONObject status = jsonObj.getJSONObject("status");
                String cores = String.join(";", status.keySet());
                String[] names = cores.split(";");
                for (int i = 0; i < names.length; i++) {
                    nomCores.put(names[i], names[i]);
                }
                return nomCores;
            } catch (JSONException ex) {
                logger.error(ex);
                return null;
            }
        } else {
            return null;
        }

    }

    public static void createRootProgTask(String pathTasks, String perMinutesTask, String perHoursTask, String dayTask, String dayWeekTask, String dayMonthTask, String hourTask, String minuteTask, String selInst, String selCore) {
        try {
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");
            NodeList nodeLstInt;
            String latestID = "";
            //Obtener ultimo Nodo
            if (nodeLst.getLength() - 1 >= 0) {
                nodeLstInt = nodeLst.item(nodeLst.getLength() - 1).getChildNodes();
                //Obtener ultimo ID
                latestID = nodeLstInt.item(0).getTextContent();
            } else if (nodeLst.getLength() - 1 < 0) {
                latestID = "0";
            }

            // Nos devuelve el nodo ra\u00EDz del documento XML.
            Node newRoot = doc.getDocumentElement();
            Element newNodo = doc.createElement("tarea");

            int newID = Integer.parseInt(latestID) + 1;
            Element nodeID = doc.createElement("id");
            nodeID.setTextContent(String.valueOf(newID));

            Element nodePerMinutes = doc.createElement("porMinutos");
            nodePerMinutes.setTextContent(perMinutesTask);

            Element nodePerHours = doc.createElement("porHoras");
            nodePerHours.setTextContent(perHoursTask);

            Element nodeDay = doc.createElement("dia");
            nodeDay.setTextContent(dayTask);

            Element nodeDayWeek = doc.createElement("diaSemana");
            nodeDayWeek.setTextContent(dayWeekTask);

            Element nodeDayMonth = doc.createElement("diaMes");
            nodeDayMonth.setTextContent(dayMonthTask);

            Element nodeHour = doc.createElement("hora");
            nodeHour.setTextContent(hourTask);

            Element nodeMinute = doc.createElement("minuto");
            nodeMinute.setTextContent(minuteTask);

            Element nodeInst = doc.createElement("instancia");
            nodeInst.setTextContent(selInst);

            Element nodeCore = doc.createElement("core");
            nodeCore.setTextContent(selCore);

            newNodo.appendChild(nodeID);
            newNodo.appendChild(nodePerMinutes);
            newNodo.appendChild(nodePerHours);
            newNodo.appendChild(nodeDay);
            newNodo.appendChild(nodeDayWeek);
            newNodo.appendChild(nodeDayMonth);
            newNodo.appendChild(nodeHour);
            newNodo.appendChild(nodeMinute);
            newNodo.appendChild(nodeInst);
            newNodo.appendChild(nodeCore);
            newRoot.appendChild(newNodo);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);

        } catch (IOException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            logger.error(e);
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result loadProgTaskBackUp() {
        try {
            PropertiesFile pf = new PropertiesFile();
            pf.loadProperties();
            String pathXML = pf.getPathFileModule() + "/backups/task.xml";
            String xml = FileUtils.readFileToString(new File(pathXML));

            JSONObject jsonObject = XML.toJSONObject(xml);

            String jsonString = jsonObject.toString();

            return ok(jsonString);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Security.Authenticated(Secured.class)
    public static Result editTaskDatabases() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando Programaci\u00E3n de tarea de Bases de datos";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        String[] splitRequest = requestTask.split("<;>", -1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule() + "/backups/task.xml";
        try {
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                NodeList nodeLstInt = nodeLst.item(i).getChildNodes();
                if (nodeLstInt.item(0).getTextContent().equals(splitRequest[0])) {
                    for (int j = 0; j < nodeLstInt.getLength(); j++) {

                        switch (j) {
                            case 1:
                                nodeLstInt.item(j).setTextContent(splitRequest[1]);
                                break;
                            case 2:
                                nodeLstInt.item(j).setTextContent(splitRequest[2]);
                                break;
                            case 3:
                                nodeLstInt.item(j).setTextContent(splitRequest[3]);
                                break;
                            case 4:
                                nodeLstInt.item(j).setTextContent(splitRequest[4]);
                                break;
                            case 5:
                                nodeLstInt.item(j).setTextContent(splitRequest[5]);
                                break;
                            case 6:
                                nodeLstInt.item(j).setTextContent(splitRequest[6]);
                                break;
                            case 7:
                                nodeLstInt.item(j).setTextContent(splitRequest[7]);
                                break;
                            case 8:
                                nodeLstInt.item(j).setTextContent(splitRequest[8]);
                                break;
                        }
                    }
                    String folderRoot = pf.getPathFileModule() + "/backups/";
                    new ProgrammingTaskBD(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], Integer.parseInt(splitRequest[6]), Integer.parseInt(splitRequest[7]), splitRequest[8], folderRoot).iniciarTarea();
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException pce) {
        } catch (SAXException | IOException | TransformerException | SchedulerException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Security.Authenticated(Secured.class)
    public static Result editProgTaskBackUp() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Modificando Programaci\u00E3n de tarea de copia de seguridad";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        String[] splitRequest = requestTask.split("<;>", -1);
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule() + "/backups/task.xml";
        try {
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                NodeList nodeLstInt = nodeLst.item(i).getChildNodes();
                if (nodeLstInt.item(0).getTextContent().equals(splitRequest[0])) {
                    for (int j = 0; j < nodeLstInt.getLength(); j++) {

                        switch (j) {
                            case 1:
                                nodeLstInt.item(j).setTextContent(splitRequest[1]);
                                break;
                            case 2:
                                nodeLstInt.item(j).setTextContent(splitRequest[2]);
                                break;
                            case 3:
                                nodeLstInt.item(j).setTextContent(splitRequest[3]);
                                break;
                            case 4:
                                nodeLstInt.item(j).setTextContent(splitRequest[4]);
                                break;
                            case 5:
                                nodeLstInt.item(j).setTextContent(splitRequest[5]);
                                break;
                            case 6:
                                nodeLstInt.item(j).setTextContent(splitRequest[6]);
                                break;
                            case 7:
                                nodeLstInt.item(j).setTextContent(splitRequest[7]);
                                break;
                            case 8:
                                nodeLstInt.item(j).setTextContent(splitRequest[8]);
                                break;
                            case 9:
                                nodeLstInt.item(j).setTextContent(splitRequest[9]);
                                break;
                        }
                    }
                    String folderRoot = pf.getPathFileModule() + "/backups/";
                    new ProgrammingTaskBackUp(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], Integer.parseInt(splitRequest[6]), Integer.parseInt(splitRequest[7]), splitRequest[8], splitRequest[9], folderRoot).iniciarTarea();
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);
            return redirect(routes.Application.profile());
        } catch (ParserConfigurationException pce) {
        } catch (SAXException | IOException | TransformerException | SchedulerException ex) {
            logger.error(ex);
        }
        return null;
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteProgTaskBackUp() {
        /*Lineas para Guardar en LOG */
        String username = request().username();
        String ipAddress = request().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request().remoteAddress();
        }
        String lineLog = ipAddress + "<;>" + username + "<;>Eliminando Tareas de copia de seguridad";
        generateLineLog(lineLog);

        String requestTask = request().body().asText();
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String pathTasks = pf.getPathFileModule() + "/backups/task.xml";
        try {
            File file = new File(pathTasks);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("tarea");

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Element e = (Element) nodeLst.item(i);
                String valueID = nodeLst.item(i).getFirstChild().getTextContent();
                if (valueID.equals(requestTask)) {
                    e.getParentNode().removeChild(e);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(pathTasks));
            transformer.transform(source, result);

            return redirect(routes.Application.showScheduleBackUpEdit());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            logger.error(ex);
        }
        return null;
    }

    public static String retrievePublicIP() {
        String publicIP = "";
        BufferedReader br;
        FileReader fr;
        String setupPathconf = "/opt/data/IFindIt/admin_service/temp/docker0.properties";
        try {
            fr = new FileReader(setupPathconf);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.contains("*****")) {
                    if (sCurrentLine.contains("public_ip")) {
                        publicIP = sCurrentLine.split("=")[1].trim();
                    }
                }
            }
            return publicIP;
        } catch (IOException e) {
            logger.error("Error: " + e);
        }
        return publicIP;
    }

    public boolean backUp(String solr, String core) {

        logger.info("Inicio copia de solr: " + solr + " a la coleccion " + core);
        String url_solr = getBaseURL(solr, core) + "/replication?command=backup";
        JSONObject response = doQuery(url_solr, "", "GET");
        try {
            JSONObject jsonObj = new JSONObject(response.getString("responseSolr"));
            if (jsonObj.getString("status").equals("OK")) {
                logger.info("Backup interno realizado con exito: " + solr + " a la coleccion " + core);
                String pathBackUp = "/opt/data/IFindIt/Solr/" + solr;
                boolean check1 = new File(pathBackUp + "/solr/", "solr.xml").exists();
                if (check1) {
                    File[] directories = new File(pathBackUp + "/solr/" + core + "/data/").listFiles(File::isDirectory);
                    String newestBackup = "";
                    for (int i = 0; i < directories.length; i++) {
                        if (directories[i].getName().startsWith("snapshot.")) {
                            try {
                                String[] snapSplit = directories[i].getName().split("\\.");
                                String snapSplitDate = snapSplit[1];
                                Date dateSnapshot = new SimpleDateFormat("yyyyMMddHHmmss").parse(snapSplitDate);
                                if (!newestBackup.equals("")) {
                                    Date dateNewestSnapshot = new SimpleDateFormat("yyyyMMddHHmmss").parse(newestBackup);
                                    if (dateSnapshot.after(dateNewestSnapshot)) {
                                        newestBackup = snapSplitDate;
                                    }
                                } else {
                                    newestBackup = snapSplitDate;
                                }
                            } catch (ParseException ex) {
                                logger.error("Error: " + ex.getMessage());

                            }
                        }
                    }

                    logger.info("Fecha ultimo backup: " + newestBackup);
                    String pathOutBackUp = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/backups/backup." + solr + "." + core + "." + newestBackup;

                    if (!newestBackup.equals("")) {
                        String pathLastBackUp = pathBackUp + "/solr/" + core + "/data/" + "snapshot." + newestBackup;
                        String pathConfBackUp = pathBackUp + "/solr/" + core + "/conf";
                        logger.info("Creacion de archivo en: " + pathOutBackUp);
                        createTarFile(pathOutBackUp, pathConfBackUp, pathLastBackUp);

                    }
                    return true;
                }
                logger.error("No existe una coleccion en la ubicacion " + pathBackUp + "/solr/" + "solr.xml");
                return false;
            }
            logger.error("Backup interno no fue realizado con exito: " + solr + " a la coleccion " + core);
            return false;
        } catch (JSONException ex) {
            logger.error("Error: " + ex.getMessage());
            return false;
        }

    }

    public static JSONObject doQuery(String reqUrl, String data, String method) {
        System.out.println("reqUrl " + reqUrl);
        logger.info("reqUrl " + reqUrl);
        JSONObject respuesta = new JSONObject();
        int responseCode = 0;
        StringBuilder response = new StringBuilder();
        try {
            
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setDoOutput(true);

            if (!data.equals("")) {
                logger.info("Data--> " + data);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] postData = data.getBytes(StandardCharsets.UTF_8);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }
            }

            responseCode = con.getResponseCode();
            logger.info("Response Code doQuery: " + responseCode);

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
            } else {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
            }
            respuesta.put("responseCode", String.valueOf(responseCode));
            respuesta.put("responseSolr", response.toString());
        } catch (IOException ex) {
            logger.error(ex);
        }
        return respuesta;
    }

/////////////////////////////////
}
