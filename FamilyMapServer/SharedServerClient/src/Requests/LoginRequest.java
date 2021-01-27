package Requests;

import java.lang.reflect.Field;

public class LoginRequest {

    private String userName, password; // Non-empty string

    public LoginRequest() {}

    /**
     * Creates a login request object with the data provided by the
     * login handler
     *
     * @param userName Non-empty string
     * @param password Non-empty string
     */
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if any of the class variables have a null value
     * @return
     */
    public boolean anyNull() {

        for (Field f : getClass().getDeclaredFields()) {
            try {
                if (f.get(this) == null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks if any of the class variables are an empty string
     *
     * @return
     */
    public boolean anyEmpty() {

        for (Field f : getClass().getDeclaredFields()) {
            try {
                if (f.get(this).equals("")) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
