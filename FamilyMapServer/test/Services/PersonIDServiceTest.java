package Services;

import DataAccess.*;
import Model.Event;
import Model.Person;
import Requests.EventIDRequest;
import Requests.PersonIDRequest;
import Results.EventIDResult;
import Results.PersonIDResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class PersonIDServiceTest {

    Database db;

    private PersonIDService service = null;

    private PersonDao pdao = null;
    private UserDao udao = null;
    private EventDao edao = null;
    private AuthTokenDao adao = null;

    private final String USERNAME_SHEILA = "sheila";

    private final Person testPerson = new Person("Betty_White", USERNAME_SHEILA, "Betty", "White", "f",
            "Frank_Jones", "Mrs_Jones", "Blain_McGary");

    @BeforeEach
    void setUp() {
        service = new PersonIDService();
        try {
            db = new Database();
            db.openConnection();
            db.clearTables();
            pdao = new PersonDao(db.getConnection());
            pdao.insert(testPerson);
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
    void personIDPositive() {
        try {
            PersonIDRequest request = new PersonIDRequest(testPerson.getPersonID(), "");
            PersonIDResult result = service.personID(request);

            Assertions.assertEquals(result.getPersonID(), testPerson.getPersonID());
            Assertions.assertEquals(result.getAssociatedUsername(), testPerson.getAssociatedUsername());
            Assertions.assertEquals(result.getFatherID(), testPerson.getFatherID());
            Assertions.assertEquals(result.getMotherID(), testPerson.getMotherID());
            Assertions.assertEquals(result.getSpouseID(), testPerson.getSpouseID());
            Assertions.assertEquals(result.getFirstName(), testPerson.getFirstName());
            Assertions.assertEquals(result.getLastName(), testPerson.getLastName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void personIDNegative() {

        // Search for an event that isn't there
        try {
            PersonIDRequest request = new PersonIDRequest("Fake event ID", "");
            PersonIDResult result = service.personID(request);

            Assertions.assertNull(result);

        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}