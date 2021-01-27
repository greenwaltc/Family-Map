package Data;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Model.Event;
import Model.Person;

public class DataCache {

    private static DataCache instance;

    public static DataCache getInstance() {

        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {

        mUserPersons = new ArrayList<>();
        mUserEvents = new ArrayList<>();
        mColorOptions = new ArrayList<>();
        mEventTypeToColor = new HashMap<>();

        // Initialize options for colors
        mColorOptions.add(BitmapDescriptorFactory.HUE_AZURE);
        mColorOptions.add(BitmapDescriptorFactory.HUE_ROSE);
        mColorOptions.add(BitmapDescriptorFactory.HUE_YELLOW);
        mColorOptions.add(BitmapDescriptorFactory.HUE_GREEN);
        mColorOptions.add(BitmapDescriptorFactory.HUE_RED);
        mColorOptions.add(BitmapDescriptorFactory.HUE_BLUE);
        mColorOptions.add(BitmapDescriptorFactory.HUE_ORANGE);
        mColorOptions.add(BitmapDescriptorFactory.HUE_CYAN);
        mColorOptions.add(BitmapDescriptorFactory.HUE_MAGENTA);
        mColorOptions.add(BitmapDescriptorFactory.HUE_VIOLET);
    }

    private static String sUserPersonID;
    private Person mUserPerson;

    private static String sAuthToken;
    private static String sFirstName;
    private static String sLastName;

    ArrayList<Person> mUserPersons;
    ArrayList<Event> mUserEvents;

    ArrayList<Person> mCurrentPersons;
    ArrayList<Event> mCurrentEvents;

    private Event selectedEvent;

    private Map<String, Float> mEventTypeToColor;
    private ArrayList<Float> mColorOptions;

    public void clearCache() {
        instance = null;
    }

    public void loadPersons(ArrayList<Person> persons) {
        mUserPersons = persons;

        if (!(sUserPersonID.isEmpty() || sUserPersonID == null)){
            for (int i = 0; i < mUserPersons.size(); i++) {
                if (mUserPersons.get(i).getPersonID().equals(sUserPersonID)) {
                    mUserPerson = mUserPersons.get(i);
                    break;
                }
            }
        }

        mCurrentPersons = persons;
    }
    public void loadEvents(ArrayList<Event> events) {
        mUserEvents = events;
        mCurrentEvents = events;
    }

    public Event getEventByEventID(String eventID) {

        for (int i = 0; i < mUserEvents.size(); i++) {
            if (mUserEvents.get(i).getEventID().equals(eventID)) {
                return mUserEvents.get(i);
            }
        }

        return null;
    }

    public Event getFirstEventByPersonID(String personID) {

        ArrayList<Event> events = getPersonLifeEvents(personID);

        // If it doesn't exist, return null
        if (events.isEmpty()) return null;

        return events.get(0);
    }

    public Person findPersonByEvent(Event event) {

        String associatedPersonID = event.getPersonID();

        for (int i = 0; i < mUserPersons.size(); ++i) {
            Person currentPerson = mUserPersons.get(i);
            if (currentPerson.getPersonID().equals(associatedPersonID)) {
                return currentPerson;
            }
        }

        return null;
    }

    public ArrayList<Event> getPersonLifeEvents(String personID) {

        ArrayList<Event> events = new ArrayList<>();

        // Find all the events associated with a person
        for (int i = 0; i < mUserEvents.size(); i++) {
            if (mUserEvents.get(i).getPersonID().equals(personID)) {
                events.add(mUserEvents.get(i));
            }
        }

        // Sort life events
        Collections.sort(events, new SortEvents());

        return events;
    }

    public ArrayList<Event> getPersonFilteredLifeEvents(String personID) {

        ArrayList<Event> events = new ArrayList<>();

        // Find all the events associated with a person
        for (int i = 0; i < mCurrentEvents.size(); i++) {
            if (mCurrentEvents.get(i).getPersonID().equals(personID)) {
                events.add(mCurrentEvents.get(i));
            }
        }

        // Sort life events
        Collections.sort(events, new SortEvents());

        return events;
    }

    public Person getPersonByID(String personID) {

        for (int i = 0; i < mCurrentPersons.size(); ++i) {
            Person person = mCurrentPersons.get(i);
            if (person.getPersonID().equals(personID)) return person;
        }

        return null;
    }

    public Person getChildByID(String personID) {

        for (int i = 0; i < mCurrentPersons.size(); i++) {
            Person currentPerson = mCurrentPersons.get(i);

            if ( (currentPerson.getMotherID() != null &&
                    currentPerson.getMotherID().equals(personID)) ||
                 (currentPerson.getFatherID() != null &&
                         currentPerson.getFatherID().equals(personID))){
                return currentPerson;
            }
        }

        return null;
    }

    public boolean eventInSelectedEvents(Event event) {
        if (event == null) return false;

        for (int i = 0; i < mCurrentEvents.size(); i++) {
            if (event.getEventID().equals(mCurrentEvents.get(i).getEventID())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Person> getUserPersons() {
        return mUserPersons;
    }

    public static String getAuthToken() {
        return sAuthToken;
    }

    public static void setAuthToken(String authToken) {
        sAuthToken = authToken;
    }

    public static String getFirstName() {
        return sFirstName;
    }

    public static void setFirstName(String firstName) {
        sFirstName = firstName;
    }

    public static String getLastName() {
        return sLastName;
    }

    public static void setLastName(String lastName) {
        sLastName = lastName;
    }

    public ArrayList<Event> getCurrentEvents() {
        return mCurrentEvents;
    }

    public void setCurrentEvents(ArrayList<Event> currentEvents) {
        mCurrentEvents = currentEvents;
    }

    public static void setUserPersonID(String userPersonID) {
        sUserPersonID = userPersonID;
    }

    public Person getUserPerson() {
        return mUserPerson;
    }

    public Map<String, Float> getEventTypeToColor() {
        return mEventTypeToColor;
    }

    public ArrayList<Float> getColorOptions() {
        return mColorOptions;
    }

    public void setUserPerson(Person userPerson) {
        mUserPerson = userPerson;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    private class SortEvents implements Comparator<Event>
    {
        @Override
        public int compare(Event e1, Event e2) {
            if (e1.getYear() - e2.getYear() != 0) {
                return e1.getYear() - e2.getYear();
            }
            else { // The event years are the same, so order alphabetically by event type normalized to lower case
                return e1.getEventType().toLowerCase().charAt(0) -
                        e2.getEventType().toLowerCase().charAt(0);
            }
        }
    }
}