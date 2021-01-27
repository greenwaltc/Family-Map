package Server.GetRequestHandlers;

import DataAccess.AuthTokenDao;
import DataAccess.DataAccessException;
import DataAccess.Database;
import Model.AuthToken;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

public class GetRequestHandler implements HttpHandler {

    String [] uri;
    String token;
    Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        uri = exchange.getRequestURI().toString().split("/");
        token = exchange.getRequestHeaders().getFirst("Authorization");
    }

    protected boolean isValidAuthToken() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();
        AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
        AuthToken authToken = authDao.find(token);
        database.closeConnection(false);

        if (authToken != null) return true;

        return false;
    }

    protected void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    protected void sendSuccess(String str, HttpExchange exchange) {

        OutputStream respBody = exchange.getResponseBody();
        try {
            exchange.sendResponseHeaders(200, 0);
            writeString(str, respBody);
            respBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendError(String str, HttpExchange exchange) {

        OutputStream respBody = exchange.getResponseBody();
        try {
            exchange.sendResponseHeaders(400, 0);
            writeString(str, respBody);
            respBody.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
