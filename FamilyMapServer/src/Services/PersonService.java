package Services;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.PersonDao;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Requests.PersonRequest;
import Results.PersonResult;

import java.sql.SQLException;
import java.util.ArrayList;

public class PersonService {

    public PersonService(){}

    /**
     * Returns ALL family members of the current user. The current user is
     * determined from the provided auth token.
     *
     * The response body returns a JSON object with a data attribute that
     * contains an array of Person objects.  Each Person object has the same
     * format as described in previous section on the /person/[personID] API.
     *
     * @param r Person request object
     * @return Person result
     */
    public PersonResult person(PersonRequest r) throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();

        PersonDao personDao = new PersonDao(database.getConnection());
        AuthTokenDao authTokenDao = new AuthTokenDao(database.getConnection());

        AuthToken token = null;
        ArrayList<Person> persons = null;

        try {
            token = authTokenDao.find(r.getAuthToken());
            if (token != null) {
                persons = personDao.findAll(token.getUserName());
            }
            else {
                database.closeConnection(false);
                return null;
            }
        } catch (DataAccessException e) {
            database.closeConnection(false);
            throw e;
        }

        database.closeConnection(false);

        return new PersonResult(persons, "true");
    }
}
