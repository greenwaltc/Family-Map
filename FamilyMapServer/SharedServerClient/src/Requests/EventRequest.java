package Requests;

public class EventRequest {

    private String authToken; // User's provided authorization token

    public EventRequest() {}

    /**
     * Creates event request with provided auth token
     *
     * @param authToken User's authorization token
     */
    public EventRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
