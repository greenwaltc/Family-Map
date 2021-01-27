package Services;

import DataAccess.*;
import Results.ClearResult;

import java.sql.SQLException;

public class ClearService {

    public ClearService(){}

    public ClearResult clear() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();

        AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
        authDao.clear();
        EventDao eventDao = new EventDao(database.getConnection());
        eventDao.clear();
        PersonDao personDao = new PersonDao(database.getConnection());
        personDao.clear();
        UserDao userDao = new UserDao(database.getConnection());
        userDao.clear();

        database.closeConnection(true);

        return new ClearResult("Clear succeeded.", "true");
    }
}
