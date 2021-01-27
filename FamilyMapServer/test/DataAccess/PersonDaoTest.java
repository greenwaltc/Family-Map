package DataAccess;

import Model.Event;
import Model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class PersonDaoTest {

    private Database db;
    private PersonDao dao;
    private Person testPerson = new Person("TestPersonID", "TestAssocUsername",
            "TestFirstName", "TestLastName", "m", "TestFatherID",
            "TestMotherID", "TestSpouseID");
    int num_adds = 100;

    @BeforeEach
    void setUp() {
        db = new Database();
        try {
            db.openConnection();
            dao = new PersonDao(db.getConnection());
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
            Assertions.assertDoesNotThrow(() -> dao.insert(testPerson));
            Assertions.assertEquals(testPerson.getFirstName(), dao.find("TestPersonID").getFirstName());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertIndividualNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert( new Person(null, "",
                "", "", "m",
                "", "", "") ));

        testPerson.setGender("not a real gender");
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert(testPerson));
        testPerson.setGender("m"); // Setting it back so further tests aren't messed up
    }

    @Test
    void testInsertGroupPositive1() {
        Assertions.assertDoesNotThrow(() -> dao.insert(createDummies()));
        try {
            Assertions.assertEquals(dao.find(Integer.toString(num_adds - 1)).getFirstName(),
                    Integer.toString(num_adds - 1));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInsertGroupPositive2() {
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
            Assertions.assertEquals(dao.find(Integer.toString(num_adds - 1)).getFirstName(), Integer.toString(num_adds - 1));
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
    void removeUserNamePositive() {
        try {
            insertDummies();
            dao.removeUserName("TestAssocUsername");
            Assertions.assertNull(dao.find(Integer.toString(num_adds - 1)));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeUserNameNegative() {
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
            ArrayList<Person> found = dao.findAll("TestAssocUsername");
            Assertions.assertEquals(num_adds, found.size());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findAllNegative() {
        try {
            insertDummies();
            ArrayList<Person> found = dao.findAll("Not in database");
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
    void clearPositive2() {
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
            dao.insert( new Person(Integer.toString(i), "TestAssocUsername", Integer.toString(i),
                    Integer.toString(i), "m", Integer.toString(i), Integer.toString(i), Integer.toString(i)) );
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