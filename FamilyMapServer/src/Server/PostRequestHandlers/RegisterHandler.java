package Server.PostRequestHandlers;

import DataAccess.*;
import Requests.RegisterRequest;
import Results.RegisterResult;
import Services.RegisterService;
import com.sun.net.httpserver.HttpExchange;

import java.sql.SQLException;

import java.io.IOException;

public class RegisterHandler extends PostRequestHandler {

    private RegisterRequest newUser;

    @Override
    public void handle(HttpExchange exchange) {

        try {
            super.handle(exchange);

            newUser = super.gson.fromJson(super.str, RegisterRequest.class);

            if( checkEmpty(exchange) ) return;
            if( checkGender(exchange) ) return;
            if( checkUserName(exchange) ) return;

            RegisterService request = new RegisterService();
            RegisterResult result = request.register(newUser);

            String json = super.gson.toJson(result);
            send(json, exchange);
            return;
        } catch (DataAccessException | IOException | SQLException e) {
            e.printStackTrace();
            sendError("error Internal server error.", exchange);
        }
    }

    private boolean checkEmpty(HttpExchange exchange) {

        if (newUser.anyNull() || newUser.anyEmpty()) {
            sendError("error Register field missing", exchange);
            return true;
        }
        return false;
    }

    private boolean checkGender(HttpExchange exchange) {

        if ( !(newUser.getGender().equals("m") ||
                newUser.getGender().equals("f")) ) {
            sendError("error Gender must be m or f", exchange);
            return true;
        }
        return false;
    }

    private boolean checkUserName(HttpExchange exchange) throws SQLException, DataAccessException {

        Database database = new Database();
        boolean inDatabase = false;

        try {
            database.openConnection();
            UserDao dao = new UserDao(database.getConnection());

            if (dao.find(newUser.getUserName()) != null) { // Username was not already found in database.
                inDatabase = true;
            }
            database.closeConnection(false); // Username was already found in database
        } catch (SQLException | DataAccessException throwables) {
            throwables.printStackTrace();
            throw throwables;
        }

        if (inDatabase) {
            sendError("error Username already exists.", exchange);
            return true;
        }

        return false;
    }

    @Override
    protected void sendError(String errorStr, HttpExchange exchange) {

        RegisterResult result = new RegisterResult(errorStr, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }
}
