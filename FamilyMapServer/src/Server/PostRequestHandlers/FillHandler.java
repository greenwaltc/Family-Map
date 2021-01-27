package Server.PostRequestHandlers;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.User;
import Requests.FillRequest;
import Results.FillResult;
import Results.RegisterResult;
import Services.FillService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class FillHandler extends PostRequestHandler{

    FillRequest fill;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        super.handle(exchange);

        String [] url = exchange.getRequestURI().toString().split("/");
        String username = null;
        int generations = 0;

        if (url.length == 3) {
            // Do default fill
            username = url[2];
            generations = 4;
        }
        else if (url.length == 4) {
            // Do fill w/ specified generations
            username = url[2];
            generations = Integer.parseInt(url[3]);
        }

        try {
            // Check that username is in database
            if (!validUsername(username)) {
                sendError("Invalid username or generations parameter", exchange);
                return;
            }

            // Create fill request object
            FillRequest request = new FillRequest(username, generations);

            // Pass to fill service and perform operations
            FillService service = new FillService();
            FillResult result = service.fill(request);

            // Send response to server
            String json = super.gson.toJson(result);
            send(json, exchange);
            return;
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            sendError("Internal server error.", exchange);
            return; // Maybe get rid of?
        }

    }

    @Override
    protected void sendError(String errorStr, HttpExchange exchange) {

        FillResult result = new FillResult(errorStr, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }

    private boolean validUsername(String username) throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();
        UserDao userDao = new UserDao(database.getConnection());
        User user = userDao.find(username);
        database.closeConnection(false);

        if (user != null) return true;

        return false;
    }
}
