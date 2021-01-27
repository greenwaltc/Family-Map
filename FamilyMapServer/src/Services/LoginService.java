package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.AuthToken;
import Model.User;
import Requests.LoginRequest;
import Results.LoginResult;

import java.sql.SQLException;
import java.util.UUID;

public class LoginService {

    public LoginService(){}

    public LoginResult login(LoginRequest r) throws SQLException, DataAccessException {

        if (r.getPassword() == null || r.getUserName() == null) {
            throw new DataAccessException("Request property missing.");
        }

        // The user is in the database & credentials were correct
            // Create an auth token
            // put it in the database
            // find the corresponding user
            // create successful response
        Database database = new Database();
        database.openConnection();

        String token = UUID.randomUUID().toString();
        AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
        AuthToken authToken = new AuthToken(r.getUserName(), token);
        authDao.insert(authToken);

        UserDao userDao = new UserDao(database.getConnection());
        User user = userDao.find(r.getUserName());

        database.closeConnection(true); //odo change back when working

        if (user != null) {
            return new LoginResult(token, user.getUserName(), user.getPersonID(), "true");
        }
        else {
            return null;
        }
    }
}
