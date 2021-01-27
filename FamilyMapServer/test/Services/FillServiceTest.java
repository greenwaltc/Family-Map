package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.FillRequest;
import Results.FillResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class FillServiceTest {

    private FillRequest request = null;
    private FillService service = null;
    private FillResult result = null;

    private User testUser = new User("TestUsername", "TestPassword", "TestEmail", "TestFirstName",
            "TestLastName", "m", "TestPersonID");

    @BeforeEach
    void setUp() {
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

        request = new FillRequest(testUser.getUserName());
        service = new FillService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fillPositive() {

        // Tests a standard fill of 4 generations

        String testStr = "Successfully added 31 persons and 91 events to the database.";

        try {
            result = service.fill(request);
            Assertions.assertEquals(result.getMessage(), testStr);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void fillLargePositive() {

        // Tests a larger fill of 20 generations

        request.setGenerations(10);

        String testStr = "Successfully added 2047 persons and 6139 events to the database.";

        try {
            result = service.fill(request);
            Assertions.assertEquals(result.getMessage(), testStr);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}