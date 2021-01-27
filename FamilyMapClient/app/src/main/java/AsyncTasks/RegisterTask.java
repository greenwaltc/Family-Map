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
import Requests.PersonRequest;
import Requests.RegisterRequest;
import Results.EventResult;
import Results.PersonResult;
import Results.RegisterResult;

public class RegisterTask extends AsyncTask<RegisterRequest, Void, RegisterResult> implements
        GetPersonsTask.GetPersonsListener,
        GetEventsTask.GetEventsListener{

    private ServerProxy mServerProxy;
    private HttpPort mHttpPort;

    public interface RegisterListener {
        void onPostExecuteRegister(String message, boolean isSuccess);
    }

    private RegisterListener mListener;

    public RegisterTask(RegisterListener l) {
        mListener = l;
    }

    public RegisterTask(){}

    @Override
    protected RegisterResult doInBackground(RegisterRequest... registerRequests) {

        RegisterRequest request = registerRequests[0];

        // Create URL
        URL url = createURL();

        if (url != null) {
            mServerProxy = new ServerProxy(url);
            RegisterResult result = mServerProxy.Register(request);
            return result;
        }

        return null;
    }

    private URL createURL() {

        // Create URL
        URL url = null;
        try {
            url = new URL("http://" +
                    mHttpPort.getServerHost() + ":" +
                    mHttpPort.getServerPort() +
                    "/user/register");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Verify url is not still null
        if (url == null) {
            Log.d("Register Result", "URL not made!");
            return null;
        }

        return url;
    }

    public void setHttpPort(HttpPort httpPort) {
        mHttpPort = httpPort;
    }

    @Override
    protected void onPostExecute(RegisterResult registerResult) {

        // If the login attempt was unsuccessful, send an error back
        // Note: the model object LoginResult has success flipped
        if (!registerResult.isSuccess()) {
            mListener.onPostExecuteRegister(registerResult.getMessage(), false);
            return;
        }

        // Set the auth token for the cache
        DataCache cache = DataCache.getInstance();
        cache.setAuthToken(registerResult.getAuthToken());
        cache.setUserPersonID(registerResult.getPersonID());

        // Get the user's persons
        PersonRequest personRequest = new PersonRequest(registerResult.getAuthToken());
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
        String fName = personResult.getData().get(0).getFirstName();
        String lName = personResult.getData().get(0).getLastName();
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
        mListener.onPostExecuteRegister(null, true);
    }

    public URL createURL(RegisterTask.RegisterTaskLink link) {
        Objects.requireNonNull(link);
        URL url = createURL();
        return url;
    }

    public static final class RegisterTaskLink{
        private RegisterTaskLink() {}
        private static RegisterTask.RegisterTaskLink link;
        public static RegisterTask.RegisterTaskLink getRegisterLink() {
            if (link == null) {
                link = new RegisterTask.RegisterTaskLink();
            }
            return link;
        }
    }

}
