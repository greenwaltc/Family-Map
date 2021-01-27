package Results;

public class LoginResult {

    //For success response body
    String authToken; // Non-empty auth token string
    String userName; // User name passed in with the request
    String personID; // Non-empty string containing the Person ID of the
                        // user's generated Person object

    //For failure response body
    private String message; // Description of the error

    private String success; // Boolean identifier

    public LoginResult() {}

    /**
     * Creates a login result with the data provided by the login service
     *
     * @param success Boolean identifier
     * @param authToken Non-empty auth token string
     * @param userName User name passed in with request
     * @param personID Non-empty string containing the Person ID of the user's generated Person object
     * @param message Description of the error
     */
    public LoginResult(String success, String authToken,
                       String userName, String personID,
                       String message) {
        this.success = success;
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.message = message;
    }

    public LoginResult(String authToken, String userName, String personID, String success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = success;
    }

    public LoginResult(String message, String success) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {

        if (success.equals("false")) {
            return true;
        }
        return false;
    }

    public void setSuccess(String success) {
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
}
