package AsyncTasks;


import android.bignerdranch.familymapclient.ServerProxy;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import Model.HttpPort;
import Requests.EventRequest;
import Results.EventResult;

public class GetEventsTask extends AsyncTask<EventRequest, Void, EventResult> {

    private ServerProxy mServerProxy;
    private HttpPort mHttpPort;

    public interface GetEventsListener {
        void onPostExecuteGetEvents(EventResult eventResult);
    }

    private GetEventsListener mListener;

    public GetEventsTask(GetEventsListener l) {
        mListener = l;
    }

    public GetEventsTask(){}

    @Override
    protected EventResult doInBackground(EventRequest... eventRequests) {

        EventRequest request = eventRequests[0];

        URL url = createURL();

        if (url != null) {
            mServerProxy = new ServerProxy(url);
            EventResult result = mServerProxy.Event(request);
            return result;
        }

        return null;
    }

    @Override
    protected void onPostExecute(EventResult eventResult) {
        mListener.onPostExecuteGetEvents(eventResult);
    }

    private URL createURL() {

        // Create URL
        URL url = null;
        try {
            url = new URL("http://" +
                    mHttpPort.getServerHost() + ":" +
                    mHttpPort.getServerPort() +
                    "/events");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Verify url is not still null
        if (url == null) {
            Log.d("EventResult", "URL not made!");
            return null;
        }

        return url;
    }

    public void setHttpPort(HttpPort httpPort) {
        mHttpPort = httpPort;
    }

    public URL createURL(GetEventsTask.EventTaskLink link) {
        Objects.requireNonNull(link);
        URL url = createURL();
        return url;
    }

    public static final class EventTaskLink{
        private EventTaskLink() {}
        private static GetEventsTask.EventTaskLink link;
        public static GetEventsTask.EventTaskLink getEventsLink() {
            if (link == null) {
                link = new GetEventsTask.EventTaskLink();
            }
            return link;
        }
    }
}
