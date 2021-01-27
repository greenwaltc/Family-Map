package Requests;

public class EventIDRequest {

    private String eventID; // Requested Event ID
    private String authToken; // User's authorization token

    public EventIDRequest() {}

    /**
     * Creates event ID request
     *
     * @param eventID Requested event ID
     * @param authToken User's authorization token
     */
    public EventIDRequest(String eventID, String authToken) {
        this.eventID = eventID;
        this.authToken = authToken;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
