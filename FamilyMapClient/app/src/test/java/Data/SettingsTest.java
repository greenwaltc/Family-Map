package Data;

import android.bignerdranch.familymapclient.ServerProxy;

import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import AsyncTasks.GetEventsTask;
import AsyncTasks.GetPersonsTask;
import AsyncTasks.RegisterTask;
import Model.Event;
import Model.HttpPort;
import Model.Person;
import Requests.ClearRequest;
import Requests.EventRequest;
import Requests.PersonRequest;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.PersonResult;
import Results.RegisterResult;

public class SettingsTest {

    private Settings mSettings;
    HttpPort mPort = new HttpPort("localhost", "8080");
    private ServerProxy mServerProxy;
    private DataCache mCache = DataCache.getInstance();

    @BeforeEach
    public void setup() {
        mSettings = Settings.getInstance();

        clearDatabase();
        RegisterResult registerResult = registerTestUser();
        String authToken = registerResult.getAuthToken();

        loadPersons(authToken);
        loadEvents(authToken);
    }

    @AfterEach
    public void teardown() {
        mSettings.clearSettings();
        clearDatabase();
    }

    @Test
    public void testFilterFathersSide() {
        mSettings.setShowMotherSide(false);
        mSettings.filterPersons();
        ArrayList<Person> fatherSidePersons = mSettings.getFilteredPersons();

        // Get the user's mother for checking
        Person user = mCache.getUserPerson();

        // Verify that none of the mothers side people are in the filtered people
        Assertions.assertEquals(true, checkSidesOK(user.getMotherID(), fatherSidePersons));
    }

    @Test
    public void testFilterMothersSide() {
        mSettings.setShowFatherSide(false);
        mSettings.filterPersons();
        ArrayList<Person> motherSidePersons = mSettings.getFilteredPersons();

        // Get the user's mother for checking
        Person user = mCache.getUserPerson();

        // Verify that none of the mothers side people are in the filtered people
        Assertions.assertEquals(true, checkSidesOK(user.getFatherID(), motherSidePersons));
    }

    @Test
    public void testFilterMaleEvents() {
        mSettings.setShowFemaleEvents(false);
        checkFilteredEventsOK();
    }

    @Test
    public void testFilterFemaleEvents() {
        mSettings.setShowMaleEvents(false);
        checkFilteredEventsOK();
    }

    private void checkFilteredEventsOK() {
        mSettings.filterPersons();
        mSettings.filterEvents();

        ArrayList<Person> filteredPersons = mSettings.getFilteredPersons();
        ArrayList<Event> filteredEvents = DataCache.getInstance().getCurrentEvents();

        for (int i = 0; i < filteredEvents.size(); i++) {
            boolean eventPersonPairFound = false;

            // Get the person associated with the event
            Person associatedPerson = mCache.findPersonByEvent(filteredEvents.get(i));

            // Verify that person is in the filtered persons
            for (int j = 0; j < filteredPersons.size(); j++) {
                Person currentPerson = filteredPersons.get(j);
                if (currentPerson.getPersonID().equals(associatedPerson.getPersonID())) {
                    eventPersonPairFound = true;
                    break;
                }
            }

            Assertions.assertEquals(true, eventPersonPairFound); // This will be true if each filtered event has an associated person in the filtered persons
        }
    }

    private boolean checkSidesOK(String personID, ArrayList<Person> filteredPeople) {

        // Base case
        if (personID == null) return true;

        Person person = mCache.getPersonByID(personID);

        // Traverse left side
        checkSidesOK(person.getMotherID(), filteredPeople);

        // Traverse right side
        checkSidesOK(person.getFatherID(), filteredPeople);

        // Visit node
        // Check each person in the filtered people to verify the current person is not found
        for (int i = 0; i < filteredPeople.size(); i++) {
            Person currentPerson = filteredPeople.get(i);
            if (currentPerson.getPersonID().equals(person.getPersonID())) return false;
        }
        return true;
    }

    private void clearDatabase() {
        // Clear the database
        // Necessary so that test cases work more than once
        URL url = null;
        try {
            url = new URL("http://" +
                    mPort.getServerHost() + ":" +
                    mPort.getServerPort() +
                    "/clear");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mServerProxy = new ServerProxy(url);
        ClearRequest clearRequest = new ClearRequest();
        mServerProxy.clear(clearRequest);
    }

    private RegisterResult registerTestUser() {
        // Create the register request
        RegisterRequest registerRequest = new RegisterRequest("Test", "Test",
                "Test", "Test", "Test", "m");

        // Create the register URL
        RegisterTask registerTask = new RegisterTask();
        RegisterTask.RegisterTaskLink rLink = RegisterTask.RegisterTaskLink.getRegisterLink();
        registerTask.setHttpPort(mPort);
        URL registerURL = registerTask.createURL(rLink);

        mServerProxy = new ServerProxy(registerURL);
        return mServerProxy.Register(registerRequest);
    }

    private void loadPersons(String authToken) {
        // Create the person URL
        GetPersonsTask personsTask = new GetPersonsTask();
        GetPersonsTask.PersonTaskLink pLink = GetPersonsTask.PersonTaskLink.getPersonsLink();
        personsTask.setHttpPort(mPort);
        URL personURL = personsTask.createURL(pLink);

        // Create the person request
        PersonRequest personRequest = new PersonRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(personURL);

        // Get the result
        PersonResult personResult = mServerProxy.Person(personRequest);
        Person user = personResult.getData().get(0);
        mCache.setUserPerson(user);
        mCache.setUserPersonID(user.getPersonID());
        mCache.loadPersons(personResult.getData());
    }

    private void loadEvents(String authToken) {

        // Create the event URL
        GetEventsTask eventsTask = new GetEventsTask();
        GetEventsTask.EventTaskLink eLink = GetEventsTask.EventTaskLink.getEventsLink();
        eventsTask.setHttpPort(mPort);
        URL eventURL = eventsTask.createURL(eLink);

        // Create the event request
        EventRequest eventRequest = new EventRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(eventURL);

        // Get the result
        EventResult eventResult = mServerProxy.Event(eventRequest);
        mCache.loadEvents(eventResult.getData());
    }
}