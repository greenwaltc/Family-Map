package Requests;

public class PersonRequest {

    private String authToken;

    public PersonRequest() {}

    /**
     * Creates a person request with the user's authorization token
     * @param authToken User's authorization token
     */
    public PersonRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
