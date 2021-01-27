package Server.GetRequestHandlers;

import DataAccess.DataAccessException;
import Requests.EventRequest;
import Results.EventResult;
import Services.EventService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class EventHandler extends GetRequestHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        try {
            if (!isValidAuthToken()) {
                sendError("error Invalid auth token", exchange);
                return;
            }

            EventRequest request = new EventRequest(super.token);
            EventService service = new EventService();
            EventResult result = service.event(request);

            String json = super.gson.toJson(result);
            super.sendSuccess(json, exchange);

        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            sendError("Internal server error.", exchange);
        }
    }

    @Override
    protected void sendError(String str, HttpExchange exchange) {

        EventResult result = new EventResult(str, "true");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }
}
