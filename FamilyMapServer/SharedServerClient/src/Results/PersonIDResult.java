package Results;

public class PersonIDResult {

    //For a successful response body
    private String associatedUsername; // Name of user account this person belongs to
    private String personID; // Person’s unique ID
    private String firstName; // Person’s first name
    private String lastName; // Person’s last name
    private String gender; // Person’s gender (m or f)
    private String fatherID; // ID of person’s father [OPTIONAL, can be missing]
    private String motherID; // ID of person’s mother [OPTIONAL, can be missing]
    private String spouseID; // ID of person’s spouse [OPTIONAL, can be missing]

    //For an unsuccessful response body
    private String message; // For errors. Can be;
                                // Invalid auth token
                                // Invalid personID parameter
                                // Requested person does not belong to this user
                                // Internal server error

    private String success; // Boolean identifier

    public PersonIDResult() {}

    /**
     * Creates a Person result for a successful query.
     *
     * @param associatedUsername Name of user account this person belongs to
     * @param personID Person’s unique ID
     * @param firstName Person’s first name
     * @param lastName Person’s last name
     * @param gender Person’s gender (m or f)
     * @param fatherID ID of person’s father [OPTIONAL, can be missing]
     * @param motherID ID of person’s mother [OPTIONAL, can be missing]
     * @param spouseID ID of person’s spouse [OPTIONAL, can be missing]
     * @param success
     */
    public PersonIDResult(String associatedUsername, String personID, String firstName, String lastName, String gender, String fatherID, String motherID, String spouseID, String success) {
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = success;
    }

    /**
     * Creates a Person result for an unsuccessful query.
     *
     * @param message Error message "Invalid auth token", "Invalid personID parameter", "Requested person does not belong to this user", or "Internal server error"
     * @param success Boolean identifier
     */
    public PersonIDResult(String message, String success) {
        this.message = message;
        this.success = success;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
