package android.bignerdranch.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;

import Data.DataCache;
import Data.Settings;

public class SettingsActivity extends AppCompatActivity {

    private static Switch mLSL_switch;
    private static Switch mFTL_switch;
    private static Switch mSL_switch;
    private static Switch mFatherSide_switch;
    private static Switch mMotherSide_switch;
    private static Switch mMaleEvents_switch;
    private static Switch mFemaleEvents_switch;
    private static RelativeLayout mLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Settings settings = Settings.getInstance();

        // Wire up widgets
        mLSL_switch = (Switch) findViewById(R.id.lifeStoryLinesSwitch);
        if(settings.isShowLSL()) mLSL_switch.setChecked(true);
        mLSL_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLSL_switch.isChecked()) settings.setShowLSL(true);
                else settings.setShowLSL(false);
                filter();
            }
        });

        mFTL_switch = (Switch) findViewById(R.id.familyLinesSwitch);
        if(settings.isShowFTL()) mFTL_switch.setChecked(true);
        mFTL_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFTL_switch.isChecked()) settings.setShowFTL(true);
                else settings.setShowFTL(false);
                filter();
            }
        });

        mSL_switch = (Switch) findViewById(R.id.spouseLinesSwitch);
        if(settings.isShowSL()) mSL_switch.setChecked(true);
        mSL_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSL_switch.isChecked()) settings.setShowSL(true);
                else settings.setShowSL(false);
                filter();
            }
        });

        mFatherSide_switch = (Switch) findViewById(R.id.fathersSideSwitch);
        if(settings.isShowFatherSide()) mFatherSide_switch.setChecked(true);
        mFatherSide_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFatherSide_switch.isChecked()) settings.setShowFatherSide(true);
                else settings.setShowFatherSide(false);
                filter();
            }
        });

        mMotherSide_switch = (Switch) findViewById(R.id.mothersSideSwitch);
        if(settings.isShowMotherSide()) mMotherSide_switch.setChecked(true);
        mMotherSide_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMotherSide_switch.isChecked()) settings.setShowMotherSide(true);
                else settings.setShowMotherSide(false);
                filter();
            }
        });

        mMaleEvents_switch = (Switch) findViewById(R.id.maleEventsSwitch);
        if(settings.isShowMaleEvents()) mMaleEvents_switch.setChecked(true);
        mMaleEvents_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMaleEvents_switch.isChecked()) settings.setShowMaleEvents(true);
                else settings.setShowMaleEvents(false);
                filter();
            }
        });

        mFemaleEvents_switch = (Switch) findViewById(R.id.femaleEventsSwitch);
        if(settings.isShowFemaleEvents()) mFemaleEvents_switch.setChecked(true);
        mFemaleEvents_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFemaleEvents_switch.isChecked()) settings.setShowFemaleEvents(true);
                else settings.setShowFemaleEvents(false);
                filter();
            }
        });

        mLogout = (RelativeLayout) findViewById(R.id.settingsLogoutRow);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    // Reset data cache
                DataCache.getInstance().clearCache();
                    // Reset settings
                Settings.getInstance().clearSettings();
                    // Launch new main activity
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private void filter() {
        Settings settings = Settings.getInstance();
        settings.filterPersons();
        settings.filterEvents();
        settings.setSettingsChanged(true);
    }


}