package Data;

import java.util.ArrayList;

import Model.Event;
import Model.Person;

public class Settings {

    private static Settings instance;

    private static boolean showLSL;  // Lines for Life Story
    private static boolean showFTL;  // Lines for Family Tree
    private static boolean showSL;   // Lines for Spouse
    private static boolean showFatherSide;
    private static boolean showMotherSide;
    private static boolean showMaleEvents;
    private static boolean showFemaleEvents;

    private static boolean settingsChanged;

    private ArrayList<Person> mFilteredPersons;
    private ArrayList<Person> mFatherSidePersons;
    private ArrayList<Person> mMotherSidePersons;


    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private Settings() {
        showLSL = true;
        showFTL = true;
        showSL = true;
        showFatherSide = true;
        showMotherSide = true;
        showMaleEvents = true;
        showFemaleEvents = true;

        settingsChanged = false;
    }

    public void clearSettings() {
        instance = null;
    }

    // This function basically just filters which persons are included in the settings.
        // This doesn't change what persons are shown, but it will be used later to find
        // out which events are to be shown. Only events associated with the filtered people are to
        // be shown.
    public void filterPersons() {

        DataCache cache = DataCache.getInstance();

        // Make a temporary array list of persons
        ArrayList<Person> allPersons = cache.getUserPersons();
        mFilteredPersons = new ArrayList<>();
        Person user = cache.getUserPerson();


        // First filter by fathers side or mothers side
        if (showFatherSide && !showMotherSide) { // Only show father's side
            mFilteredPersons.add(user); // In either case, the user needs to be included in the filter
            Person father = cache.getPersonByID(user.getFatherID());
            filterBySide(allPersons, mFilteredPersons, father.getPersonID());
        }
        else if (!showFatherSide && showMotherSide) { // Only show mother's side
            mFilteredPersons.add(user); // In either case, the user needs to be included in the filter
            Person mother = cache.getPersonByID(user.getMotherID());
            filterBySide(allPersons, mFilteredPersons, mother.getPersonID());
        }
        else if (!showFatherSide && !showMotherSide){
            mFilteredPersons.add(user);
            if (user.getSpouseID() != null) {
                Person spouse = cache.getPersonByID(user.getSpouseID());
                mFilteredPersons.add(spouse);
            }
        }
        else { // Show both sides, copy the array over
            for (int i = 0; i < allPersons.size(); i++) {
                mFilteredPersons.add(allPersons.get(i));
            }
        }

        // Second, filter by gender
        if (showMaleEvents && !showFemaleEvents) {
            for (int i = mFilteredPersons.size() - 1; i >= 0; i--) {
                if (!mFilteredPersons.get(i).getGender().equals("m")) { // Get rid of the females
                    mFilteredPersons.remove(i);
                }
            }
        }
        else if (!showMaleEvents && showFemaleEvents) {
            for (int i = mFilteredPersons.size() - 1; i >= 0; i--) {
                if (mFilteredPersons.get(i).getGender().equals("m")) { // Get rid of the males
                    mFilteredPersons.remove(i);
                }
            }
        }
        else if (!showMaleEvents && !showFemaleEvents) { // Neither gender events should be shown, so get rid of all people
            mFilteredPersons = null;
        }
        // Else do nothing, both genders are to be included
    }

    // This algorithm is mad inefficient
    private void filterBySide(ArrayList<Person> allPersons, ArrayList<Person> filteredPersons,
                              String personID) { // Starts with the user's fatherID

        // Base case: the personID is null
        if (personID == null) return;

        // visit the node
            // Find the person with the person ID
            // Add that person to the filtered person\
        Person person = DataCache.getInstance().getPersonByID(personID);
        filteredPersons.add(person);

        // Visit the mother's node
            // Recursively call the function with mother ID
        filterBySide(allPersons, filteredPersons, person.getMotherID());

        // Visit the father's node
            // Recursively call the function with the father ID
        filterBySide(allPersons, filteredPersons, person.getFatherID());
    }

    public void filterEvents() {
        DataCache cache = DataCache.getInstance();
        ArrayList<Event> filteredEvents = new ArrayList<>();

        if (mFilteredPersons == null || mFilteredPersons.isEmpty()) {
            cache.setCurrentEvents(filteredEvents);
            return;
        }

        for (int i = 0; i < mFilteredPersons.size(); i++) {
            Person currentPerson = mFilteredPersons.get(i);

            // Find all the events associated with the person
            ArrayList<Event> events = cache.getPersonLifeEvents(currentPerson.getPersonID());

            // Add those events to the filtered events
            for (int j = 0; j < events.size(); j++) {
                Event currentEvent = events.get(j);
                filteredEvents.add(currentEvent);
            }
        }

        // After everything, reset mCurrentEvents in data cache
        cache.setCurrentEvents(filteredEvents);
    }

    public static boolean isShowLSL() {
        return showLSL;
    }

    public static void setShowLSL(boolean showLSL) {
        Settings.showLSL = showLSL;
    }

    public static boolean isShowFTL() {
        return showFTL;
    }

    public static void setShowFTL(boolean showFTL) {
        Settings.showFTL = showFTL;
    }

    public static boolean isShowSL() {
        return showSL;
    }

    public static void setShowSL(boolean showSL) {
        Settings.showSL = showSL;
    }

    public static boolean isShowFatherSide() {
        return showFatherSide;
    }

    public static void setShowFatherSide(boolean showFatherSide) {
        Settings.showFatherSide = showFatherSide;
    }

    public static boolean isShowMotherSide() {
        return showMotherSide;
    }

    public static void setShowMotherSide(boolean showMotherSide) {
        Settings.showMotherSide = showMotherSide;
    }

    public static boolean isShowMaleEvents() {
        return showMaleEvents;
    }

    public static void setShowMaleEvents(boolean showMaleEvents) {
        Settings.showMaleEvents = showMaleEvents;
    }

    public static boolean isShowFemaleEvents() {
        return showFemaleEvents;
    }

    public static void setShowFemaleEvents(boolean showFemaleEvents) {
        Settings.showFemaleEvents = showFemaleEvents;
    }

    public static boolean isSettingsChanged() {
        return settingsChanged;
    }

    public static void setSettingsChanged(boolean settingsChanged) {
        Settings.settingsChanged = settingsChanged;
    }

    public ArrayList<Person> getFilteredPersons() {
        return mFilteredPersons;
    }
}
