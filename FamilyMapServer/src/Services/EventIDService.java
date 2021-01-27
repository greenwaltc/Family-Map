package Services;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.EventDao;
import Model.Event;
import Model.Person;
import Requests.EventIDRequest;
import Results.EventIDResult;
import Results.PersonIDResult;

import java.sql.SQLException;

public class EventIDService {

    public EventIDService(){}

    /**
     * Returns the single Event object with the specified ID.
     *
     * @param r Event ID request
     * @return Event ID result
     */
    public EventIDResult eventID(EventIDRequest r) throws SQLException, DataAccessException {

        String eventID = r.getEventID();

        Database database = new Database();
        database.openConnection();

        EventDao dao = new EventDao(database.getConnection());
        Event event = dao.find(eventID);

        database.closeConnection(false);

        Double latitude = null;
        Double longitude = null;

        if (event != null) {
            latitude = Math.round(event.getLatitude() * 10000.0) / 10000.0;
            longitude = Math.round(event.getLongitude() * 10000.0) / 10000.0;

            return new EventIDResult(event.getAssociatedUsername(),
                    event.getEventID(),
                    event.getPersonID(),
                    latitude,
                    longitude,
                    event.getCountry(),
                    event.getCity(),
                    event.getEventType(),
                    event.getYear(),
                    "true");
        }

//        return new EventIDResult(event.getAssociatedUsername(),
//                event.getEventID(),
//                event.getPersonID(),
//                latitude,
//                longitude,
//                event.getCountry(),
//                event.getCity(),
//                event.getEventType(),
//                event.getYear(),
//                "true");
        return null;
    }
}
