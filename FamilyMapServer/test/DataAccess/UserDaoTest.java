package DataAccess;

import Model.Event;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class UserDaoTest {

    private Database db;
    private UserDao dao;
    private User testUser = new User("TestUsername", "TestPassword", "TestEmail",
            "TestFirstName", "TestLastName", "m", "TestPersonID");
    int num_adds = 100;

    @BeforeEach
    void setUp() {
        db = new Database();
        try {
            db.openConnection();
            dao = new UserDao(db.getConnection());
            db.clearTables();
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
    void insertPositive() {
        try {
            Assertions.assertDoesNotThrow(() -> dao.insert(testUser));
            Assertions.assertEquals(testUser.getPassword(), dao.find("TestUsername").getPassword());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert( new User(null, null,
                null, null, null, "m", null)));
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert( new User("", "", "",
                "", "", "m", null)));
    }

    @Test
    void findPositive() {
        try {
            dao.insert(testUser);
            Assertions.assertEquals(testUser.getPassword(), dao.find("TestUsername").getPassword());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findNegative() {
        try {
            Assertions.assertNull(dao.find("Not in database"));
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
            Assertions.assertNull(dao.find(Integer.toString(num_adds - 1))); // Nothing should be returned
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
            Assertions.assertNull(dao.find("Not in database")); // Nothing should be returned
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    void insertDummies() throws DataAccessException {
        for (int i = 1; i <= num_adds; ++i) {
            dao.insert( new User(Integer.toString(i), Integer.toString(i), Integer.toString(i),
                    Integer.toString(i), Integer.toString(i), "m", Integer.toString(i)) );
        }
    }
}