package Server.GetRequestHandlers;

import DataAccess.*;
import Model.AuthToken;
import Model.Event;
import Requests.EventIDRequest;
import Results.EventIDResult;
import Services.EventIDService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class EventIDHandler extends GetRequestHandler{

    private String eventID;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        super.handle(exchange);
        eventID = uri[2];

        try {

            if (!isValidAuthToken()) {
                sendError("error Invalid auth token", exchange);
                return;
            }
            if (!isValidEventID()) {
                sendError("error Invalid eventID parameter", exchange);
                return;
            }
            if (!isCorrectUser()) {
                sendError("error Requested event does not belong to this user.", exchange);
                return;
            }

            EventIDRequest request = new EventIDRequest(eventID, super.token);
            EventIDService service = new EventIDService();
            EventIDResult result = service.eventID(request);

            String json = super.gson.toJson(result);
            super.sendSuccess(json, exchange);

        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            sendError("Internal server error.", exchange);
        }
    }

    @Override
    protected void sendError(String err, HttpExchange exchange) {

        EventIDResult result = new EventIDResult(err, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }

    private boolean isValidEventID() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();
        EventDao eventDao = new EventDao(database.getConnection());
        Event event = eventDao.find(eventID);
        database.closeConnection(false);

        if (event != null) return true;

        return false;
    }

    public boolean isCorrectUser() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();

        EventDao eventDao = new EventDao(database.getConnection());
        Event event = eventDao.find(eventID);

        AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
        AuthToken token = authDao.find(super.token);

        database.closeConnection(false);

        if (event.getAssociatedUsername().equals(token.getUserName())) {
            return true;
        }

        return false;
    }
}
