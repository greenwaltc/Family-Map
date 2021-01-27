package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.PersonDao;
import Model.Person;
import Requests.PersonIDRequest;
import Results.PersonIDResult;

import java.sql.SQLException;

public class PersonIDService {

    public PersonIDService(){}

    /**
     * Returns the single Person object with the specified ID.
     *
     * @param r Person ID request with the requested ID
     * @return Person ID result
     */
    public PersonIDResult personID(PersonIDRequest r) throws SQLException, DataAccessException {

        String personID = r.getPersonID();

        Database database = new Database();
        database.openConnection();

        PersonDao dao = new PersonDao(database.getConnection());
        Person person = dao.find(personID);

        database.closeConnection(false);

        if (person != null) {
            return new PersonIDResult(person.getAssociatedUsername(),
                    person.getPersonID(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getGender(),
                    person.getFatherID(),
                    person.getMotherID(),
                    person.getSpouseID(),
                    "true");
        }

        return null;
    }
}
