package AsyncTasks;

import Data.DataCache;

import android.bignerdranch.familymapclient.ServerProxy;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import Model.HttpPort;
import Requests.EventRequest;
import Requests.LoginRequest;
import Requests.PersonRequest;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonResult;

public class LoginTask extends AsyncTask<LoginRequest, Void, LoginResult> implements
        GetPersonsTask.GetPersonsListener,
        GetEventsTask.GetEventsListener {

    private ServerProxy mServerProxy;
    private HttpPort mHttpPort;

    public interface LoginListener {
        void onPostExecuteLogin(boolean isSuccess);
    }

    private LoginListener mListener;

    public LoginTask(LoginListener l) {
        mListener = l;
    }

    public LoginTask(){}


    @Override
    protected LoginResult doInBackground(LoginRequest... loginRequests) {

        LoginRequest request = loginRequests[0];

        // Create URL
        URL url = createURL();

        if (url != null) {
            mServerProxy = new ServerProxy(url);
            LoginResult result = mServerProxy.Login(request);
            return result;
        }

        return null;
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {

        // If the login attempt was unsuccessful, send an error back
            // Note: the model object LoginResult has success flipped
        if (loginResult.isSuccess()) {
            mListener.onPostExecuteLogin(false);
            return;
        }

        // Set the auth token for the cache
        DataCache cache = DataCache.getInstance();
        cache.setAuthToken(loginResult.getAuthToken());
        cache.setUserPersonID(loginResult.getPersonID());

        // Get the user's persons
        PersonRequest personRequest = new PersonRequest(loginResult.getAuthToken());
        GetPersonsTask personsTask = new GetPersonsTask(this );
        personsTask.setHttpPort(mHttpPort);
        personsTask.execute(personRequest);
    }

    @Override
    public void onPostExecuteGetPersons(PersonResult personResult) {

        // Add the persons to the cache
        DataCache cache = DataCache.getInstance();
        cache.loadPersons(personResult.getData());

        // Set the users names in the cache
        String fName = personResult.getData().get(1).getFirstName();
        String lName = personResult.getData().get(1).getLastName();
        cache.setFirstName(fName);
        cache.setLastName(lName);

        // Get the auth token
        String authToken = cache.getAuthToken();

        // Get the user's events
        EventRequest request = new EventRequest(authToken);
        GetEventsTask eventsTask = new GetEventsTask(this );
        eventsTask.setHttpPort(mHttpPort);
        eventsTask.execute(request);
    }

    @Override
    public void onPostExecuteGetEvents(EventResult eventResult) {

        // Add the events to the cache
        DataCache cache = DataCache.getInstance();
        cache.loadEvents(eventResult.getData());

        // Now finish out by call listener post execute
        mListener.onPostExecuteLogin(true);
    }

    private URL createURL() {

        // Create URL
        URL url = null;
        try {
            url = new URL("http://" +
                    mHttpPort.getServerHost() + ":" +
                    mHttpPort.getServerPort() +
                    "/user/login");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Verify url is not still null
        if (url == null) {
            Log.d("LoginResult", "URL not made!");
            return null;
        }

        return url;
    }

    public URL createURL(LoginTaskLink link) {
        Objects.requireNonNull(link);
        URL url = createURL();
        return url;
    }

    public static final class LoginTaskLink{
        private LoginTaskLink() {}
        private static LoginTaskLink link;
        public static LoginTaskLink getLoginLink() {
            if (link == null) {
                link = new LoginTaskLink();
            }
            return link;
        }
    }

    public void setHttpPort(HttpPort httpPort) {
        mHttpPort = httpPort;
    }
}
