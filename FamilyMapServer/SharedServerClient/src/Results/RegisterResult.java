package Results;

public class RegisterResult {

    //For a success response body
    private String authToken; // Non-empty auth token string
    private String userName; // User name passed in with request
    private String personID; // Non-empty string containing the Person ID of the
                                // user's generated Person object

    //For an error response body
    private String message; // Description of the error

    private String success; // Boolean identifier

    public RegisterResult() {}

    /**
     * Creates a register result with the data provided by the register service
     * @param authToken Non-empty auth token string
     * @param userName username passed in with the request
     * @param personID Non-empty string containing the Person ID of the user's generated Person object
     * @param message Description of the error
     * @param success Boolean identifier
     */
    public RegisterResult(String authToken, String userName,
                          String personID, String message,
                          String success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.message = message;
        this.success = success;
    }

    /**
     * For an unsuccessful response body.
     * @param message
     * @param success
     */
    public RegisterResult(String message, String success) {
        this.success = success;
        this.message = message;
    }

    /**
     * For use in a successful response body because no error message should be given.
     * Params are the same for the default constructor.
     *
     * @param success
     * @param authToken
     * @param userName
     * @param personID
     */
    public RegisterResult(String authToken, String userName, String personID, String success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = success;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
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
