package DataAccess;

import Model.Person;

import java.sql.*;
import java.util.ArrayList;

public class PersonDao {

    private final Connection conn;

    /**
     * Creates person DAO with connection to the database.
     * @param conn Connection to database.
     */
    public PersonDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new person into the database.
     *
     * @param person Person model object to add.
     * @throws DataAccessException
     */
    public void insert(Person person) throws DataAccessException {

        String sql= "INSERT INTO Persons (PersonID, AssociatedUsername," +
                " FirstName, LastName, Gender, FatherID," +
                " MotherID, SpouseID)" +
                " VALUES(?,?,?,?,?,?,?,?);";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting person into the database.");
        }
    }

    public void insert(ArrayList<Person> persons) throws DataAccessException {

        for (Person person : persons) {
            insert(person);
        }
    }

    /**
     * Finds the person with the specified PersonID.
     *
     * @param PersonID The primary key to find in the table.
     * @return Person model object.1
     * @throws DataAccessException
     */
    public Person find(String PersonID) throws DataAccessException {

        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE PersonID = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, PersonID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"),
                        rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Gender"),
                        rs.getString("FatherID"),
                        rs.getString("MotherID"),
                        rs.getString("SpouseID"));
                return person;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person.");
        }
        finally {

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
     * Removes all people data with the associated username including the user.
     * @param userName Associated username
     */
    public void removeUserName(String userName) throws DataAccessException {

        String sql = "DELETE FROM Persons WHERE AssociatedUsername = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while removing person.");
        }
    }

    public ArrayList<Person> findAll(String username) throws DataAccessException {

        ArrayList<Person> persons = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Persons WHERE AssociatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            rs = stmt.executeQuery();

            while (rs.next()) {
                persons.add(new Person((String) rs.getObject("PersonID"),
                        (String) rs.getObject("AssociatedUsername"),
                        (String) rs.getObject("FirstName"),
                        (String) rs.getObject("LastName"),
                        (String) rs.getObject("Gender"),
                        (String) rs.getObject("FatherID"),
                        (String) rs.getObject("MotherID"),
                        (String) rs.getObject("SpouseID")));
            }

            if (persons.size() > 0) {
                return persons;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding persons table.");
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return persons;
    }

    /**
     * Clears the Persons table from the database
     */
    public void clear() throws DataAccessException {

        String sql = "DELETE FROM Persons";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Persons table.");
        }

    }
}
