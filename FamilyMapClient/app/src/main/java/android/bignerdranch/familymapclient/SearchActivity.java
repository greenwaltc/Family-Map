package android.bignerdranch.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;

import Data.DataCache;
import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private static String ARG_EVENT_ID = "event-id";

    private ImageView searchIcon;
    private ImageView clearSearchIcon;
    private EditText searchFieldEditText;

    private static String searchString;
    private static ArrayList<Person> foundPersons;
    private static ArrayList<Event> foundEvents;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchIcon = (ImageView) findViewById(R.id.searchIcon);
        Drawable sIcon = new IconDrawable(this,
                FontAwesomeIcons.fa_search).
                colorRes(R.color.colorPrimaryDark);
        searchIcon.setImageDrawable(sIcon);

        searchFieldEditText = (EditText) findViewById(R.id.searchField);
        searchFieldEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchString = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                foundPersons = findPersons();
                foundEvents = findEvents();
                SearchAdapter adapter = new SearchAdapter(foundPersons, foundEvents);
                recyclerView.setAdapter(adapter);
            }
        });

        clearSearchIcon = (ImageView) findViewById(R.id.clearSearchIcon);
        Drawable cIcon = new IconDrawable(this,
                FontAwesomeIcons.fa_times).
                colorRes(R.color.colorPrimaryDark);
        clearSearchIcon.setImageDrawable(cIcon);
        clearSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFieldEditText.setText("");
            }
        });
    }

    private ArrayList<Person> findPersons() {
        ArrayList<Person> matchingPersons = new ArrayList<>();
        ArrayList<Person> allPersons = DataCache.getInstance().getUserPersons();

        for (int i = 0; i < allPersons.size(); i++) {
            Person person = allPersons.get(i);

            if (person.getFirstName().toLowerCase().contains(searchString.toLowerCase()) ||
                person.getLastName().toLowerCase().contains(searchString.toLowerCase())) {
                matchingPersons.add(person);
            }
        }

        return matchingPersons;
    }

    private ArrayList<Event> findEvents() {
        ArrayList<Event> matchingEvents = new ArrayList<>();
        ArrayList<Event> allEvents = DataCache.getInstance().getCurrentEvents();

        for (int i = 0; i < allEvents.size(); i++) {
            Event event = allEvents.get(i);

            if (event.getCountry().toLowerCase().contains(searchString.toLowerCase()) ||
            event.getCity().toLowerCase().contains(searchString.toLowerCase()) ||
            event.getEventType().toLowerCase().contains(searchString.toLowerCase()) ||
            Integer.toString(event.getYear()).toLowerCase().contains(searchString.toLowerCase())) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchObjectViewHolder> {

        private final ArrayList<Person> persons;
        private final ArrayList<Event> events;

        SearchAdapter(ArrayList<Person> persons, ArrayList<Event> events) {
            this.persons = persons;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < persons.size() ? PERSON_ITEM_VIEW_TYPE: EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchObjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_row, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.event_row, parent, false);
            }

            return new SearchObjectViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchObjectViewHolder holder, int position) {
            if (position < persons.size()) {
                holder.bind(persons.get(position));
            }
            else {
                holder.bind(events.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() {
            return persons.size() + events.size();
        }
    }

    private class SearchObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView information;
        private final ImageView icon;

        private final int viewType;
        private Event event;
        private Person person;

        public SearchObjectViewHolder(@NonNull View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                name = itemView.findViewById(R.id.eventPersonNameTextView);
                information = itemView.findViewById(R.id.eventInfoTextView);
                icon = itemView.findViewById(R.id.eventIcon);
            }
            else {
                name = itemView.findViewById(R.id.personNameTextView);
                information = null;
                icon = itemView.findViewById(R.id.personGenderIcon);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if (viewType == EVENT_ITEM_VIEW_TYPE) {
                intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(ARG_EVENT_ID, event.getEventID());
            }
            else {
                intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.EXTRA_PERSON, person.getPersonID());
            }
            startActivity(intent);
        }

        private void bind(Event event) {
            this.event = event;

            DataCache cache = DataCache.getInstance();
            Person associatedPerson = cache.findPersonByEvent(event);
            String fullName = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();

            name.setText(fullName);

            String info = event.getEventType().toUpperCase() + ": " +
                    event.getCity() + ", " +
                    event.getCountry() + " (" +
                    event.getYear() + ")";
            information.setText(info);

            Drawable placeIcon = new IconDrawable(SearchActivity.this,
                    FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.colorPrimaryDark);
            icon.setImageDrawable(placeIcon);
        }

        private void bind(Person person) {
            this.person = person;

            String fullName = person.getFirstName() + " " + person.getLastName();
            name.setText(fullName);

            String gender = person.getGender();
            Drawable genderIcon = null;

            if (gender.equals("m")) {
                genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.colorMaleBlue);
            }
            else {
                genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.colorFemalePink);
            }
            icon.setImageDrawable(genderIcon);
        }
    }
}