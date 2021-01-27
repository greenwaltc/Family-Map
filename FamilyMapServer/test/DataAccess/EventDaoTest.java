package DataAccess;

import Model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;


class EventDaoTest {

    private Database db;
    private EventDao dao;
    private Event testEvent = new Event("TestEventID", "TestAssocUsername",
            "TestPersonID", 0.0, 0.0, "TestCountry", "TestCity",
            "TestEventType", 3000);
    int num_adds = 100;

    @BeforeEach
    void setUp() {
        db = new Database();
        try {
            db.openConnection();
            dao = new EventDao(db.getConnection());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            db.closeConnection(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db = null;
    }

    @Test
    void insertIndividualPositive() {
        try {
            Assertions.assertDoesNotThrow(() -> dao.insert(testEvent));
            Assertions.assertEquals(testEvent.getPersonID(), dao.find(testEvent.getEventID()).getPersonID());
            Assertions.assertEquals(testEvent.getAssociatedUsername(), dao.find(testEvent.getEventID()).getAssociatedUsername());
            Assertions.assertEquals(testEvent.getLatitude(), dao.find(testEvent.getEventID()).getLatitude());
            Assertions.assertEquals(testEvent.getLongitude(), dao.find(testEvent.getEventID()).getLongitude());
            Assertions.assertEquals(testEvent.getCountry(), dao.find(testEvent.getEventID()).getCountry());
            Assertions.assertEquals(testEvent.getCity(), dao.find(testEvent.getEventID()).getCity());
            Assertions.assertEquals(testEvent.getEventType(), dao.find(testEvent.getEventID()).getEventType());
            Assertions.assertEquals(testEvent.getYear(), dao.find(testEvent.getEventID()).getYear());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertIndividualNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert( new Event(null,null,
                null,0.0,0.0,
                null,null,null, 0)));
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert( new Event("","",
                "",0.0,0.0,
                "","",null, 0)));
    }

    @Test
    void testInsertGroupPositive1() {
        Assertions.assertDoesNotThrow(() -> dao.insert(createDummies()));
        try {
            Assertions.assertEquals(dao.find(Integer.toString(num_adds - 1)).getEventID(),
                    Integer.toString(num_adds - 1));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertGroupPositive2() {
        // Testing inserting an empty array list. Should not throw an exception, but nothing should be added
        Assertions.assertDoesNotThrow(() -> dao.insert(new ArrayList<>()));
        try {
            // After inserting an empty array, nothing should be found
            Assertions.assertNull(dao.find(Integer.toString(num_adds - 1)));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findPositive() {
        try {
            insertDummies();
            Assertions.assertNotNull(dao.find(Integer.toString(num_adds - 1)));
            Assertions.assertEquals(dao.find(Integer.toString(num_adds - 1)).getEventID(), Integer.toString(num_adds - 1));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findNegative() {
        try {
            insertDummies();
            Assertions.assertNull(dao.find("Not in database"));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeUserNamePositive1() {
        try {
            insertDummies();
            dao.removeUserName("TestAssocUsername");
            Assertions.assertNull(dao.find(Integer.toString(num_adds - 1)));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeUserNamePositive2() {
        try {
            insertDummies();
            dao.removeUserName("Not in database");
            Assertions.assertNotNull(dao.find(Integer.toString(num_adds - 1)));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findAllPositive() {
        try {
            insertDummies();
            ArrayList<Event> found = dao.findAll("TestAssocUsername");
            Assertions.assertEquals(num_adds, found.size());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findAllNegative() {
        try {
            insertDummies();
            ArrayList<Event> found = dao.findAll("Not in database");
            Assertions.assertNotEquals(num_adds, found.size());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void clearPositive1() {
        try {
            int temp = num_adds;
            num_adds = 1000;
            insertDummies();
            num_adds = temp;

            dao.clear();
            Assertions.assertEquals(dao.findAll("TestAssocUsername").size(), 0); // Nothing should be returned
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void clearNegative2() {
        try {
            int temp = num_adds;
            num_adds = 1000;
            insertDummies();
            num_adds = temp;

            dao.clear();
            Assertions.assertEquals(dao.findAll("Not in database").size(), 0); // Nothing should be returned
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    void insertDummies() throws DataAccessException {
        for (int i = 1; i <= num_adds; ++i) {
            dao.insert( new Event(Integer.toString(i), "TestAssocUsername", Integer.toString(i),
                    0.0, 0.0, Integer.toString(i), Integer.toString(i), Integer.toString(i), 0) );
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