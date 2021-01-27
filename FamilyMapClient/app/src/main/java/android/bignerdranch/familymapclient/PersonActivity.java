package android.bignerdranch.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;

import Data.DataCache;
import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    private static String ARG_EVENT_ID = "event-id";

    public static final String EXTRA_PERSON =
            "android.bignerdranch.familymapclient.extra_person";

    private static String mPersonID;
    private static Person mPerson;

    private static TextView mFNameTextView;
    private static TextView mLNameTextView;
    private static TextView mGenderTextView;

    private static ArrayList<Event> mLifeEvents;
    private static ArrayList<FamilyMember> mFamilyMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_PERSON)) {
                mPersonID = intent.getStringExtra(EXTRA_PERSON);
                DataCache cache = DataCache.getInstance();
                mPerson = cache.getPersonByID(mPersonID);
                mLifeEvents = cache.getPersonFilteredLifeEvents(mPersonID);
                getFamily();
            } else return;
        }

        // Inflate widgets
        mFNameTextView = (TextView) findViewById(R.id.personFNameTextView);
        mFNameTextView.setText(mPerson.getFirstName());

        mLNameTextView = (TextView) findViewById(R.id.personLNameTextView);
        mLNameTextView.setText(mPerson.getLastName());

        mGenderTextView = (TextView) findViewById(R.id.personGenderTextView);
        if (mPerson.getGender().equals("m")) {
            mGenderTextView.setText("Male");
        }
        else {
            mGenderTextView.setText("Female");
        }
        
        //todo expandable lists
        ExpandableListView lifeEventsExpandableListView =
                findViewById(R.id.personExpandableListView);
        lifeEventsExpandableListView.setAdapter(new ExpandableListAdapter(mLifeEvents, mFamilyMembers));

    }

    private void getFamily() {
        DataCache cache = DataCache.getInstance();
        mFamilyMembers = new ArrayList<>();

        if (mPerson.getFatherID() != null) {
            Person person = cache.getPersonByID(mPerson.getFatherID());
            FamilyMember father = new FamilyMember(person, FamilyMember.Relation.FATHER);
            mFamilyMembers.add(father);
        }
        if (mPerson.getMotherID() != null) {
            Person person = cache.getPersonByID(mPerson.getMotherID());
            FamilyMember mother = new FamilyMember(person, FamilyMember.Relation.MOTHER);
            mFamilyMembers.add(mother);
        }
        if (mPerson.getSpouseID() != null) {
            Person person = cache.getPersonByID(mPerson.getSpouseID());
            FamilyMember spouse = new FamilyMember(person, FamilyMember.Relation.SPOUSE);
            mFamilyMembers.add(spouse);
        }

        Person child = cache.getChildByID(mPersonID);
        if (child != null) {
            FamilyMember c = new FamilyMember(child, FamilyMember.Relation.CHILD);
            mFamilyMembers.add(c);
        }
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENTS_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final ArrayList<Event> lifeEvents;
        private final ArrayList<FamilyMember> family;

        ExpandableListAdapter(ArrayList<Event> events, ArrayList<FamilyMember> family) {
            lifeEvents = events;
            this.family = family;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return getString(R.string.lifeEventsTitle);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.familyTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return family.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    titleView.setText(R.string.lifeEventsTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView = null;

            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_row, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_row, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeLifeEventView(View lifeEventItemView, final int childPosition) {

            RelativeLayout row = lifeEventItemView.findViewById(R.id.eventRow);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startEventActivity(lifeEvents.get(childPosition).getEventID());
                }
            });

            ImageView image = lifeEventItemView.findViewById(R.id.eventIcon);
            Drawable placeIcon = new IconDrawable(PersonActivity.this,
                    FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.colorPrimaryDark);
            image.setImageDrawable(placeIcon);

            TextView eventInfo = lifeEventItemView.findViewById(R.id.eventInfoTextView);
            Event event = lifeEvents.get(childPosition);
            String info = event.getEventType().toUpperCase() + ": " +
                    event.getCity() + ", " +
                    event.getCountry();
            eventInfo.setText(info);

            Person person = DataCache.getInstance().findPersonByEvent(event);
            TextView eventName = lifeEventItemView.findViewById(R.id.eventPersonNameTextView);
            String name = person.getFirstName() + " " + person.getLastName();
            eventName.setText(name);
        }

        private void initializePersonView(View personItemView, final int childPosition) {

            FamilyMember fm = family.get(childPosition);
            final Person person = fm.getPerson();
            final String gender = person.getGender();
            final String name = person.getFirstName() + " " + person.getLastName();

            RelativeLayout row = personItemView.findViewById(R.id.personRow);
            row.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    startPersonActivity(person.getPersonID());
                }
            });

            ImageView image = personItemView.findViewById(R.id.personGenderIcon);
            Drawable genderIcon = null;
            if (gender.equals("m")) {
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.colorMaleBlue);
            }
            else {
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.colorFemalePink);
            }
            image.setImageDrawable(genderIcon);

            TextView personName = personItemView.findViewById(R.id.personNameTextView);
            personName.setText(name);

            TextView relationship = personItemView.findViewById(R.id.personRelationshipTextView);
            switch (fm.getRelation()) {
                case CHILD:
                    relationship.setText("Child");
                    break;
                case FATHER:
                    relationship.setText("Father");
                    break;
                case MOTHER:
                    relationship.setText("Mother");
                    break;
                case SPOUSE:
                    relationship.setText("Spouse");
                    break;
            }
        }

        private void startEventActivity(String eventID) {
            Intent intent = new Intent(PersonActivity.this, EventActivity.class);
            intent.putExtra(ARG_EVENT_ID, eventID);
            startActivity(intent);
        }

        private void startPersonActivity(String personID) {
            Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
            intent.putExtra(PersonActivity.EXTRA_PERSON, personID);
            startActivity(intent);
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private static class FamilyMember {

        public enum Relation { FATHER, MOTHER, SPOUSE, CHILD }

        private Relation relation;

        private Person person;

        FamilyMember(Person person, Relation relation) {
            this.person = person;
            this.relation = relation;
        }

        public Relation getRelation() {
            return relation;
        }

        public Person getPerson() {
            return person;
        }
    }
}