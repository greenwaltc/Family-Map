package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.AuthToken;
import Model.User;
import Requests.RegisterRequest;
import Results.RegisterResult;
import Services.Fill.Generations;

import java.sql.SQLException;
import java.util.UUID;

public class RegisterService {

    public RegisterService(){}

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new user,
     * logs the user in, and returns an auth token.
     *
     * @param r Register request with needed information
     * @return
     */
    public RegisterResult register(RegisterRequest r) throws DataAccessException, SQLException {

        User newUser = generateNewUser(r);

        try {

            Database database = new Database();
            database.openConnection();
            UserDao toAdd = new UserDao(database.getConnection());
            toAdd.insert(newUser);

            AuthToken token = createAuthToken(newUser);
            AuthTokenDao tokenDao = new AuthTokenDao(database.getConnection());
            tokenDao.insert(token);

            database.closeConnection(true); // odo: change when working

            Generations generations = new Generations(newUser); // Register generates 4 generations by default
            generations.fill();

            RegisterResult result = createResult(token, newUser);

            return result;
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private User generateNewUser(RegisterRequest r){

        String personID = UUID.randomUUID().toString();
        User newUser = new User(r.getUserName(),
                r.getPassword(),
                r.getEmail(),
                r.getFirstName(),
                r.getLastName(),
                r.getGender(),
                personID);

        return newUser;
    }

    private AuthToken createAuthToken(User user) {

        String authToken = UUID.randomUUID().toString();
        AuthToken token = new AuthToken(user.getUserName(), authToken);
        return token;
    }

    private RegisterResult createResult(AuthToken t, User u) {

        return new RegisterResult(t.getToken(), u.getUserName(), u.getPersonID(), "true");
    }
}
