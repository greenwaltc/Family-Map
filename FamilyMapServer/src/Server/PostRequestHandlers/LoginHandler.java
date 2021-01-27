package Server.PostRequestHandlers;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.LoginRequest;
import Results.LoginResult;
import Services.LoginService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class LoginHandler extends PostRequestHandler{

    private LoginRequest login;

    @Override
    public void handle(HttpExchange exchange) {

        try {
            super.handle(exchange);
            login = super.gson.fromJson(super.str, LoginRequest.class);

            if (checkEmpty(exchange)) return;
            if (!checkFields(exchange)) return;

            LoginService request = new LoginService();
            LoginResult result = request.login(login);

            String json = super.gson.toJson(result);
            send(json, exchange);
        } catch (IOException | DataAccessException | SQLException e) {
            e.printStackTrace();
            sendError("Internal server error.", exchange);
        }
    }

    private boolean checkEmpty(HttpExchange exchange) {

        if (login.anyNull() || login.anyEmpty()) {
            sendError("Login field missing.", exchange);
            return true;
        }
        return false;
    }

    private boolean checkFields(HttpExchange exchange) throws SQLException, DataAccessException {

        Database database = new Database();
        boolean inDatabase = false;

        try {
            database.openConnection();
            UserDao dao = new UserDao(database.getConnection());
            User user = dao.find(login.getUserName());

            if (user != null &&
            user.getUserName().equals(login.getUserName()) &&
            user.getPassword().equals(login.getPassword())) {
                inDatabase = true;
            }
            database.closeConnection(false);
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (!database.getConnection().isClosed()) {
                database.closeConnection(false);
            }
        }
        if (!inDatabase) {
            sendBadLogin("error Username or password is incorrect or not found.", exchange);
        }
        return inDatabase;
    }

    @Override
    protected void sendError(String errorStr, HttpExchange exchange) {
        LoginResult result = new LoginResult(errorStr, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }

    private void sendBadLogin(String errorStr, HttpExchange exchange) {

        LoginResult result = new LoginResult(errorStr, "false");
        String json = super.gson.toJson(result);

        OutputStream respBody = exchange.getResponseBody();
        try {
            exchange.sendResponseHeaders(400, 0);
            writeString(json, respBody);
            respBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
