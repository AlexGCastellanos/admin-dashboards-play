package controllers;

import static controllers.QueryRouteConfig.routeQueryForm;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import models.ConfigurationsOthersFiles;
import models.DatabasesModel;
import models.FillLists;
import models.OperationsModel;
import models.ProgTarea;
import play.mvc.*;
import views.html.*;
import models.PropertiesFile;
import org.apache.log4j.Logger;
import play.data.Form;
import static play.mvc.Controller.ctx;
import static play.mvc.Results.ok;
import views.formdata.CleanDataServiceForm;
import views.formdata.DatabasesForm;
import views.formdata.ConfigurationForm;
import views.formdata.FileServerForm;
import views.formdata.GroupsForm;
import views.formdata.LoginFormData;
import views.formdata.PermissionsForm;
import views.formdata.ProfilesForm;
import views.formdata.ProgTareaOthersFilesForm;
import views.formdata.ProgTaskDatabasesForm;
import views.formdata.ProgTaskFileserversForm;
import views.formdata.QueryAndIndexingForm;
import views.formdata.RouteQueryForm;
import views.formdata.UsersForm;

import views.formdata.ScheduleTaskBackUpForm;
import views.formdata.uploadFileForm;
import views.formdata.uploadFileIndexerForm;
import views.formdata.uploadSolrToSolrForm;

public class Application extends Controller {

    static Logger logger = Logger.getLogger(Application.class);

    public static Result index() {
        return redirect(routes.Application.login());
    }

    public static Result login() {
        Form<LoginFormData> formData = Form.form(LoginFormData.class);
        return ok(login.render("Login", false, Secured.getUserInfo(ctx()), formData));
    }

    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

    public static Result postLogin() {
        // Get the submitted form data from the request object, and run validation.
        Form<LoginFormData> formData = Form.form(LoginFormData.class).bindFromRequest();

        if (formData.hasErrors()) {
            flash("error", "Credenciales para login invalidas.");
            return badRequest(login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData));
        } else {
            // email/password OK, so now we set the session variable and only go to authenticated pages.
            session().clear();
            session("email", formData.get().email);
            session("profile", formData.get().profile);

            return redirect(routes.Application.profile());
        }
    }

    public static Result loadConfigModule() {
        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        String concat = (pf.getPathFileModule() + "<;>" + pf.getPasswordCertificate());
        return ok(concat);
    }

    @Security.Authenticated(Secured.class)
    public static Result profile() {
        return ok(profile.render("Profile", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddPermission() {
        Form<PermissionsForm> filledForm = Form.form(PermissionsForm.class);
        return ok(admin_add_permissions.render("Agregar permiso", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditPermission() {
        Form<PermissionsForm> filledForm = Form.form(PermissionsForm.class);
        return ok(admin_edit_permissions.render("Editar permiso", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddProfiles() {
        Form<ProfilesForm> filledForm = Form.form(ProfilesForm.class);
        return ok(admin_add_profiles.render("Agregar perfiles", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditProfiles() {
        Form<ProfilesForm> filledForm = Form.form(ProfilesForm.class);
        return ok(admin_edit_profiles.render("Editar perfiles", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddGroups() {
        Form<GroupsForm> filledForm = Form.form(GroupsForm.class);
        return ok(admin_add_groups.render("Agregar grupos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditGroups() {
        Form<GroupsForm> filledForm = Form.form(GroupsForm.class);
        return ok(admin_edit_groups.render("Editar grupos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddUsers() {
        Form<UsersForm> filledForm = Form.form(UsersForm.class);
        return ok(admin_add_users.render("Agregar usuarios", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditUsers() {
        Form<UsersForm> filledForm = Form.form(UsersForm.class);
        return ok(admin_edit_users.render("Editar usuarios", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showConfigurationModule() {
        Form<ConfigurationForm> filledForm = Form.form(ConfigurationForm.class);
        return ok(configuration_module.render("Configuracion Modulo", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddDatabaseConfig() {

        Form<DatabasesForm> filledForm = Form.form(DatabasesForm.class);

        HashMap<String, String> arrDatabases;
        HashMap<String, String> arrDatabasesSort;
        DatabasesModel databases = new DatabasesModel();
        arrDatabases = databases.fillDatabases();
        arrDatabasesSort = databases.sortHashMapByValues(arrDatabases);

        return ok(databases_config.render("Configuracion Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrDatabasesSort));

    }

    @Security.Authenticated(Secured.class)
    public static Result showEditDatabaseConfig() {
        Form<DatabasesForm> filledForm = Form.form(DatabasesForm.class);

        HashMap<String, String> arrDatabases;
        HashMap<String, String> arrDatabasesSort;
        DatabasesModel databases = new DatabasesModel();
        arrDatabases = databases.fillDatabases();
        arrDatabasesSort = databases.sortHashMapByValues(arrDatabases);

        return ok(databases_edit_config.render("Editar Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrDatabasesSort));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTaskDatabase() {
        Form<ProgTaskDatabasesForm> filledForm = Form.form(ProgTaskDatabasesForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        Databases databases = new Databases();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        LinkedHashMap<String, String> nomConnections;
        nomConnections = databases.fillConnections();

        return ok(databases_prog_task.render("Programaci\u00F3n Tarea Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTaskEditDatabase() {
        Form<ProgTaskDatabasesForm> filledForm = Form.form(ProgTaskDatabasesForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        Databases databases = new Databases();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        LinkedHashMap<String, String> nomConnections;
        nomConnections = databases.fillConnections();

        return ok(databases_edit_prog_task.render("Editar Programaci\u00F3n Tarea Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddFileServerConfig() {
        Form<FileServerForm> filledForm = Form.form(FileServerForm.class);
        return ok(fileServer_config.render("Configuracio\u00F3n de Servidor de archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditFileServerConfig() {
        Form<FileServerForm> filledForm = Form.form(FileServerForm.class);
        return ok(fileServer_edit_config.render("Editar Servidor de archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTaskFileServer() {
        Form<ProgTaskFileserversForm> filledForm = Form.form(ProgTaskFileserversForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        FileServers fileServers = new FileServers();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        LinkedHashMap<String, String> nomConnections;
        nomConnections = fileServers.fillConnections();

        return ok(fileServer_prog_task.render("Programaci\u00F3n Tarea Servidor de archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTaskEditFileServer() {
        Form<ProgTaskFileserversForm> filledForm = Form.form(ProgTaskFileserversForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        FileServers fileServers = new FileServers();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        LinkedHashMap<String, String> nomConnections;
        nomConnections = fileServers.fillConnections();

        return ok(fileServer_edit_prog_task.render("Editar Programaci\u00F3n Tarea Servidor de archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes, nomConnections));
    }

    @Security.Authenticated(Secured.class)
    public static Result showCleanDataDatabase() {
        return ok(databases_cleanData.render("Load CleanDataService Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showCleanDataFileServer() {
        return ok(fileServer_cleanData.render("Load CleanDataService File server", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showOtrasConfigCleanData() {
        Form<CleanDataServiceForm> filledForm = Form.form(CleanDataServiceForm.class);
        return ok(cleanDataService_config.render("Otras configuraciones CleanDataService", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showLoadOthersFile() {
        Form<uploadFileIndexerForm> filledForm = Form.form(uploadFileIndexerForm.class);
        HashMap<String, String> arrScripts;
        arrScripts = UploadOtherFiles.fillScripts();
        return ok(upLoadOthersFile.render("Indexar archivos Excel", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), arrScripts, filledForm));
    }

    @Security.Authenticated(Secured.class)
    public static Result showSolrToSolr() {
        Form<uploadSolrToSolrForm> filledForm = Form.form(uploadSolrToSolrForm.class);
        return ok(loadCopySolrToSolr.render("Copia de Solr a Solr", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    public static Result showCollectionAdmin() {

        PropertiesFile pf = new PropertiesFile();
        pf.loadProperties();
        pf.loadUrlApiConfig();

        Form<QueryAndIndexingForm> filledForm = Form.form(QueryAndIndexingForm.class);

        Form<RouteQueryForm> filledRouteQueryForm = routeQueryForm.bindFromRequest();

        String urlApiSolr = pf.getUrlApiSolr();

        if (urlApiSolr == null || urlApiSolr.isEmpty()) {
            return badRequest(config_admin_collections.render("Configurar URL de la API", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledRouteQueryForm));
        } else {
            HashMap<String, String> arrOperations;
            HashMap<String, String> arrOperationsSort;
            OperationsModel operations = new OperationsModel();
            arrOperations = operations.fillOperations();
            arrOperationsSort = operations.sortHashMapByValues(arrOperations);

            HashMap<String, String> arrDirectories;
            HashMap<String, String> arrDirectoriesSort;
            CopyCollectionsInfo copyCollections = new CopyCollectionsInfo();
            arrDirectories = copyCollections.fillDirectories();
            arrDirectoriesSort = copyCollections.sortHashMapByValues(arrDirectories);

            return ok(admin_collections_solr.render("Consultar, guardar e indexar", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrOperationsSort, arrDirectoriesSort));
        }
    }

    public static Result showApiConfig() {
        Form<RouteQueryForm> filledForm = Form.form(RouteQueryForm.class);
        return ok(config_admin_collections.render("Configurar url de la API", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm));
    }

    public static Result loadUrlConfig() {
        PropertiesFile pf = new PropertiesFile();
        pf.loadUrlApiConfig();
        String concat = pf.getUrlApiSolr();
        return ok(concat);
    }

    public static Result loadAdminUsers() {
        String publicIP = retrievePublicIP();
        String concat = "http://" + publicIP + "/AuthDashboards/acceso/mainMenu";
        return ok(concat);
    }

    public static String retrievePublicIP() {
        String publicIP = "";
        BufferedReader br;
        FileReader fr;
        String setupPathconf = "/opt/data/IFindIt/admin_dashboards/admin_serviceDash/temp/docker0.properties";
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

    @Security.Authenticated(Secured.class)
    public static Result showAdminUsers() {
        return ok(admin_users_dashboards.render("Admnistrar usuarios dashboards", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showBackup() {
        return ok(doBackUp.render("Copia de seguridad", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    public static Result showScheduleBackup() {
        Form<ScheduleTaskBackUpForm> filledForm = Form.form(ScheduleTaskBackUpForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        return ok(scheduleBackUp.render("Copia de seguridad", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes));
    }

    @Security.Authenticated(Secured.class)
    public static Result showScheduleBackUpEdit() {
        Form<ScheduleTaskBackUpForm> filledForm = Form.form(ScheduleTaskBackUpForm.class);

        LinkedHashMap<String, String> arrListDaysWeek;
        FillLists fillLists = new FillLists();
        arrListDaysWeek = fillLists.fillDayWeek();

        LinkedHashMap<String, String> arrListDayMonth;
        arrListDayMonth = fillLists.fillDayMonth();

        LinkedHashMap<String, String> arrListHours;
        arrListHours = fillLists.fillHours();

        LinkedHashMap<String, String> arrListMinutes;
        arrListMinutes = fillLists.fillMinutes();

        return ok(scheduleBackUp_edit.render("Editar Programaci\u00F3n Tarea Bases de datos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListDaysWeek, arrListDayMonth, arrListHours, arrListMinutes));
    }

    @Security.Authenticated(Secured.class)
    public static Result showRestoreBackup() {
        return ok(restoreBackUp.render("Copia de seguridad", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showNewConfigOtherFiles() {
        Form<uploadFileForm> filledForm = Form.form(uploadFileForm.class);
        HashMap<String, String> arrScripts;
        arrScripts = UploadOtherFiles.fillScripts();
        return ok(upLoadOthersFile_new_config.render("Crear configuracion otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrScripts));
    }

    @Security.Authenticated(Secured.class)
    public static Result showEditConfigOtherFiles() {
        Form<uploadFileForm> filledForm = Form.form(uploadFileForm.class);
        HashMap<String, String> arrScripts;
        arrScripts = UploadOtherFiles.fillScripts();
        return ok(upLoadOthersFile_edit_config.render("Crear configuracion otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrScripts));
    }

    @Security.Authenticated(Secured.class)
    public static Result showAddScriptOtherFiles() {
        return ok(upLoadOthersFile_add_script.render("Agregar script de limpieza", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTareaOtherFiles() {
        Form<ProgTareaOthersFilesForm> filledForm = Form.form(ProgTareaOthersFilesForm.class);

        LinkedHashMap<String, String> arrListaSemanas;
        ProgTarea progTarea = new ProgTarea();
        arrListaSemanas = progTarea.fillDiaSemana();

        LinkedHashMap<String, String> arrListaDiaMes;
        arrListaDiaMes = progTarea.fillDiasMes();

        LinkedHashMap<String, String> arrListaHoras;
        arrListaHoras = progTarea.fillHoras();

        LinkedHashMap<String, String> arrListaMinutos;
        arrListaMinutos = progTarea.fillMinutos();

        HashMap<String, String> arrConfigurations;
        HashMap<String, String> arrConfigurationsSort;
        ConfigurationsOthersFiles configurationsOthersFiles = new ConfigurationsOthersFiles();
        arrConfigurations = configurationsOthersFiles.fillConfigurations();
        arrConfigurationsSort = configurationsOthersFiles.sortHashMapByValues(arrConfigurations);
        return ok(upLoadOthersFile_prog_tarea.render("Programaci\u00F3n Tarea otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListaSemanas, arrListaDiaMes, arrListaHoras, arrListaMinutos, arrConfigurationsSort));
    }

    @Security.Authenticated(Secured.class)
    public static Result showProgTareaEditOtherFiles() {
        Form<ProgTareaOthersFilesForm> filledForm = Form.form(ProgTareaOthersFilesForm.class);

        LinkedHashMap<String, String> arrListaSemanas;
        ProgTarea progTarea = new ProgTarea();
        arrListaSemanas = progTarea.fillDiaSemana();

        LinkedHashMap<String, String> arrListaDiaMes;
        arrListaDiaMes = progTarea.fillDiasMes();

        LinkedHashMap<String, String> arrListaHoras;
        arrListaHoras = progTarea.fillHoras();

        LinkedHashMap<String, String> arrListaMinutos;
        arrListaMinutos = progTarea.fillMinutos();

        HashMap<String, String> arrConfigurations;
        HashMap<String, String> arrConfigurationsSort;
        ConfigurationsOthersFiles configurationsOthersFiles = new ConfigurationsOthersFiles();
        arrConfigurations = configurationsOthersFiles.fillConfigurations();
        arrConfigurationsSort = configurationsOthersFiles.sortHashMapByValues(arrConfigurations);
        return ok(upLoadOthersFile_edit_prog_tarea.render("Editar Programaci\u00F3n Tarea otros archivos", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), filledForm, arrListaSemanas, arrListaDiaMes, arrListaHoras, arrListaMinutos, arrConfigurationsSort));
    }

}
