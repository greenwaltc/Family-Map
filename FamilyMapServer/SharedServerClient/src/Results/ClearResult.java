package Results;

public class ClearResult {

    private String message; // Clear succeeded or clear failed
    private String success; // Boolean identifier: clear success/failure

    public ClearResult() {}

    /**
     * Creates a clear result depending on results from clear service
     *
     * @param message Describes whether the clear succeeded or an accompanying error message.
     * @param success Boolean identifier
     */
    public ClearResult(String message, String success) {
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
