package Results;

import Requests.FillRequest;

public class FillResult {

    private String message; // Ex: Successfully added X persons and Y events to the database.
                                // Or: Description of the error
    private String success; // Boolean identifier

    public FillResult() {}

    /**
     * Creates a fill result with the appropriate message and boolean identifier
     *
     * @param message Either details persons/events added or describes an error
     * @param success Boolean identifier
     */
    public FillResult(String message, String success) {
        this.message = message;
        this.success = success;
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
