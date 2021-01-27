package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("GET")) {
            String urlPath = exchange.getRequestURI().toString();
            String filePath;

            if (urlPath == null || urlPath.equals("/")) {
                urlPath = urlPath + "index.html";
            }

            filePath = "web" + urlPath;
            sendFile(filePath, exchange);
        }
        else {
            exchange.sendResponseHeaders(405,0); // Anything other than a GET sends this error
        }
    }

    /**
     * Sends a 404 (file not found) error
     *
     * @param exchange HTTP exchange given in handler
     * @throws IOException
     */
    private void send404(HttpExchange exchange) throws IOException {

        String fileLocation = "web/HTML/404.html";
        File file = new File(fileLocation);

        if (file.exists()) {
            OutputStream respBody = exchange.getResponseBody();
            exchange.sendResponseHeaders(404, 0);
            Files.copy(file.toPath(), respBody);
            respBody.close();
        }
        else{
            throw new IOException("Error: 404.html file not found.");
        }
    }

    /**
     * Sends the specified file in the url from the working directory to the client.
     *
     * @param filePath File path from URL
     * @param exchange HTTP exchange given in handler
     * @throws IOException
     */
    private void sendFile(String filePath, HttpExchange exchange) throws IOException {

        File webFile = new File(filePath);

        if (!webFile.exists()) {
            send404(exchange);
        }
        else {
            OutputStream respBody = exchange.getResponseBody();
            exchange.sendResponseHeaders(200, 0);
            Files.copy(webFile.toPath(), respBody);
            respBody.close();
        }
    }
}
