package Requests;

import Model.Person;

public class PersonIDRequest {

    private String personID; // Provided person ID from the URL
    private String authToken; // User's authorization token

    public PersonIDRequest() {}

    /**
     * Creates a Person ID request
     *
     * @param personID Provided person ID from the URL
     * @param authToken User's authorization token
     */
    public PersonIDRequest(String personID, String authToken) {
        this.personID = personID;
        this.authToken = authToken;
    }

    public String getPersonID() {
        return personID;
    }
}
