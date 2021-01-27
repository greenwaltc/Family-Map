package Data;

import android.bignerdranch.familymapclient.ServerProxy;

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
import Results.RegisterResult;

public class DataCacheTest {

    HttpPort mPort = new HttpPort("localhost", "8080");
    private ServerProxy mServerProxy = null;

    @BeforeEach
    public void setup() {
        RegisterResult registerResult = registerTestUser();

        DataCache cache = DataCache.getInstance();

        // Load the persons into the data cache
        ArrayList<Person> persons = getPersons(registerResult.getAuthToken());

            // Set the user person ID
        cache.setUserPersonID(persons.get(0).getPersonID());

        cache.loadPersons(persons);

        // Load the events into the database
        ArrayList<Event> events = getEvents(registerResult.getAuthToken());
        cache.loadEvents(events);
    }

    @AfterEach
    public void teardown() {
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

    @Test
    public void testGetPersonLifeEvents() {

        DataCache cache = DataCache.getInstance();

        Person user = cache.getUserPerson();
        Person father = cache.getPersonByID(user.getFatherID());

        ArrayList<Event> fatherLifeEvents = cache.getPersonLifeEvents(father.getPersonID());

        // Test father life events in chronological order
        for (int i = 1; i < fatherLifeEvents.size(); i++) {
            Assertions.assertTrue(fatherLifeEvents.get(i - 1).getYear() <=
                    fatherLifeEvents.get(i).getYear());
        }
    }

    @Test
    public void testGetPersonByID() {

        DataCache cache = DataCache.getInstance();

        // Test getting the user from the data cache
        Person user = cache.getUserPerson();
        Assertions.assertEquals("Test", user.getFirstName());
        Assertions.assertEquals("Test", user.getLastName());

        // Test getting the mother/father from the test user
        Person father = cache.getPersonByID(user.getFatherID());
        Person mother = cache.getPersonByID(user.getMotherID());
        // Verify that spouse IDs are identical for these two
            // This implies that finding A) parental relationships and B) spouse relationships works
        Assertions.assertEquals(father.getSpouseID(), mother.getPersonID());
        Assertions.assertEquals(father.getPersonID(), mother.getSpouseID());

        // Test getting spouse relationships
        Person fatherSpouse = cache.getPersonByID(father.getSpouseID());
        Assertions.assertEquals(fatherSpouse.getPersonID(), mother.getPersonID());
    }

    @Test
    public void testGetChildByID() {

        DataCache cache = DataCache.getInstance();

        Person user = cache.getUserPerson();
        Person father = cache.getPersonByID(user.getFatherID());
        Person child = cache.getChildByID(father.getPersonID());
        Assertions.assertEquals(child.getPersonID(), user.getPersonID());
    }

    private RegisterResult registerTestUser() {

        // Create the register url
        RegisterTask registerTask = new RegisterTask();
        RegisterTask.RegisterTaskLink rLink = RegisterTask.RegisterTaskLink.getRegisterLink();
        registerTask.setHttpPort(mPort);
        URL registerURL = registerTask.createURL(rLink);

        mServerProxy = new ServerProxy(registerURL);

        // Create the register request
        RegisterRequest registerRequest = new RegisterRequest("Test", "Test",
                "Test", "Test", "Test", "m");

        return mServerProxy.Register(registerRequest);
    }

    private ArrayList<Person> getPersons(String authToken) {

        GetPersonsTask personsTask = new GetPersonsTask();
        GetPersonsTask.PersonTaskLink pLink = GetPersonsTask.PersonTaskLink.getPersonsLink();
        personsTask.setHttpPort(mPort);
        URL personURL = personsTask.createURL(pLink);

        // Create the person request
        PersonRequest personRequest = new PersonRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(personURL);

        return mServerProxy.Person(personRequest).getData();
    }

    private ArrayList<Event> getEvents(String authToken) {

        // Create the event URL
        GetEventsTask eventsTask = new GetEventsTask();
        GetEventsTask.EventTaskLink eLink = GetEventsTask.EventTaskLink.getEventsLink();
        eventsTask.setHttpPort(mPort);
        URL eventURL = eventsTask.createURL(eLink);

        // Create the event request
        EventRequest eventRequest = new EventRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(eventURL);

        return mServerProxy.Event(eventRequest).getData();
    }
}