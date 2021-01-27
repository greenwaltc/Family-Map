package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
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


import DataAccess.*;

class LoadServiceTest {

    private LoadRequest load_good = null;
    private LoadRequest load_missingData = null;
    private LoadRequest load_missingUser = null;
    private LoadRequest load_empty = null;

    private LoadService service = null;

    private final String USERNAME_SHEILA = "sheila";
    private final String USERNAME_PATRICK = "patrick";
    private final String PASSWORD_SHEILA = "parker";
    private final String PASSWORD_PATRICK = "spencer";
    private final int NUM_PEOPLE_SHEILA = 8;
    private final int NUM_PEOPLE_PATRICK = 3;
    private final int NUM_EVENTS_SHEILA = 16;
    private final int NUM_EVENTS_PATRICK = 3;

    @BeforeEach
    void setUp() {
        try {
            Gson gson = new Gson();

            String content = Files.readString(Path.of("./json/LoadData.json"), StandardCharsets.US_ASCII);
            load_good = gson.fromJson(content, LoadRequest.class);

            content = Files.readString(Path.of("./json/NullData.json"), StandardCharsets.US_ASCII);
            load_missingData = gson.fromJson(content, LoadRequest.class);

            content = Files.readString(Path.of("./json/MissingUser.json"), StandardCharsets.US_ASCII);
            load_missingUser = gson.fromJson(content, LoadRequest.class);

            content = Files.readString(Path.of("./json/Empty.json"), StandardCharsets.US_ASCII);
            load_empty = gson.fromJson(content, LoadRequest.class);

            service = new LoadService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loadPositive() {
        // Load the data into the database, then check if everything was loaded correctly
        Assertions.assertNotNull(load_good);
        Assertions.assertDoesNotThrow(() -> service.load(load_good));

        try {
            Database database = new Database();
            database.openConnection();

            PersonDao pdao = new PersonDao(database.getConnection());
            UserDao udao = new UserDao(database.getConnection());
            EventDao edao = new EventDao(database.getConnection());

            Assertions.assertEquals(pdao.findAll(USERNAME_SHEILA).size(), NUM_PEOPLE_SHEILA);
            Assertions.assertEquals(pdao.findAll(USERNAME_PATRICK).size(), NUM_PEOPLE_PATRICK);
            Assertions.assertEquals(edao.findAll(USERNAME_SHEILA).size(), NUM_EVENTS_SHEILA);
            Assertions.assertEquals(edao.findAll(USERNAME_PATRICK).size(), NUM_EVENTS_PATRICK);
            Assertions.assertEquals(udao.find(USERNAME_SHEILA).getPassword(), PASSWORD_SHEILA);
            Assertions.assertEquals(udao.find(USERNAME_PATRICK).getPassword(), PASSWORD_PATRICK);

            database.closeConnection(false);
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void loadNegative() {

        Assertions.assertThrows(DataAccessException.class, () -> service.load(load_missingData));
//        Assertions.assertThrows(DataAccessException.class, () -> service.load(load_missingUser));
        Assertions.assertThrows(DataAccessException.class, () -> service.load(load_empty));
    }
}