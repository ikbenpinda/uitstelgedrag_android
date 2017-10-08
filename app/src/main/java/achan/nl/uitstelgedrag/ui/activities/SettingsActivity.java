package achan.nl.uitstelgedrag.ui.activities;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.persistence.Settings;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.ui.Themer;
import achan.nl.uitstelgedrag.ui.Themes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static achan.nl.uitstelgedrag.ui.Themes.AUTO;
import static achan.nl.uitstelgedrag.ui.Themes.DARK;
import static achan.nl.uitstelgedrag.ui.Themes.LIGHT;

/**
 * Shows basic and advanced settings.
 */
public class SettingsActivity extends Base { // todo sharedpreferences

    @BindView(R.id.settings_semi_automatic_attendance_tracking_switch) Switch  attendanceTrackingSwitch;
    @BindView(R.id.settings_theme_spinner)                             Spinner themeSpinner;
    @BindView(R.id.settings_mic_trigger_spinner)                       Spinner micTriggerSwitch;
    @BindView(R.id.settings_wipe_database_button)                      Button  wipeDatabaseButton;
    @BindView(R.id.settings_gesture_based_interaction_switch)          Switch  enableGesturesButton;

    Activity activity;
    Context context;
    Settings settings;

    @Override
    Activities getCurrentActivity() {
        return Activities.SETTINGS;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        context = this;
        settings = new Settings(this);
        activity = this;
        //set spinner to current theme
        //if current theme is current theme, do nothing
        //else, change.

        Themes theme_current = settings.getTheme() == 0? LIGHT : DARK;
        themeSpinner.setSelection(theme_current.id);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // fixme - default id vs selecteditempos.
//                if(themeSpinner.getSelectedItemPosition() == theme_current.id)
//                    return;

                boolean changed = false;

                Log.i("Settings", "Changed theme! Selector value: " + themeSpinner.getSelectedItemPosition());
                if (themeSpinner.getSelectedItem().toString().equals("light")) {
//                    settings.setTheme(LIGHT.id);
                    changed = Themer.setTheme(activity, LIGHT);
                    Log.i("Settings", "Changed theme to light theme.");
                }
                else if (themeSpinner.getSelectedItem().toString().equals("dark")){
//                    settings.setTheme(DARK.id);
                    changed = Themer.setTheme(activity, DARK);
                    Log.i("Settings", "Changed theme to dark theme.");
                } else {
//                    settings.setTheme(AUTO.id);
                    changed = Themer.setTheme(activity, AUTO);
                    Log.i("Settings", "Changed theme to auto theme.");
                }

                if (changed)
                    recreate(); // note - causes loop
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // todo proximity / accel setting
//    @OnClick(R.id.settings_mic_trigger_spinner) void setMicTriggerMode(){
//        String state = "";
//        Log.i("Settings", "Microphone trigger set to " + state);
//    }
//
//    @OnClick(R.id.settings_gesture_based_interaction_switch) void toggleGestures(){
//        String state = "";
//        Log.i("Settings", "Gestures " + state);
//    }

    @OnClick(R.id.settings_wipe_database_button) void wipeDatabase(){
        new AlertDialog.Builder(context)
                .setTitle("Verwijder alle gegevens")
                .setMessage("Weet je het zeker?")
                .setNegativeButton("Annuleren", null)
                .setPositiveButton("Verwijderen", (dialog, which) -> {
                    UitstelgedragOpenHelper database = new UitstelgedragOpenHelper(context, null);
                    database.wipe();
                })
                .create()
                .show();
    }
    // todo day/night theme

}
