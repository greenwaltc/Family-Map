package Requests;

import java.util.ArrayList;
import Model.User;
import Model.Person;
import Model.Event;

public class LoadRequest {

    private ArrayList<User> users; // Array of User objects
    private ArrayList<Person> persons; // Array of Person objects
    private ArrayList<Event> events; // Array of Event objects

    public LoadRequest() {}

    /**
     * Creates a load request from the data provided by the load handler
     *
     * The users property in the request body contains an array of users
     * to be created. The persons and events properties contain family
     * history information for these users.  The objects contained in the
     * persons and events arrays should be added to the server’s
     * database.  The objects in the users array have the same format as
     * those passed to the /user/register API with the addition of the
     * personID.  The objects in the persons array have the same format
     * as those returned by the /person/[personID] API.  The objects in the
     * events array have the same format as those returned by the
     * /event/[eventID] API.
     *
     * @param userObjects Array of User objects
     * @param personObject Array of Person objects
     * @param eventObjects Array of Event objects
     */
    public LoadRequest(ArrayList<User> userObjects, ArrayList<Person> personObject, ArrayList<Event> eventObjects) {
        this.users = userObjects;
        this.persons = personObject;
        this.events = eventObjects;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
