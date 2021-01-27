package Requests;

import java.lang.reflect.Field;

public class RegisterRequest {

    private String userName, password, email, firstName, lastName; //Non-empty string
    private String gender; //"f" or "m"

    public RegisterRequest () {}

    /**
     * Creates a register request with the data provided by the
     * register handler.
     *
     * @param userName Non-empty string
     * @param password Non-empty string
     * @param email Non-empty string
     * @param firstName Non-empty string
     * @param lastName Non-empty string
     * @param gender "f" or "m"
     */
    public RegisterRequest(String userName, String password,
                           String email, String firstName,
                           String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
