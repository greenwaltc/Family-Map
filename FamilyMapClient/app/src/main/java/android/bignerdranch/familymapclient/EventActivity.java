package android.bignerdranch.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class EventActivity extends AppCompatActivity {

    private static String mEventID;

    private static String ARG_EVENT_ID = "event-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(ARG_EVENT_ID)) {
                mEventID = intent.getStringExtra(ARG_EVENT_ID);
            } else return;
        } else return;

        Bundle bundle = new Bundle();
        bundle.putString(ARG_EVENT_ID, mEventID);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.event_activity_fragment_container);

        if (fragment == null) {
            fragment = new MapFragment();
            fragment.setArguments(bundle); // getArguments();
            fm.beginTransaction()
                    .add(R.id.event_activity_fragment_container, fragment)
                    .commit();
        }
    }
}




























