package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.RegisterRequest;
import Results.RegisterResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class RegisterServiceTest {

    RegisterRequest request = null;
    RegisterService service = null;
    RegisterResult result = null;

    @BeforeEach
    void setUp() {
        try {
            Database db = new Database();
            db.openConnection();
            db.clearTables();
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
    void registerPositive() {

        // A successful add with no previous user by the same name

        request = new RegisterRequest("TestUsername", "TestPassword", "TestEmail",
                "TestFirstName", "TestLastName", "m");
        service = new RegisterService();

        try {
            result = service.register(request);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Assertions.assertTrue(result.getAuthToken() != null);
        Assertions.assertNull(result.getMessage());
        Assertions.assertEquals(result.getUserName(), request.getUserName());
        Assertions.assertNotNull(result.getAuthToken());
    }

    @Test
    void registerNegative() {

        service = new RegisterService();

        // Put a user with existing username in there to test originality
        try {
            Database db = new Database();
            db.openConnection();
            UserDao udao = new UserDao(db.getConnection());
            udao.insert( new User("TestUsername", "TestPassword", "TestEMail", "TestFirstName",
                    "TestLastName", "m", "TestPersonID"));
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Test that registering a new user with the same username throws an error
        request = new RegisterRequest("TestUsername", "TestPassword", "TestEmail",
                "TestFirstName", "TestLastName", "m");
        Assertions.assertThrows(DataAccessException.class, () -> service.register(request));

        // Test that registering with invalid gender throws an error. Username is now unique
        request = new RegisterRequest("NewUsername", "TestPassword", "TestEmail",
                "TestFirstName", "TestLastName", "not a real gender");
        Assertions.assertThrows(DataAccessException.class, () -> service.register(request));

        // Test that registering with any values as null throws an error
        request = new RegisterRequest("NewUsername", null, "TestEmail",
                "TestFirstName", "TestLastName", "not a real gender");
        Assertions.assertThrows(DataAccessException.class, () -> service.register(request));
    }
}