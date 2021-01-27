package DataAccess;

import java.sql.*;

import Model.User;

public class UserDao {

    private final Connection conn;

    /**
     * Creates user DAO with connection to the database.
     * @param conn Connection to database.
     */
    public UserDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user User model object to add.
     * @throws DataAccessException
     */
    public void insert(User user) throws DataAccessException {

        String sql= "INSERT INTO Users (UserName, Password, Email, \"First Name\", \"Last Name\", Gender, \"Person ID\")" +
                " VALUES(?,?,?,?,?,?,?);";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting user into the database.");
        }
    }

    /**
     * Finds the user with the specified UserName.
     *
     * @param userName The primary key to find in the table.
     * @return User model object.
     * @throws DataAccessException
     */
    public User find(String userName) throws DataAccessException {

        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM Users WHERE UserName = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("First Name"),
                        rs.getString("Last Name"),
                        rs.getString("Gender"),
                        rs.getString("Person ID"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding user.");
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
     * Clears the Users table from the database
     */
    public void clear() throws DataAccessException {

        String sql = "DELETE FROM Users";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing Users table.");
        }

    }
}