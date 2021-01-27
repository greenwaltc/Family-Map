package Results;

import java.util.ArrayList;
import Model.Event;

public class EventResult {

    // For a successful query
    private ArrayList<Event> data; // Array of Event objects

    // For a failed query
    private String message; // Description of the error
                                // "Invalid auth token", "Internal server error"

    private String success; // Boolean identifier

    public EventResult() {}

    /**
     * Creates an event result object for a successful query
     *
     * @param data Array of event objects
     * @param success Boolean identifier
     */
    public EventResult(ArrayList<Event> data, String success) {
        this.data = data;
        this.success = success;
    }

    /**
     * Creates an event result object for an unsuccessful query
     *
     * @param message Description of the error -- "Invalid auth token", "Internal server error"
     * @param success Boolean identifier
     */
    public EventResult(String message, String success) {
        this.message = message;
        this.success = success;
    }

    public ArrayList<Event> getData() {
        return data;
    }

    public void setData(ArrayList<Event> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
