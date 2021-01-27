package AsyncTasks;

import android.bignerdranch.familymapclient.ServerProxy;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import Model.HttpPort;
import Requests.PersonRequest;
import Results.PersonResult;

public class GetPersonsTask extends AsyncTask<PersonRequest, Void, PersonResult> {

    private ServerProxy mServerProxy;
    private HttpPort mHttpPort;

    public interface GetPersonsListener {
        void onPostExecuteGetPersons(PersonResult personResult);
    }

    private GetPersonsListener mListener;

    public GetPersonsTask(GetPersonsListener l) {
        mListener = l;
    }

    public GetPersonsTask(){}

    @Override
    protected PersonResult doInBackground(PersonRequest... personRequests) {

        PersonRequest request = personRequests[0];

        URL url = createURL();

        if (url != null) {
            mServerProxy = new ServerProxy(url);
            PersonResult result = mServerProxy.Person(request);
            return result;
        }

        return null;
    }

    @Override
    protected void onPostExecute(PersonResult personResult) {
        mListener.onPostExecuteGetPersons(personResult);
    }

    private URL createURL() {

        // Create URL
        URL url = null;
        try {
            url = new URL("http://" +
                    mHttpPort.getServerHost() + ":" +
                    mHttpPort.getServerPort() +
                    "/person");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Verify url is not still null
        if (url == null) {
            Log.d("PersonResult", "URL not made!");
            return null;
        }

        return url;
    }

    public void setHttpPort(HttpPort httpPort) {
        mHttpPort = httpPort;
    }

    public URL createURL(GetPersonsTask.PersonTaskLink link) {
        Objects.requireNonNull(link);
        URL url = createURL();
        return url;
    }

    public static final class PersonTaskLink{
        private PersonTaskLink() {}
        private static GetPersonsTask.PersonTaskLink link;
        public static GetPersonsTask.PersonTaskLink getPersonsLink() {
            if (link == null) {
                link = new GetPersonsTask.PersonTaskLink();
            }
            return link;
        }
    }
}
