package achan.nl.uitstelgedrag.ui.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Shows basic and advanced settings.
 */
public class SettingsActivity extends Base { // todo sharedpreferences

    @BindView(R.id.settings_semi_automatic_attendance_tracking_switch) Switch  attendanceTrackingSwitch;
    @BindView(R.id.settings_theme_spinner)                             Spinner  themeSpinner;
    @BindView(R.id.settings_mic_trigger_spinner)                       Spinner micTriggerSwitch;
    @BindView(R.id.settings_wipe_database_button)                      Button  wipeDatabaseButton;
    @BindView(R.id.settings_gesture_based_interaction_switch)          Switch enableGesturesButton;

    Context context;

    @Override
    Activities getCurrentActivity() {
        return Activities.SETTINGS;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        context = this;
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

    @OnClick(R.id.settings_wipe_database_button) void wipeDatebase(){
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
