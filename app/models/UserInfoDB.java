package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides an in-memory repository for UserInfo.
 * Storing credentials in the clear is kind of bogus.
 * @author Philip Johnson
 */
public class UserInfoDB {
  
  private static Map<String, UserInfo> userinfos = new HashMap<String, UserInfo>();
  
  /**
   * Adds the specified user to the UserInfoDB.
   * @param idUser
   * @param name Their name.
   * @param email Their email.
   * @param password Their password. 
   * @param group 
   * @param permissions 
   */
  public static void addUserInfo(String idUser, String name, String email, String password, String group, ArrayList<String> permissions) {
    userinfos.put(email, new UserInfo(idUser, name, email, password, group, permissions));
  }
  
  /**
   * Returns true if the email represents a known user.
   * @param email The email.
   * @return True if known user.
   */
  public static boolean isUser(String email) {
    return userinfos.containsKey(email);
  }

  /**
   * Returns the UserInfo associated with the email, or null if not found.
   * @param email The email.
   * @return The UserInfo.
   */
  public static UserInfo getUser(String email) {
    return userinfos.get((email == null) ? "" : email);
  }

  /**
   * Returns true if email and password are valid credentials.
   * @param email The email. 
   * @param password The password. 
   * @return True if email is a valid user email and password is valid for that email.
   */
  public static boolean isValid(String email, String password) {
    return ((email != null) 
            &&
            (password != null) 
            &&
            isUser(email) 
            &&
            getUser(email).getPassword().equals(password));
  }
  
    public static void removeUsers() {
        userinfos.clear();
    }
  
  
}
