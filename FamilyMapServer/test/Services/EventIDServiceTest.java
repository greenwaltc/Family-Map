package Services;

import DataAccess.*;
import Model.AuthToken;
import Model.Event;
import Requests.EventIDRequest;
import Requests.LoadRequest;
import Results.EventIDResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class EventIDServiceTest {

    Database db;

    private EventIDService service = null;

    private EventDao edao = null;

    private final String USERNAME_SHEILA = "sheila";

    private final Event testEvent = new Event("Mrs_Jones_Surf", USERNAME_SHEILA, "Mrs_Jones",
            -27.9833, 153.4, "Australia", "Gold Coast", "Learned to Surf",
            2000);

    @BeforeEach
    void setUp() {
        service = new EventIDService();
        try {
            db = new Database();
            db.openConnection();
            db.clearTables();
            edao = new EventDao(db.getConnection());
            edao.insert(testEvent);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void eventIDPositive() {

        try {
            EventIDRequest request = new EventIDRequest(testEvent.getEventID(), "");
            EventIDResult result = service.eventID(request);

            Assertions.assertEquals(result.getEventID(), testEvent.getEventID());
            Assertions.assertEquals(result.getAssociatedUsername(), testEvent.getAssociatedUsername());
            Assertions.assertEquals(result.getPersonID(), testEvent.getPersonID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void eventIDNegative() {

        // Search for an event that isn't there
        try {
            EventIDRequest request = new EventIDRequest("Fake event ID", "");
            EventIDResult result = service.eventID(request);

            Assertions.assertNull(result);

        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}