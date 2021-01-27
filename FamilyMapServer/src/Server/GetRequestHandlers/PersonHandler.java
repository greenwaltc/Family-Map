package Server.GetRequestHandlers;

import DataAccess.DataAccessException;
import Requests.PersonRequest;
import Results.PersonResult;
import Services.PersonService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class PersonHandler extends GetRequestHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        try {
            if (!isValidAuthToken()) {
                sendError("error Invalid auth token", exchange);
                return;
            }

            PersonRequest request = new PersonRequest(super.token);
            PersonService service = new PersonService();
            PersonResult result = service.person(request);

            String json = super.gson.toJson(result);
            super.sendSuccess(json, exchange);

        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            sendError("error Internal server error.", exchange);
        }
    }

    @Override
    protected void sendError(String str, HttpExchange exchange) {

        PersonResult result = new PersonResult(str, "true");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }
}
