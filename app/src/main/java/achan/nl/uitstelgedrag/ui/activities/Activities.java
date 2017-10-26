package achan.nl.uitstelgedrag.ui.activities;

import android.support.annotation.LayoutRes;

import achan.nl.uitstelgedrag.R;

/**
 * Activity data to communicate the activity independent of implementation.
 * <p>
 * Created by Etienne on 8-8-2016.
 */
public enum Activities {// FIXME use string resources!

    /** The dashboard / main activity of the application. */
    OVERVIEW(0, "Overzicht", R.layout.activity_overview, Overview.class),

    /** The attendances overview. */
    ATTENDANCELOG(1, "Urenstaat", R.layout.activity_attendance, Attendance.class),

    /** The planning activity for the day plan. */
    DAYPLANNER(2, "Dagplanner", R.layout.activity_day_planning, Dayplanner.class),

    /** The tasks overview. */
    TASKS(3, "Taken", R.layout.activity_task, TaskActivity.class),

    /** The notes overview. */
    NOTES(7, "Notities", R.layout.activity_note, NoteActivity.class),

    /** The detailview for a specific task. */
    TASK_DETAIL(31, "Details", R.layout.activity_task_detail, TaskDetailActivity.class),

    /** The detailview for a specific task. */
    NOTE_DETAIL(71, "Details", R.layout.activity_note_detail, NoteDetailActivity.class),

    /** The settings menu. */
    SETTINGS(4, "Instellingen", R.layout.activity_settings, SettingsActivity.class),

    /** The (online) manual. */
    HELP(5, "Help", R.layout.activity_help, HelpActivity.class),

    /** About section of the application. Not often used but useful for testing deployments. */
    ABOUT(6, "Over", R.layout.activity_about, AboutActivity.class),

    /** Seperate multi-purpose view for setting a location. */
    SETLOCATION(32, "Locatie", R.layout.activity_set_location, SetLocationActivity.class);


    /** Arbitrary id for the activity. Can be negative. */
    long id;

    /** Name of the activity, to be used for display. */
    String name;

    /** Layout id for the activity. Note: uses the activity, not the content layout. */
    @LayoutRes
    int layout;

    /** The activity class for intent creation. */
    Class activity;

    Activities(long id, String name, @LayoutRes int layout, Class activity) {
        this.id = id;
        this.name = name;
        this.layout = layout;
        this.activity = activity;
    }
}

