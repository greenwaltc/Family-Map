package DataAccess;

import Model.Event;

import java.sql.*;
import java.util.ArrayList;

public class EventDao {

    private final Connection conn; //Don't create own connections, use ones given. Repeat this pattern for other Dao classes

    /**
     * Creates event DAO with connection to the database.
     * @param conn Connection to database.
     */
    public EventDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts an event into the database.
     *
     * @param event Event model object to add
     * @throws DataAccessException
     */
    public void insert(Event event) throws DataAccessException {

        String sql = "INSERT INTO Events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setDouble(4, event.getLatitude());
            stmt.setDouble(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database.");
        }
    }

    public void insert(ArrayList<Event> events) throws DataAccessException {

        for (Event e : events) {
            if (e != null) {
                insert(e);
            }
        }
    }

    /**
     * Finds the specified event in the database.
     *
     * @param eventID The primary key to find in the table.
     * @return
     * @throws DataAccessException
     */
    public Event find(String eventID) throws DataAccessException {

        Event event;
        ResultSet rs = null;
        String sql = "Select * FROM Events WHERE EventID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encoutered while finding event");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Removes all events for all people with the associated username
     * @param userName Associated username
     * @throws DataAccessException
     */
    public void removeUserName(String userName) throws DataAccessException {

        String sql = "DELETE FROM Events WHERE AssociatedUsername = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while removing event.");
        }
    }

    public ArrayList<Event> findAll(String username) throws DataAccessException {

        ArrayList<Event> events = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE AssociatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(new Event((String) rs.getObject("EventID"),
                        (String) rs.getObject("AssociatedUsername"),
                        (String) rs.getObject("PersonID"),
                        (double) rs.getObject("Latitude"),
                        (double) rs.getObject("Longitude"),
                        (String) rs.getObject("Country"),
                        (String) rs.getObject("City"),
                        (String) rs.getObject("EventType"),
                        (int) rs.getObject("Year")));
            }

            if (events.size() > 0) {
                return events;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding events table.");
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return events;
    }

    /**
     * Clears the Events table from the database
     */
    public void clear() throws DataAccessException {

        String sql = "DELETE FROM Events";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Events table.");
        }

    }
}
