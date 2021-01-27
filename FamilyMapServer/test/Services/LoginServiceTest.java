package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.LoginRequest;
import Results.LoginResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class LoginServiceTest {

    LoginRequest request = null;
    LoginService service = null;
    LoginResult result  = null;

    User testUser = null;

    @BeforeEach
    void setUp() {
        testUser = new User("TestUsername", "TestPassword", "TestEmail", "TestFirstName",
                "TestLastName", "m", "TestPersonID");

        try {
            Database db = new Database();
            db.openConnection();
            db.clearTables();

            UserDao udao = new UserDao(db.getConnection());
            udao.insert(testUser);

            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        service = new LoginService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loginPositive() {

        // Test a successful login with a user that exists in the database
        request = new LoginRequest(testUser.getUserName(), testUser.getPassword());

        try {
            result = service.login(request);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Assertions.assertEquals(result.getUserName(), testUser.getUserName());
        Assertions.assertNull(result.getMessage());
        Assertions.assertNotNull(result.getAuthToken());
        Assertions.assertNotNull(result.getAuthToken());

        // Check the auth token table to see if a new one was added correctly
        try {
            Database db = new Database();
            db.openConnection();

            AuthTokenDao adao = new AuthTokenDao(db.getConnection());
            Assertions.assertNotNull(adao.find(result.getAuthToken()));

            db.closeConnection(false);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void loginNegative() {

        // Try a login with a user that does not exist in the database
        request = new LoginRequest("Fake Username", "Fake Password");

        try {
            Assertions.assertNull(service.login(request));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        // Try a login with missing information
        request = new LoginRequest("Fake Username", null);
        Assertions.assertThrows(DataAccessException.class, () -> service.login(request));
    }
}