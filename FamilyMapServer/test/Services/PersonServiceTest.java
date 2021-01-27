package Services;

import DataAccess.*;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Requests.EventRequest;
import Requests.PersonRequest;
import Results.EventResult;
import Results.PersonResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class PersonServiceTest {

    PersonRequest request;
    PersonService service;
    PersonResult result;

    private Database db;
    private PersonDao pdao;
    private AuthTokenDao adao;
    //    private Event testEvent = new Event("TestEventID", "TestAssocUsername",
//            "TestPersonID", 0.0, 0.0, "TestCountry", "TestCity",
//            "TestEventType", 3000);
    int num_adds = 100;
    ArrayList<Person> persons = null;

    @BeforeEach
    void setUp() {
        try {
            db = new Database();
            db.openConnection();
            db.clearTables();

            pdao = new PersonDao(db.getConnection());
            persons = createDummies();
            pdao.insert(persons);

            adao = new AuthTokenDao(db.getConnection());
            adao.insert( new AuthToken("TestAssocUsername", "TestAuthToken"));

            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        request = new PersonRequest("TestAuthToken");
        service = new PersonService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void personPositive() {
        try {
            result = service.person(request);

            Assertions.assertEquals(result.getData().size(), persons.size());

            for (int i = 0; i < num_adds; ++i) {
                Assertions.assertEquals(result.getData().get(i).getAssociatedUsername(), persons.get(i).getAssociatedUsername());
                Assertions.assertEquals(result.getData().get(i).getPersonID(), persons.get(i).getPersonID());
                Assertions.assertEquals(result.getData().get(i).getFirstName(), persons.get(i).getFirstName());
                Assertions.assertEquals(result.getData().get(i).getLastName(), persons.get(i).getLastName());
                Assertions.assertEquals(result.getData().get(i).getFatherID(), persons.get(i).getFatherID());
                Assertions.assertEquals(result.getData().get(i).getMotherID(), persons.get(i).getMotherID());
                Assertions.assertEquals(result.getData().get(i).getGender(), persons.get(i).getGender());
                Assertions.assertEquals(result.getData().get(i).getSpouseID(), persons.get(i).getSpouseID());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void personNegative() {

        // Try to find persons using an auth token that doesn't exist
        request = new PersonRequest("Fake auth token");
        try {
            Assertions.assertNull(service.person(request));
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

        // Try to find the persons with a PREVIOUSLY EXISTING (now not) auth token
        request = new PersonRequest("TestAuthToken");
        try {
            result = service.person(request);
            Assertions.assertNull(result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Person> createDummies() {

        ArrayList<Person> persons = new ArrayList<>();

        for (int i = 1; i <= num_adds; ++i) {
            persons.add( new Person(Integer.toString(i), "TestAssocUsername", Integer.toString(i),
                    Integer.toString(i), "m", Integer.toString(i), Integer.toString(i), Integer.toString(i)) );
        }

        return persons;
    }
}