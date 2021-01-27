package Server.GetRequestHandlers;

import DataAccess.*;
import Model.AuthToken;
import Model.Person;
import Requests.PersonIDRequest;
import Results.PersonIDResult;
import Services.PersonIDService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public class PersonIDHandler extends GetRequestHandler{

    private String personID;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        super.handle(exchange);
        personID = uri[2];

        try {

            if (!isValidAuthToken()) {
                sendError("error Invalid auth token", exchange);
                return;
            }
            if (!isValidPersonID()) {
                sendError("error Invalid personID parameter", exchange);
                return;
            }
            if (!isCorrectUser()) {
                sendError("error Requested person does not belong to this user", exchange);
                return;
            }

            // All checks are completed and we can now proceed with finding the correct user
            PersonIDRequest request = new PersonIDRequest(personID, super.token);
            PersonIDService service = new PersonIDService();
            PersonIDResult result = service.personID(request);

            String json = super.gson.toJson(result);
            super.sendSuccess(json, exchange);

        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            sendError("Internal server error", exchange);
        }
    }

    @Override
    protected void sendError(String err, HttpExchange exchange) {

        PersonIDResult result = new PersonIDResult(err, "false");
        String json = super.gson.toJson(result);
        super.sendError(json, exchange);
    }

    @Override
    protected void sendSuccess(String str, HttpExchange exchange) {

        PersonIDResult result = new PersonIDResult(str, "true");
        String json = super.gson.toJson(result);
        super.sendSuccess(json, exchange);
    }

    private boolean isValidPersonID() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();
        PersonDao personDao = new PersonDao(database.getConnection());
        Person person = personDao.find(personID);
        database.closeConnection(false);

        if (person != null) return true;

        return false;
    }

    private boolean isCorrectUser() throws SQLException, DataAccessException {

        Database database = new Database();
        database.openConnection();

        PersonDao personDao = new PersonDao(database.getConnection());
        Person person = personDao.find(personID);

        AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
        AuthToken token = authDao.find(super.token);

        database.closeConnection(false);

        if (person.getAssociatedUsername().equals(token.getUserName())) {
            return true;
        }

        return false;
    }
}
