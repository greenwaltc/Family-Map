package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Model.AuthToken;
import Model.Event;
import Requests.EventRequest;
import Results.EventResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class EventServiceTest {

    EventRequest request;
    EventService service;
    EventResult result;

    private Database db;
    private EventDao edao;
    private AuthTokenDao adao;
//    private Event testEvent = new Event("TestEventID", "TestAssocUsername",
//            "TestPersonID", 0.0, 0.0, "TestCountry", "TestCity",
//            "TestEventType", 3000);
    int num_adds = 100;
    ArrayList<Event> events = null;

    @BeforeEach
    void setUp() {
        try {
            db = new Database();
            db.openConnection();
            db.clearTables();

            edao = new EventDao(db.getConnection());
            events = createDummies();
            edao.insert(events);

            adao = new AuthTokenDao(db.getConnection());
            adao.insert( new AuthToken("TestAssocUsername", "TestAuthToken"));

            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        request = new EventRequest("TestAuthToken");
        service = new EventService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void eventPositive() {
        try {
            result = service.event(request);

            Assertions.assertEquals(result.getData().size(), events.size());

            for (int i = 0; i < num_adds; ++i) {
                Assertions.assertEquals(result.getData().get(i).getEventID(), events.get(i).getEventID());
                Assertions.assertEquals(result.getData().get(i).getAssociatedUsername(), events.get(i).getAssociatedUsername());
                Assertions.assertEquals(result.getData().get(i).getPersonID(), events.get(i).getPersonID());
                Assertions.assertEquals(result.getData().get(i).getEventType(), events.get(i).getEventType());
                Assertions.assertEquals(result.getData().get(i).getYear(), events.get(i).getYear());
                Assertions.assertEquals(result.getData().get(i).getLongitude(), events.get(i).getLongitude());
                Assertions.assertEquals(result.getData().get(i).getLatitude(), events.get(i).getLatitude());
                Assertions.assertEquals(result.getData().get(i).getCity(), events.get(i).getCity());
                Assertions.assertEquals(result.getData().get(i).getCountry(), events.get(i).getCountry());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void eventNegative() {

        // Try to find an event using an auth token that doesn't exist
        request = new EventRequest("Fake auth token");
        try {
            Assertions.assertNull(service.event(request));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        // Clear the tables before the next run
        try {
            db = new Database();
            db.openConnection();
            db.clearTables();
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Try to find the event with a PREVIOUSLY EXISTING (now not) auth token
        request = new EventRequest("TestAuthToken");
        try {
            result = service.event(request);
            Assertions.assertNull(result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Event> createDummies() {

        ArrayList<Event> events = new ArrayList<>();

        for (int i = 1; i <= num_adds; ++i) {
            events.add( new Event(Integer.toString(i), "TestAssocUsername", Integer.toString(i),
                    0.0, 0.0, Integer.toString(i), Integer.toString(i), Integer.toString(i), 0) );
        }

        return events;
    }
}