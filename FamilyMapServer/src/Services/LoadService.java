package Services;

import DataAccess.*;
import Model.*;
import Requests.LoadRequest;
import Results.LoadResult;

import java.sql.SQLException;
import java.util.ArrayList;

public class LoadService {

    public LoadService(){}

    /**
     * Clears all data from the database (just like the /clear API),
     * and then loads the posted user, person, and event data into
     * the database.
     *
     * @param r Load request with user, person, and event data
     * @return Load result
     */
    public LoadResult load(LoadRequest r) throws DataAccessException, SQLException {

        if (r == null) throw new DataAccessException("Load request (JSON) was empty.");

        ArrayList<User> users = r.getUsers();
        ArrayList<Person> people = r.getPersons();
        ArrayList<Event> events = r.getEvents();

        Database database = new Database();

        try {
            database.openConnection();
            database.clearTables();

            AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
            UserDao userDao = new UserDao(database.getConnection());
            PersonDao personDao = new PersonDao(database.getConnection());
            EventDao eventDao = new EventDao(database.getConnection());

            if (users != null && people != null && events != null) {
                for (User u : users) {
                    userDao.insert(u);
                }
                for (Person p : people) {
                    personDao.insert(p);
                }
                for (Event e : events) {
                    eventDao.insert(e);
                }

                database.closeConnection(true);

                return new LoadResult("Successfully added " +
                        users.size() +
                        " users, " +
                        people.size() +
                        " persons, and " +
                        events.size() +
                        " events to the database", "true");
            }

            database.closeConnection(true);

            String temp = "Successfully added 0 users, 0 persons, and 0 events to the database";
            return new LoadResult(temp, "true");

        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            throw e;
        }
    }
}
