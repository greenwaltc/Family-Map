package android.bignerdranch.familymapclient;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.DataCache;
import Data.Settings;
import Model.Event;
import Model.Person;


public class MapFragment extends Fragment {

    private static String ARG_EVENT_ID = "event-id";
    private static String ARG_CAMERA_POS = "camera-pos";

    private LinearLayout eventLayout;
    private ImageView genderImageView;
    private TextView defaultEventTextView;
    private TextView personNameTextView;
    private TextView eventDetailsTextView;

    private GoogleMap map;
    private Event selectedEvent;
    private Map<Marker, Event> markersToEvents;
    private List<Polyline> lines;

    private Map<String, Float> eventTypeToColor;
    private ArrayList<Float> colorOptions;
    private int colorIndex = 0;

    private ArrayList<Polyline> polyLines;
    private static int lineWidth = 10;
    private static int genOneLineWidth = 16;
    private static final int SPOUSE_LINE_COLOR = Color.RED;
    private static final int LIFE_EVENTS_LINE_COLOR = Color.BLUE;
    private static final int FAMILY_TREE_LINE_COLOR = Color.GRAY;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public static MapFragment newInstance(String eventID) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    public boolean haveEventIDArgument() {
        return (getArguments() != null && getArguments().containsKey(ARG_EVENT_ID));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof MainActivity){
            setHasOptionsMenu(true);
        }

        DataCache cache = DataCache.getInstance();
        colorOptions = cache.getColorOptions();
        eventTypeToColor = cache.getEventTypeToColor();

        polyLines = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        // Inflate and set up the default image for event gender view
        genderImageView = (ImageView) view.findViewById(R.id.genderImage);
        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).
                                                colorRes(R.color.colorAndroidGreen);
        genderImageView.setImageDrawable(genderIcon);

        // Enable the event layout when clicked to go to a person activity
        eventLayout = (LinearLayout) view.findViewById(R.id.eventInfoView);
        eventLayout.setOnClickListener(eventInfoClickListener);

        // Inflate and set up default text for event info
        defaultEventTextView = (TextView) view.findViewById(R.id.defaultEventTextView);
        defaultEventTextView.setText(getString(R.string.defaultEventText));

        // Inflate person and event details text views
        personNameTextView = (TextView) view.findViewById(R.id.personNameTextView);
        eventDetailsTextView = (TextView) view.findViewById(R.id.eventDetailsTextView);

        markersToEvents = new HashMap<>();
        selectedEvent = null;
        lines = new ArrayList<>();

        if (savedInstanceState == null) {
            if (haveEventIDArgument()) {
                String eventID = getArguments().getString(ARG_EVENT_ID);
                DataCache cache = DataCache.getInstance();
                selectedEvent = cache.getEventByEventID(eventID);
            }
        }
        else {
            if (savedInstanceState.containsKey(ARG_EVENT_ID)) {
                String eventID = savedInstanceState.getString(ARG_EVENT_ID);
                DataCache cache = DataCache.getInstance();
                selectedEvent = cache.getEventByEventID(eventID);
            }
        }

        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setOnMarkerClickListener(markerClickListener);

                addMarkers(false);

                if (selectedEvent != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLng(getLatLng(selectedEvent)));
                    updateEventView();
                }
            }
        });

        return view;
    }

    private void addMarkers(boolean createEvents) {

        DataCache cache = DataCache.getInstance();
        ArrayList<Event> events = cache.getCurrentEvents();
        Person user = cache.getUserPerson();

        if (events == null) return;

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            Double latitude = event.getLatitude();
            Double longitude = event.getLongitude();

            LatLng location = new LatLng(latitude, longitude);
            String locationName = event.getCity() + ", " + event.getCountry();

            makeMarker(location, locationName, event);

            // A nice little feature to move the camera to the users birth
                // Will need to be changed later
            if (event.getPersonID().equals(user.getPersonID()) &&
                event.getEventType().toLowerCase().equals("birth")) {

                map.animateCamera(CameraUpdateFactory.newLatLng(location));

                Toast.makeText(getActivity(),
                        "You were born here!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateEventView() {

        if (defaultEventTextView != null) {
            defaultEventTextView.setText("");
            defaultEventTextView = null;
        }
        if (genderImageView != null) {
            genderImageView.setImageDrawable(null);
        }

        Person person = DataCache.getInstance().findPersonByEvent(selectedEvent);

        // Set the appropriate icon
        if (person.getGender().equals("m")) {
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.colorMaleBlue);
            genderImageView.setImageDrawable(genderIcon);
        }
        else {
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.colorFemalePink);
            genderImageView.setImageDrawable(genderIcon);
        }

        // Set the person's name to the appropriate field
        String fullName = person.getFirstName() + " " + person.getLastName();
        personNameTextView.setText(fullName);

        // Set the event info to the appropriate field
        String eventInfo = selectedEvent.getEventType().toUpperCase() + ": " +
                selectedEvent.getCity() + ", " + selectedEvent.getCountry() + " (" +
                selectedEvent.getYear() + ")";
        eventDetailsTextView.setText(eventInfo);

        // Draws the poly lines
        drawLines(person);
    }

    private void drawLines(Person person) {

        View v = getView();
        DataCache cache = DataCache.getInstance();

        // Remove all previous lines
        for (Polyline p : polyLines) {
            if (p != null) p.remove();
        }

        // Get settings to see which lines to draw
        Settings settings = Settings.getInstance();

        // Draw the spouse lines
        if (settings.isShowSL()) {
            Event spouseEarliestEvent = cache.getFirstEventByPersonID(person.getSpouseID());

            if (spouseEarliestEvent != null && cache.eventInSelectedEvents(spouseEarliestEvent)) { // Draw the lines for the spouse
                drawSingleLine(selectedEvent, spouseEarliestEvent, lineWidth, SPOUSE_LINE_COLOR);
            }
        }

        // Draw life story lines
        if (settings.isShowLSL()) {
            ArrayList<Event> lifeEvents = cache.getPersonLifeEvents(person.getPersonID());

            if (!lifeEvents.isEmpty()) {

                for (int i = 0; i < lifeEvents.size(); ++i) {
                    if (i < lifeEvents.size() - 1 &&
                        cache.eventInSelectedEvents(lifeEvents.get(i))) {
                        drawSingleLine(lifeEvents.get(i), lifeEvents.get(i + 1), lineWidth, LIFE_EVENTS_LINE_COLOR);
                    }
                }
            }
        }

        // Draw family tree lines
        if (settings.isShowFTL()) {
            drawFamilyTreeLines(person, selectedEvent, genOneLineWidth);
        }
    }

    private void drawFamilyTreeLines(Person person, Event event, Integer width) {

        // Base case
        if (person == null || event == null) return;

        // if the line width is negative, reset it to 1
        if (width <= 0) width = 1;

        // Visit the node
        DataCache cache = DataCache.getInstance();
        String fatherID = person.getFatherID();
        String motherID = person.getMotherID();
        Event fatherEarliestEvent = null;
        Event motherEarliestEvent = null;

        // Check if the event is supposed to have lines drawn to it

        if (fatherID != null) {
            // Draw the line to father's earliest event
            fatherEarliestEvent = cache.getFirstEventByPersonID(person.getFatherID());
            if (cache.eventInSelectedEvents(fatherEarliestEvent) && cache.eventInSelectedEvents(event)) {
                drawSingleLine(event, fatherEarliestEvent, width, FAMILY_TREE_LINE_COLOR);
            }
            else if (cache.eventInSelectedEvents(fatherEarliestEvent) && !cache.eventInSelectedEvents(event)) {
                drawSingleLine(selectedEvent, fatherEarliestEvent, width, FAMILY_TREE_LINE_COLOR);
            }
        }

        if (motherID != null) {
            // Draw the line to the mother's earliest event
            motherEarliestEvent = cache.getFirstEventByPersonID(person.getMotherID());
            if (cache.eventInSelectedEvents(motherEarliestEvent) && cache.eventInSelectedEvents(event)) {
                drawSingleLine(event, motherEarliestEvent, width, FAMILY_TREE_LINE_COLOR);
            }
            else if (cache.eventInSelectedEvents(motherEarliestEvent) && !cache.eventInSelectedEvents(event)) {
                drawSingleLine(selectedEvent, motherEarliestEvent, width, FAMILY_TREE_LINE_COLOR);
            }
        }

        // Visit the left child (father)
        Person father = cache.getPersonByID(person.getFatherID());
        drawFamilyTreeLines(father, fatherEarliestEvent, width / 2);

        // Visit the right child (mother)
        Person mother = cache.getPersonByID(person.getMotherID());
        drawFamilyTreeLines(mother, motherEarliestEvent, width / 2);
    }

    private LatLng getLatLng(Event event) {

        Double latitude = event.getLatitude();
        Double longitude = event.getLongitude();

        LatLng location = new LatLng(latitude, longitude);

        return location;
    }

    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            selectedEvent = markersToEvents.get(marker);

            updateEventView();

            // Move camera to new event
            LatLng position = new LatLng(selectedEvent.getLatitude(),
                    selectedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(position));

            return true;
        }
    };

    private View.OnClickListener eventInfoClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Person person = DataCache.getInstance().findPersonByEvent(selectedEvent);
            String personID = person.getPersonID();
            startPersonActivity(personID);
        }
    };

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                DataCache.getInstance().setSelectedEvent(selectedEvent);
                startSettingsActivity();
                return true;
            case R.id.action_search:
                startSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void startPersonActivity(String personID) {
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        intent.putExtra(PersonActivity.EXTRA_PERSON, personID);
        startActivity(intent);
    }

    private void makeMarker(LatLng pos, String location, Event event) {

        String eventType = event.getEventType().toLowerCase();
        float color = getMarkerColor(eventType);

        Marker m = map.addMarker(new MarkerOptions().
                position(pos).
                title(location).
                icon(BitmapDescriptorFactory.defaultMarker(color)));

        markersToEvents.put(m, event);
        m.setTag(event);
    }

    private float getMarkerColor(String eventType) {

        Float color;

        if (eventTypeToColor.get(eventType) == null) { // This event type hasn't been handled yet
            color = colorOptions.get(colorIndex);
            colorIndex++;
            colorIndex %= colorOptions.size();
            eventTypeToColor.put(eventType, color);
        }
        else {
            color = eventTypeToColor.get(eventType);
        }

        return color;
    }

    private void drawSingleLine(Event e1, Event e2, int width, int color) {

        Polyline line = map.addPolyline(new PolylineOptions().
                add(getLatLng(e1),
                        getLatLng(e2)).
                width(width).
                color(color));
        polyLines.add(line);
    }
}


// todo Make sure colors match (case insensitive)
