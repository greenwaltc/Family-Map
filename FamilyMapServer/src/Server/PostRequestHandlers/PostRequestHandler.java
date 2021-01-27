package Server.PostRequestHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;

public class PostRequestHandler implements HttpHandler {

    protected Gson gson;
    protected Object obj;
    protected String str;
    protected boolean success;
    protected String message;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        gson = new Gson();
        Reader reader = new InputStreamReader(exchange.getRequestBody());
        obj = gson.fromJson(reader, (Type) Object.class);
        str = gson.toJson(obj);
    }

    protected void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    protected void send(String str, HttpExchange exchange) {

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
