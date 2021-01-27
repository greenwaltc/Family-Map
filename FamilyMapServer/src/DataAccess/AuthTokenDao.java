package DataAccess;

import Model.AuthToken;

import java.sql.*;

public class AuthTokenDao {

    private final Connection conn;

    /**
     * Creates authorization token DAO with connection to the database.
     * @param conn Connection to database.
     */
    public AuthTokenDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new Authorization Token model object into the database.
     *
     * @param authToken Model object authorization token to add to database
     */
    public void insert(AuthToken authToken) throws DataAccessException {

        String sql= "INSERT INTO AuthorizationTokens (UserName, Token) VALUES(?,?);";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, authToken.getUserName());
            stmt.setString(2, authToken.getToken());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting auth token into the database.");
        }
    }

    /**
     * Finds the AuthToken model object corresponding to the provided
     * token.
     *
     * @param token The primary key to find in the table.
     * @return The found AuthToken model object.
     * @throws DataAccessException
     */
    public AuthToken find(String token) throws DataAccessException {

        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthorizationTokens WHERE Token = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("UserName"), rs.getString("Token"));
                return authToken;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authorization token.");
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
     * Removes an Authorization Token from the Table
     *
     * @param token Authorization Token to be removed
     * @throws DataAccessException
     */
    public void remove(String token) throws DataAccessException{

        String sql = "DELETE FROM AuthorizationTokens WHERE Token = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
             stmt.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
            throw new DataAccessException("Error encountered while removing authorization token.");
        }
    }


    /**
     * Clears the AuthorizationToken table from the database
     */
    public void clear() throws DataAccessException {

        String sql = "DELETE FROM AuthorizationTokens";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing AuthorizationTokens table.");
        }
    }
}
