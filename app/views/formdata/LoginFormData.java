package views.formdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import play.data.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;
import models.ConnectionDB;
import models.UserInfoDB;
import org.apache.log4j.Logger;

/**
 * Backing class for the login form.
 */
public class LoginFormData {

    /**
     * The submitted email.
     */
    public String email = "";
    /**
     * The submitted password.
     */
    public String password = "";

    public String profile = ""; 
    
    static Logger logger = Logger.getLogger(LoginFormData.class);
    
    /**
     * Required for form instantiation.
     */
    public LoginFormData() {
    }
   
    /**
     * Validates Form<LoginFormData>. Called automatically in the controller by
     * bindFromRequest(). Checks to see that email and password are valid
     * credentials.
     *
     * @return Null if valid, or a List[ValidationError] if problems found.
     */
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        try {
            UserInfoDB.removeUsers();
            
            Connection con;
            con = ConnectionDB.getConector();
            
            ResultSet result = ConnectionDB.RealizarConsulta("SELECT * FROM user_service;", con);
            ArrayList<String> usersId = new ArrayList<>();
            while (result.next()) {
                usersId.add(result.getString(1));
            }
            if(usersId.size()>0){
                for(int i=0; i<usersId.size();i++){
                    String select = "SELECT p.ID_PERMISSION, u.NAME_USER, u.ADDRESS, u.PASSWORD, u.ID_GROUP FROM permission_service p, permission_profile pp, profile_group pg, user_service u\n" +
                                    "WHERE p.ID_PERMISSION = pp.ID_PERMISSION\n" +
                                    "AND pp.ID_PROFILE = pg.ID_PROFILE\n" +
                                    "AND pg.ID_GROUP = u.ID_GROUP\n" +
                                    "AND u.ID_USER = '"+usersId.get(i)+"'";

                    ArrayList<String> permissions = new ArrayList<>();
                    con = ConnectionDB.getConector();
                    ResultSet resultPermission = ConnectionDB.RealizarConsulta(select, con);
                    String name="", emailStr="", passwordStr="", group="";
                    while (resultPermission.next()) {
                        permissions.add(resultPermission.getString("ID_PERMISSION"));
                        name = resultPermission.getString("NAME_USER");
                        emailStr = resultPermission.getString("ADDRESS");
                        passwordStr = resultPermission.getString("PASSWORD");
                        group = resultPermission.getString("ID_GROUP");
                    }

                    if(permissions.size()>0){
                        UserInfoDB.addUserInfo(usersId.get(i), name, emailStr, passwordStr, group, permissions);
                    }
                }
            }
            
            if (!UserInfoDB.isValid(email, password)) {
                errors.add(new ValidationError("email", ""));
                errors.add(new ValidationError("password", ""));
            }
            ConnectionDB.cerrarConexion(con);
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(""+ex);
        }
        return (errors.size() > 0) ? errors : null;
    }

}
