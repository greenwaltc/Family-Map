package Server.PostRequestHandlers;

import DataAccess.DataAccessException;
import Requests.LoadRequest;
import Results.LoadResult;
import Services.LoadService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class LoadHandler extends PostRequestHandler{

    private LoadRequest load;

    @Override
    public void handle(HttpExchange exchange){
        try {
            super.handle(exchange);

            // Change all the objects to correct model types
            load = super.gson.fromJson(super.str, LoadRequest.class);

            // Todo check that the request data is legit

            LoadService service = new LoadService();
            LoadResult result = service.load(load);

            String json = super.gson.toJson(result);
            send(json, exchange);
        } catch (IOException | DataAccessException | SQLException e) {
            e.printStackTrace();
            sendError("Internal server error", exchange);
        }
    }

    @Override
    protected void sendError(String errorStr, HttpExchange exchange) {

        LoadResult result = new LoadResult(errorStr, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }
}
