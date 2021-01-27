package DataAccess;

import java.sql.*;

public class Database {

    final String dbFile = "familymap.db";
    final String connectionURL = "jdbc:sqlite:" + dbFile;

    protected Connection conn;

    /**
     * Loads the database driver.
     *
     * @throws ClassNotFoundException
     */
    public void loadDatabaseDriver() throws ClassNotFoundException {

        final String driver = "org.sqlite.JDBC";
        Class.forName(driver);
    }

    /**
     * Opens the connection to the database.
     *
     * @return The connection object for use later by DAO classes.
     * @throws SQLException
     * @throws DataAccessException
     */
    public Connection openConnection() throws DataAccessException {

        try {
            final String CONNECTION_URL = "jdbc:sqlite:familymap.db";
            conn = DriverManager.getConnection(CONNECTION_URL);
            conn.setAutoCommit(false);
        } catch (SQLException e){
            e.printStackTrace();
            throw new DataAccessException("Unable to open connection to database");
        }

        return conn;
    }

    /**
     * Gets the previously opened connection to the database
     *
     * @return The previously opened connection to the database.
     * @throws DataAccessException
     * @throws SQLException
     */
    public Connection getConnection() throws DataAccessException {
        if (conn == null){
            return openConnection();
        }
        return conn;
    }

    /**
     * Closes the connection if committing is a success. Otherwise, rolls back the changes
     * to the database.
     *
     * @param commit Boolean for manually authorizing changes to database to database.
     * @throws SQLException
     */
    public void closeConnection(boolean commit) throws SQLException{

        if (commit){
            conn.commit();
        }
        else {
            conn.rollback();
        }
        conn = null;
    }

    /**
     * Erases all data and clears all tables in the database.
     *
     * @throws DataAccessException
     */
    public void clearTables() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM AuthorizationTokens";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Events";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Persons";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Users";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }
}
