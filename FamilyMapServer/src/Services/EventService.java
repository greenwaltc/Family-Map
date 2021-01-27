package Services;

import DataAccess.*;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Requests.EventRequest;
import Results.EventResult;
import Results.PersonResult;

import java.sql.SQLException;
import java.util.ArrayList;

public class EventService {

    public EventService(){}

    /**
     * Returns ALL events for ALL family members of the current user.
     * The current user is determined from the provided auth token.
     *
     * The response body returns a JSON object with a data attribute
     * that contains an array of Event objects.  Each Event object
     * has the same format as described in previous section on the
     * /event/[eventID] API.
     *
     * @param r Event request object
     * @return Event result object
     */
    public EventResult event(EventRequest r) throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();

        EventDao eventDao = new EventDao(database.getConnection());
        AuthTokenDao authTokenDao = new AuthTokenDao(database.getConnection());

        AuthToken token = null;
        ArrayList<Event> events = null;

        try {
            token = authTokenDao.find(r.getAuthToken());
            if (token != null) {
                events = eventDao.findAll(token.getUserName());
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

        return new EventResult(events, "true");
    }
}
