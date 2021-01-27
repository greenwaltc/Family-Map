package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.FillRequest;
import Results.FillResult;
import Services.Fill.Generations;

import java.sql.SQLException;

public class FillService {

    public FillService(){}

    /**
     * Populates the server's database with generated data for the specified user name.
     * The required "username" parameter must be a user already registered with the server.
     * If there is any data in the database already associated with the given user name, it is deleted.
     * The optional generations parameter lets the caller specify the number of generations of
     * ancestors to be generated, and must be a non-negative integer (the default is 4, which results
     * in 31 new persons each with associated events).
     *
     * @param r Services.Fill request with necessary data
     * @return Services.Fill result with necessary response data
     */
    public FillResult fill(FillRequest r) throws SQLException, DataAccessException {

        // Get the user from database
        Database database = new Database();
        database.openConnection();
        UserDao userDao = new UserDao(database.getConnection());
        User user = userDao.find(r.getUserName());
        database.closeConnection(false);

        if (user == null) {
            throw new DataAccessException("Error finding user... for some reason.");
        }

        // Create generations object w/ num generations
        Generations gens = new Generations(user, r.getGenerations());

        // Call fill
        gens.fill();

        // Create result
        return new FillResult("Successfully added " +
                gens.getNUM_PEOPLE() +
                " persons and " +
                gens.getNUM_EVENTS() +
                " events to the database.", "true");
    }
}