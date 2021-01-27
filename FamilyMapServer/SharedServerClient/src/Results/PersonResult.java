package Results;

import Model.Person;
import java.util.ArrayList;

public class PersonResult {

    private ArrayList<Person> data; // Array of Person objects

    //For a failed data query
    private String message; // Description of the error

    private String success; // Boolean identifier

    public PersonResult() {}

    /**
     * Creates person result for a successful query
     *
     * @param data Array list of Person objects
     * @param success Boolean identifier
     */
    public PersonResult(ArrayList<Person> data, String success) {
        this.data = data;
        this.success = success;
    }

    /**
     * Creates a person result for a failed query
     *
     * @param message Error message: "Invalid auth token", "Internal server error"
     * @param success Boolean identifier
     */
    public PersonResult(String message, String success) {
        this.message = message;
        this.success = success;
    }

    public ArrayList<Person> getData() {
        return data;
    }

    public void setData(ArrayList<Person> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {

        if (success.equals("true")){
            return true;
        }
        return false;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
