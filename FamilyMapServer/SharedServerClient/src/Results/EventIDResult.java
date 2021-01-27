package Results;

public class EventIDResult {

    // For a successful query
    private String associatedUsername; // Username of user account this event belongs to
                                            // (non-empty string)

    private String eventID; // Event’s unique ID (non-empty string)
    private String personID; // ID of the person this event belongs to (non-empty string)
    private Double latitude; // Latitude of the event’s location (number)
    private Double longitude; // Longitude of the event’s location (number)
    private String country; // Name of country where event occurred (non-empty string)
    private String city; // Name of city where event occurred (non-empty string)
    private String eventType; // Type of event (birth, baptism, etc.) (non-empty string)
    private Integer year; // Year the event occurred (integer)

    // For an unsuccessful query
    private String message; // Description of the error. "Invalid auth token", "Invalid eventID parameter",
                                //"Requested event does not belong to this user", "Internal server error"
    private String success; // Boolean identifier

    public EventIDResult() {}

    /**
     * Creates an event ID result object upon a successful query
     *
     * @param associatedUsername Username of user account this event belongs to (non-empty string)
     * @param eventID Event’s unique ID (non-empty string)
     * @param personID ID of the person this event belongs to (non-empty string)
     * @param latitude Latitude of the event’s location (number)
     * @param longitude Longitude of the event’s location (number)
     * @param country Name of country where event occurred (non-empty string)
     * @param city Name of city where event occurred (non-empty string)
     * @param eventType Type of event (birth, baptism, etc.) (non-empty string)
     * @param year Year the event occurred (integer)
     * @param success Boolean identifier
     */
    public EventIDResult(String associatedUsername, String eventID, String personID, double latitude, double longitude, String country, String city, String eventType, int year, String success) {
        this.associatedUsername = associatedUsername;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        this.success = success;
    }

    /**
     * Creates an event ID result object upon an unsuccessful query
     * @param message Description of the error. "Invalid auth token", "Invalid eventID parameter", "Requested event does not belong to this user", "Internal server error"
     * @param success Boolean identifier
     */
    public EventIDResult(String message, String success) {
        this.message = message;
        this.success = success;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
