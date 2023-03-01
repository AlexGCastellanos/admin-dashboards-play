package models;

import java.util.ArrayList;

/**
 * A simple representation of a user. 
 * @author Philip Johnson
 */
public class UserInfo {
 
  private String idUser;
  private String name;
  private String email;
  private String password;
  private String group;
  private ArrayList<String> permissions;
  
  /**
   * Creates a new UserInfo instance.
   * @param idUser
   * @param name The name.
   * @param email The email.
   * @param password The password.
   * @param group The group
   * @param permissions The permissions for the user in group
   */
  
  public UserInfo(String idUser, String name, String email, String password, String group,  ArrayList<String> permissions) {
    this.idUser = idUser;
    this.name = name;
    this.email = email;
    this.password = password;
    this.group = group;
    this.permissions = permissions;
  }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }
  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }
}
