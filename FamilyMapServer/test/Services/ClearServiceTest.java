package Services;

import DataAccess.*;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import Requests.LoadRequest;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

class ClearServiceTest {

    Database db;

    private LoadRequest load_good = null;
    private LoadService loadService = null;

    private PersonDao pdao = null;
    private UserDao udao = null;
    private EventDao edao = null;
    private AuthTokenDao adao = null;

    private final String USERNAME_SHEILA = "sheila";
    private final String USERNAME_PATRICK = "patrick";

    @BeforeEach
    void setUp() {
        Gson gson = new Gson();
        String content = null;
        loadService = new LoadService();

        try {
            content = Files.readString(Path.of("./json/LoadData.json"), StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }

        load_good = gson.fromJson(content, LoadRequest.class);

        try {
            loadService.load(load_good);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            db = new Database();
            db.openConnection();
            pdao = new PersonDao(db.getConnection());
            udao = new UserDao(db.getConnection());
            edao = new EventDao(db.getConnection());
            adao = new AuthTokenDao(db.getConnection());
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
    void clearPositive1() {

        try {
            // Load doesn't add anything to the auth tokens, so add one / remove one manually
            adao.insert( new AuthToken("TestUsername", "TestToken"));

            // We already know the load works, so that test doesn't need to be redone
            db.clearTables();
            Assertions.assertNull(pdao.find(USERNAME_SHEILA));
            Assertions.assertNull(pdao.find(USERNAME_PATRICK));
            Assertions.assertNull(udao.find(USERNAME_SHEILA));
            Assertions.assertNull(udao.find(USERNAME_PATRICK));
            Assertions.assertNull(edao.find(USERNAME_SHEILA));
            Assertions.assertNull(edao.find(USERNAME_PATRICK));

            Assertions.assertNull(adao.find("TestToken"));

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void clearPositive2() {

        try {
            adao.insert( new AuthToken("TestUsername", "TestToken"));
            pdao.insert( new Person("TestPersonID", "TestUsername", "TestFirstName",
                    "TestLastName", "m", "TestFatherID", "TestMotherID",
                    "TestSpouseID"));
            udao.insert( new User("TestUsername", "TestPassword", "TestEmail",
                    "TestFirstName", "TestLastName", "m", "TestPersonID"));
            edao.insert( new Event("TestEventID", "TestUsername", "TestPersonID",
                    0.0, 0.0, "TestCountry", "TestCity", "TestEventType", 0));

            db.clearTables();

            Assertions.assertNull(adao.find("TestToken"));
            Assertions.assertNull(pdao.find("TestPersonID"));
            Assertions.assertNull(udao.find("TestUsername"));
            Assertions.assertNull(edao.find("TestEventID"));

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}