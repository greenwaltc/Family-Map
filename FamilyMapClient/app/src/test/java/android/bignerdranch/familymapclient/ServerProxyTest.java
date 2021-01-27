package android.bignerdranch.familymapclient;


import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.net.MalformedURLException;
import java.net.URL;

import AsyncTasks.GetEventsTask;
import AsyncTasks.GetPersonsTask;
import AsyncTasks.LoginTask;
import AsyncTasks.RegisterTask;
import Model.HttpPort;
import Model.Person;
import Requests.ClearRequest;
import Requests.EventRequest;
import Requests.LoginRequest;
import Requests.PersonRequest;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;

public class ServerProxyTest {

    private ServerProxy mServerProxy = null;
    private URL mLoginURL = null;
    private URL mRegisterURL = null;
    private URL mPersonURL = null;
    private URL mEventURL = null;
    private LoginRequest mLoginRequest = null;
    private RegisterRequest mRegisterRequest = null;
    HttpPort mPort = new HttpPort("localhost", "8080");

    @BeforeEach
    public void setup() {
        mServerProxy = new ServerProxy();

        // Create the login URL
        LoginTask loginTask = new LoginTask();
        LoginTask.LoginTaskLink lLink = LoginTask.LoginTaskLink.getLoginLink();
        loginTask.setHttpPort(mPort); // Needed to create the url
        mLoginURL = loginTask.createURL(lLink);

        // Create the register URL
        RegisterTask registerTask = new RegisterTask();
        RegisterTask.RegisterTaskLink rLink = RegisterTask.RegisterTaskLink.getRegisterLink();
        registerTask.setHttpPort(mPort);
        mRegisterURL = registerTask.createURL(rLink);

        // Create the person URL
        GetPersonsTask personsTask = new GetPersonsTask();
        GetPersonsTask.PersonTaskLink pLink = GetPersonsTask.PersonTaskLink.getPersonsLink();
        personsTask.setHttpPort(mPort);
        mPersonURL = personsTask.createURL(pLink);

        // Create the event URL
        GetEventsTask eventsTask = new GetEventsTask();
        GetEventsTask.EventTaskLink eLink = GetEventsTask.EventTaskLink.getEventsLink();
        eventsTask.setHttpPort(mPort);
        mEventURL = eventsTask.createURL(eLink);

        // Create the login request
        mLoginRequest = new LoginRequest("Test", "Test");

        // Create the register request
        mRegisterRequest = new RegisterRequest("Test", "Test",
                "Test", "Test", "Test", "m");
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
    public void testLogin() {

        // Register the test user
        registerTestUser();

        // Log the new user in and check response
        mServerProxy = new ServerProxy(mLoginURL);
        LoginResult loginResult = mServerProxy.Login(mLoginRequest);

        Assert.assertNull(loginResult.getMessage());
        Assert.assertEquals("Test", loginResult.getUserName());
        Assert.assertNotNull(loginResult.getAuthToken());
        Assertions.assertNotNull(loginResult.getPersonID());
    }

    @Test
    public void testRegister() {

        // Register the test user
        RegisterResult registerResult = registerTestUser();

        Assert.assertNull(registerResult.getMessage());
        Assert.assertEquals("Test", registerResult.getUserName());
        Assert.assertNotNull(registerResult.getAuthToken());
        Assertions.assertNotNull(registerResult.getPersonID());
    }

    @Test
    public void testPerson() {

        // Register the test user
        RegisterResult registerResult = registerTestUser();

        String authToken = registerResult.getAuthToken();

        // Create the person request
        PersonRequest personRequest = new PersonRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(mPersonURL);

        // Get the result
        PersonResult personResult = mServerProxy.Person(personRequest);

        Assertions.assertNull(personResult.getMessage());
        Assertions.assertNotEquals(0, personResult.getData().size());
        // User will be first index in people data
        Person user = personResult.getData().get(0);
        Assertions.assertEquals("Test", user.getFirstName());
    }

    @Test
    public void testEvent() {

        // Register the test user
        RegisterResult registerResult = registerTestUser();

        String authToken = registerResult.getAuthToken();

        // Create the event request
        EventRequest eventRequest = new EventRequest(authToken);

        // Set up the proxy
        mServerProxy = new ServerProxy(mEventURL);

        // Get the result
        EventResult eventResult = mServerProxy.Event(eventRequest);

        Assertions.assertNull(eventResult.getMessage());
        Assertions.assertNotEquals(0, eventResult.getData());
        Assertions.assertEquals("Test", eventResult.getData().get(0).getAssociatedUsername());
    }

    private RegisterResult registerTestUser() {
        mServerProxy = new ServerProxy(mRegisterURL);
        return mServerProxy.Register(mRegisterRequest);
    }
}