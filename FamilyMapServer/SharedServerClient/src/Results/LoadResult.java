package Results;

public class LoadResult {

    private String message; // Ex: Successfully added X users, Y persons, and Z events to the database.
                                // Or: Description of the error
    private String success; // Boolean identifier

    public LoadResult() {}

    /**
     * Creates a load result object with the data provided by the load service
     *
     * @param message Describes the number of persons, users, and events added in a success, or an error message in a failure
     * @param success Boolean identifier
     */
    public LoadResult(String message, String success) {
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
