package Server.PostRequestHandlers;

import DataAccess.DataAccessException;
import Requests.ClearRequest;
import Results.ClearResult;
import Results.RegisterResult;
import Services.ClearService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class ClearHandler extends PostRequestHandler{

    private ClearRequest clear;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);

        clear = super.gson.fromJson(super.str, ClearRequest.class);
        ClearService request = new ClearService();
        ClearResult result = null;

        try {
            result = request.clear();
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            sendError("error Internal server error.", exchange);
        }

        String json = super.gson.toJson(result);
        send(json, exchange);
    }

    @Override
    protected void sendError(String errorStr, HttpExchange exchange) {

        ClearResult result = new ClearResult(errorStr, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }
}
