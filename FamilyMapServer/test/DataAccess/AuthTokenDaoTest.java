package DataAccess;

import Model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class AuthTokenDaoTest {

    private Database db;
    private AuthTokenDao dao;
    private AuthToken testToken = new AuthToken("TestUser", "ABCD1234");
    int num_adds = 100;

    @BeforeEach
    void setUp() {
        db = new Database();
        try {
            db.openConnection();
            dao = new AuthTokenDao(db.getConnection());
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
            dao.insert(testToken);
            Assertions.assertEquals(dao.find("ABCD1234").getUserName(), testToken.getUserName());
            Assertions.assertEquals(dao.find("ABCD1234").getToken(), testToken.getToken());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertNegative() {
        // No value in the token can be null
        Assertions.assertThrows(DataAccessException.class, () -> dao.insert(new AuthToken(null, null)));
        try {
            dao.insert(testToken);
            Assertions.assertThrows(DataAccessException.class, () -> dao.insert(testToken)); // Can't insert same tok twice
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findPositive() {
        try {
            Assertions.assertNull(dao.find(testToken.getToken())); // Not part of the positive test case, just making
                                                                        // sure it's not in there already
            dao.insert(testToken);

            Assertions.assertDoesNotThrow(() -> dao.find(testToken.getToken()));

            AuthToken found = dao.find(testToken.getToken());

            Assertions.assertEquals(found.getToken(), testToken.getToken());
            Assertions.assertEquals(found.getUserName(), testToken.getUserName());

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findNegative() {
        try {
            Assertions.assertNull(dao.find(testToken.getToken()));
            Assertions.assertNull(dao.find(""));
            Assertions.assertNull(dao.find(null));

            dao.insert(testToken);

            Assertions.assertNull(dao.find("not in database"));
            Assertions.assertNull(dao.find(""));
            Assertions.assertNull(dao.find(null));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removePositive() {
        try {
            dao.insert(testToken);
            dao.remove(testToken.getToken());
            Assertions.assertNull(dao.find(testToken.getToken()));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeNegative() {
        try {
            dao.insert(testToken);
            dao.remove("Wrong token");
            Assertions.assertNotNull(dao.find(testToken.getToken()));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void clearPositive1() {
        try {
            dao.insert(testToken);
            dao.clear();
            Assertions.assertNull(dao.find(testToken.getToken()));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void clearNegative2() {
        try {
            for (int i = 1; i <= num_adds; ++i) {
                dao.insert( new AuthToken(Integer.toString(i),Integer.toString(i)) );
            }

            Assertions.assertNotNull(dao.find(Integer.toString(num_adds - 1)));

            dao.clear();

            Assertions.assertNull(dao.find(Integer.toString(num_adds - 1)));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}