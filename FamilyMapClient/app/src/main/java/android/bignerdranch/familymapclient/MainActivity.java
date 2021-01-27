package android.bignerdranch.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import Data.DataCache;
import Data.Settings;

public class MainActivity extends AppCompatActivity implements
        LoginRegisterFragment.Listener{

    public static final String EXTRA_IS_LOGGED_IN =
            "com.android.familymapclient.is_logged_in";
    private static String ARG_EVENT_ID = "event-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new LoginRegisterFragment(this);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onSuccessfulLogin() {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        fragment = MapFragment.newInstance();
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This will reset all the events markers if settings were changed.
        Settings settings = Settings.getInstance();
        if (settings.isSettingsChanged()) {

            // Reset settings changed to false
            settings.setSettingsChanged(false);

            // Start map fragment again
            Bundle bundle = new Bundle();
            bundle.putString(ARG_EVENT_ID,
                    DataCache.getInstance().getSelectedEvent().getEventID());

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);

            fragment = MapFragment.newInstance();
            fragment.setArguments(bundle);
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}