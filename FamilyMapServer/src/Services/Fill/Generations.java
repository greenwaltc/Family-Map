package Services.Fill;

import DataAccess.*;
import Model.*;
import DataAccess.DataAccessException;
import com.google.gson.Gson;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/* For use by Services.Services.FillService and Services.Services.RegisterService */
public class Generations {

    private User user;
    int NUM_PEOPLE;
    int ARRAY_SIZE;
    int EVENTS;
    int NUM_EVENTS;
    LocationData locations;
    FemaleFirstNameData fnames;
    MaleFirstNameData mnames;
    SurnamesData snames;
    private int numGenerations;
    private ArrayList<Person> people = new ArrayList<>();
    private ArrayList<ArrayList<Event>> events = new ArrayList<>(); // The order of the 3 randomly generated events, by index, goes
                                                                        // [0] - birth, [1] - marriage, [2] - death
    private String fatherID = null;
    private String motherID = null;
    private boolean lastGen = false;

    public Generations(User user) {
        this.user = user;
        numGenerations = 4 + 1; // Here, we are treating the user like a generation, which is not how it's done
        // in the spec, so we add a "generation"

        NUM_PEOPLE = ((int) Math.pow(2, numGenerations)) - 1;
        ARRAY_SIZE = NUM_PEOPLE + 1;
        EVENTS = 3;

        clearPeople(this.user.getUserName());
        clearEvents(this.user.getUserName());
        initializeData();
    }

    public Generations(User user, int generations) {
        this.user = user;
        numGenerations = generations + 1; // Here, we are treating the user like a generation, which is not how it's done
        // in the spec, so we add a "generation"

        NUM_PEOPLE = ((int) Math.pow(2, numGenerations)) - 1;
        ARRAY_SIZE = NUM_PEOPLE + 1;
        EVENTS = 3;
        NUM_EVENTS = 0;

        clearPeople(this.user.getUserName());
        clearEvents(this.user.getUserName());
        initializeData();
    }

    /**
     * Populates the server's database with generated data for the specified
     * user name. The required username parameter must be a user already
     * registered with the server. If there is any data in the database
     * already associated with the given user name, it is deleted. The
     * generations parameter lets the caller specify the number
     * of generations of ancestors to be generated, and must be a non-negative
     * integer
     *
     * @throws DataAccessException
     */
    public void fill() throws DataAccessException, SQLException {

        // Generate new people
        for (int i = 1; i < ARRAY_SIZE; ++i) {

            if (i == 1) {
                generateUserPerson();
                continue;
            }
            if (i % 2 == 0) { // Looking at the male, generate female in tandem
                generateCouple(i);
            }
        }

        // Generate events
        for (int i = 1; i < ARRAY_SIZE; ++i) {
            if (i == 1) {
                generateUserBirth(i);
                continue;
            }
            generateEvents(i); // Will return an array of size 3 that contains the birth, marriage, and death data for an individual person.
        }

        people.remove(0);
        events.remove(0);

        addToDatabase();
    }

    private void addToDatabase() throws DataAccessException, SQLException {

        Database database = new Database();

        try {
            database.openConnection();
            PersonDao personDao = new PersonDao(database.getConnection());
            personDao.insert(people);

            EventDao eventDao = new EventDao(database.getConnection());
            for (ArrayList<Event> e : events) {
                eventDao.insert(e);
            }
            database.closeConnection(true);
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            database.closeConnection(false);
            throw e;
        }
    }

    private void generateCouple(final int INDEX) {

        Person child = people.get(INDEX / 2);

        int result = (int) Math.pow(2, (numGenerations - 1));
        if (INDEX >= result) { // We are not at the last generation yet, so this person has father/mother data
            lastGen = true;
            fatherID = null;
            motherID = null;
        } else {
            lastGen = false;
        }

        Person father = generateFather(child);
        Person mother = generateMother(child, father.getLastName(), INDEX);

        if (INDEX == 2) { // this is the users direct parents and should have the appropriate people IDs
            father.setPersonID(child.getFatherID());
            mother.setPersonID(child.getMotherID());
        }

        father.setSpouseID(mother.getPersonID());
        mother.setSpouseID(father.getPersonID());

        people.set(INDEX, father);
        people.set(INDEX + 1, mother);
    }

    private Person generateFather(Person child) {

        String personID = child.getFatherID();

        if (!lastGen) {
            fatherID = UUID.randomUUID().toString();
            motherID = UUID.randomUUID().toString();
        }

        String fName = getRandomName("mnames");
        String sName = getRandomName("snames");

        return generatePerson(personID, fName, sName, "m", fatherID, motherID);
    }

    private Person generateMother(Person child, String sName, final int INDEX) {

        String personID = child.getMotherID();

        if (!lastGen) {
            fatherID = UUID.randomUUID().toString();
            motherID = UUID.randomUUID().toString();
        }

        String fName = getRandomName("fnames");

        return generatePerson(personID, fName, sName, "f", fatherID, motherID);
    }

    private Person generatePerson(String personID, String fName, String sName, String gender, String fatherID, String motherID) {

        return new Person(personID, user.getUserName(), fName, sName, gender, fatherID, motherID);
    }

    private void generateUserPerson() {

        String fatherID = UUID.randomUUID().toString();
        String motherID = UUID.randomUUID().toString();

        Person person = new Person(user.getPersonID(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                fatherID, motherID);

        people.set(1, person);
    }

    private void generateUserBirth(final int INDEX) {

        NUM_EVENTS++;
        events.get(1).set(0, createUserBirth(INDEX));
    }

    private Event createUserBirth(final int INDEX) {

        int birthYear = (int) (Math.random() * (2020 - 1940 + 1) + 1940); // Generates a random birth year between 1940 and 2020
        String eventID = UUID.randomUUID().toString();
        String eventType = "birth";

        Location location = getRandomLocation();
        String country = location.getCountry();
        String city = location.getCity();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Event birth = new Event(eventID,
                user.getUserName(),
                people.get(INDEX).getPersonID(),
                latitude, longitude,
                country, city,
                eventType, birthYear);

        return birth;
    }

    private void generateEvents(final int INDEX) {

        ArrayList<Event> childEvents = events.get(INDEX / 2);
        Person husband = null;
        Person wife = null;

        if (INDEX % 2 == 0) {
            husband = people.get(INDEX); // Wife's events haven't been generated yet. Generate husband's first before
            // wife's
        } else {
            wife = people.get(INDEX);   // Now that husband's event have been generated, we can use those to make the
            husband = people.get(INDEX - 1);   // wife's, so she is not set to null
        }

        events.get(INDEX).set(0, createBirth(husband, wife, childEvents)); NUM_EVENTS++;
        events.get(INDEX).set(1, createMarriage(husband, wife, childEvents, INDEX)); NUM_EVENTS++;
        events.get(INDEX).set(2, createDeath(childEvents, INDEX)); NUM_EVENTS++;
    }

    private Event createBirth(Person husband, Person wife, ArrayList<Event> childEvents) {

        if (wife == null) { // We need to make the birth for the husband

            int husbandBirthUpper = childEvents.get(0).getYear() - 19; // Husbands and wives will not give birth until at
            // least one year after being married at age
            // 18 at the earliest.
            int husbandBirthLower = childEvents.get(0).getYear() - 60; // Husbands will be able to have children at up to
            // 60 years old
            int husbandBirthYear = (int) (Math.random() * (husbandBirthUpper - husbandBirthLower + 1) + husbandBirthLower);

            String eventID = UUID.randomUUID().toString();
            Location location = getRandomLocation();

            return createEvent(eventID, husband.getPersonID(), location, "birth", husbandBirthYear);
        } else { // Generate a birth for the wife

            int wifeBirthUpper = childEvents.get(0).getYear() - 19;
            int wifeBirthLower = childEvents.get(0).getYear() - 50; // Women will not give birth above 50 years old

            int wifeBirthYear = (int) (Math.random() * (wifeBirthUpper - wifeBirthLower + 1) + wifeBirthLower);
            String eventID = UUID.randomUUID().toString();
            Location location = getRandomLocation();

            return createEvent(eventID, wife.getPersonID(), location, "birth", wifeBirthYear);
        }
    }

    private Event createMarriage(Person husband, Person wife, ArrayList<Event> childEvents, final int INDEX) {

        if (wife == null) { // Wife data has not been generated yet, so bounds for marriage cannot be created

            return null;
        } else {

            Event husbandBirth = events.get(INDEX - 1).get(0);
            Event wifeBirth = events.get(INDEX).get(0);

            int marriageUpper = childEvents.get(0).getYear() - 1;
            int marriageLower;

            if (husbandBirth.getYear() > wifeBirth.getYear()) {
                marriageLower = husbandBirth.getYear() + 18;
            } else {
                marriageLower = wifeBirth.getYear() + 18;
            }
            int marriageYear = (int) (Math.random() * (marriageUpper - marriageLower + 1) + marriageLower);

            String eventID = UUID.randomUUID().toString();
            Location location = getRandomLocation();

            events.get(INDEX - 1).set(1, createEvent(UUID.randomUUID().toString(),
                    husband.getPersonID(),
                    location,
                    "marriage",
                    marriageYear));

            return createEvent(eventID, wife.getPersonID(), location, "marriage", marriageYear);
        }
    }

    private Event createDeath(ArrayList<Event> childEvents, final int INDEX) {

        String eventID = UUID.randomUUID().toString();
        String personID = people.get(INDEX).getPersonID();
        Location location = getRandomLocation();
        String eventType = "death";

        int childBirthYear = childEvents.get(0).getYear();
        int lower = childBirthYear; // A person can't die before their child is born
        int upper = events.get(INDEX).get(0).getYear() + 120; // Grabs the person's birth year, and people won't live
        // above 120 years old
        int deathYear = (int) (Math.random() * (upper - lower + 1) + lower);

        return createEvent(eventID, personID, location, eventType, deathYear);
    }

    private Event createEvent(String ID, String personID, Location location, String eventType, int year) {

        String country = location.getCountry();
        String city = location.getCity();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        return new Event(ID, user.getUserName(), personID, latitude, longitude, country, city, eventType, year);
    }


    private void clearPeople(String userName) {

        Database database = new Database();
        try {
            database.openConnection();
            PersonDao dao = new PersonDao(database.getConnection());
            dao.removeUserName(userName);
            database.closeConnection(true); // Tod: change when things are working right
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void clearEvents(String userName) {

        Database database = new Database();
        try {
            database.openConnection();
            EventDao dao = new EventDao(database.getConnection());
            dao.removeUserName(userName);
            database.closeConnection(true); // Tod: change when things are working right
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    /**
     * Initializes people and event arrays to their correct sizes.
     * Gets and stores the location, fname, mname, and sname data from the json files.
     */
    private void initializeData() {

        initializePeople(ARRAY_SIZE);
        initializeEvents(ARRAY_SIZE, EVENTS);
        getData("locations");
        getData("fnames");
        getData("mnames");
        getData("snames");
    }

    private void getData(String type) {

        try {
            Gson gson = new Gson();
            Reader reader = new FileReader("./json/" + type + ".json");
            if (type.equals("locations")) locations = gson.fromJson(reader, LocationData.class);
            else if (type.equals("fnames")) fnames = gson.fromJson(reader, FemaleFirstNameData.class);
            else if (type.equals("mnames")) mnames = gson.fromJson(reader, MaleFirstNameData.class);
            else if (type.equals("snames")) snames = gson.fromJson(reader, SurnamesData.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize people array to all 0's
     * User (person) index will start at 1, 0 will be left blank
     * or store the data for the user's spouse
     *
     * @param ARRAY_SIZE
     */
    private void initializePeople(final int ARRAY_SIZE) {

        for (int i = 0; i < ARRAY_SIZE; i++) {
            people.add(i, null);
        }
    }

    /**
     * Initialize event list
     * Events array will have the same size as people array
     * (each person gets 3 events)
     *
     * @param ARRAY_SIZE
     * @param NUM_EVENTS
     */
    private void initializeEvents(final int ARRAY_SIZE, final int NUM_EVENTS) {

        for (int i = 0; i < ARRAY_SIZE; ++i) {
            events.add(new ArrayList<>());
        }
        for (ArrayList<Event> e : events) {
            for (int i = 0; i < NUM_EVENTS; ++i) {
                e.add(null);
            }
        }
    }

    private Location getRandomLocation() {

        int upper = locations.data.length - 1;
        int lower = 0;
        int r = (int) (Math.random() * (upper - lower)) + lower;
        Location randLoc = locations.data[r];
        return randLoc;
    }

    private String getRandomName(String type) {

        int upper;
        int lower = 0;

        if (type.equals("fnames")) {
            upper = fnames.data.length - 1;
            return fnames.data[ (int) (Math.random() * (upper - lower)) + lower ];
        }
        else if (type.equals("mnames")) {
            upper = mnames.data.length - 1;
            return mnames.data[ (int) (Math.random() * (upper - lower)) + lower ];
        }
        else if (type.equals("snames")) {
            upper = snames.data.length - 1;
            return snames.data[ (int) (Math.random() * (upper - lower)) + lower ];
        }
        else {
            System.out.println("Error finding random name");
            return null;
        }
    }


    public int getNUM_PEOPLE() {
        return NUM_PEOPLE;
    }

    public int getNUM_EVENTS() {
        return NUM_EVENTS;
    }
}