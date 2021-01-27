package android.bignerdranch.familymapclient;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import Requests.ClearRequest;
import Requests.EventRequest;
import Requests.LoginRequest;
import Requests.PersonRequest;
import Requests.RegisterRequest;
import Results.ClearResult;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonResult;
import Results.RegisterResult;

public class ServerProxy {

    HttpURLConnection mConnection;

    public ServerProxy(URL url) {
        try {
            mConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerProxy(){}

    public LoginResult Login(LoginRequest request) {

        if (mConnection == null) return null;

        // Create JSON request string
        Gson gson = new Gson();
        String json = gson.toJson(request);

        setupPOSTRequest();
        String response = handleConnection(json);

        // Turn into result object and return
        LoginResult result = gson.fromJson(response, LoginResult.class);
        return result;
    }

    public RegisterResult Register(RegisterRequest request) {

        if (mConnection == null) return null;

        // Create JSON request string
        Gson gson = new Gson();
        String json = gson.toJson(request);

        setupPOSTRequest();
        String response = handleConnection(json);

        // Turn into result object and return
        RegisterResult result = gson.fromJson(response, RegisterResult.class);
        return result;
    }

    public PersonResult Person(PersonRequest request) {

        if (mConnection == null) return null;

        // Create JSON request string
        Gson gson = new Gson();
        String json = gson.toJson(request);

        setupGETRequest(request.getAuthToken());
        String response = handleConnection(json);

        PersonResult result = gson.fromJson(response, PersonResult.class);
        return result;
    }

    public EventResult Event(EventRequest request) {

        if (mConnection == null) return null;

        // Create JSON request string
        Gson gson = new Gson();
        String json = gson.toJson(request);

        setupGETRequest(request.getAuthToken());
        String response = handleConnection(json);

        EventResult result = gson.fromJson(response, EventResult.class);
        return result;
    }

    public ClearResult clear(ClearRequest request) {
        if (mConnection == null) return null;

        // Create JSON request string
        Gson gson = new Gson();
        String json = gson.toJson(request);

        setupPOSTRequest();
        String response = handleConnection(json);

        ClearResult result = gson.fromJson(response, ClearResult.class);
        return result;
    }

    private String handleConnection(String json) {

        try {
            // Setup connection
            mConnection.setDoOutput(true);

            if (mConnection.getRequestMethod() == "POST") {
                // Write JSON to connection
                OutputStream os = new BufferedOutputStream(mConnection.getOutputStream());
                os.write(json.getBytes());
                os.flush();
                mConnection.getOutputStream().close();
            }

            BufferedReader br = null;

            // Response came back ok
            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream successStream = mConnection.getInputStream();
                br = new BufferedReader(new InputStreamReader(successStream));
            }
            else if (mConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream errorStream = mConnection.getErrorStream();
                br = new BufferedReader(new InputStreamReader(errorStream));
            }

            // Store input into a string
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String response = sb.toString();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setupPOSTRequest() {

        try {
            mConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private void setupGETRequest(String authTokenStr) {

        try {
            mConnection.setRequestMethod("GET");
            mConnection.setRequestProperty("Authorization", authTokenStr);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    public void setConnection(HttpURLConnection connection) {
        mConnection = connection;
    }
}