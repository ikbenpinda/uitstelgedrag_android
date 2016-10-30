package achan.nl.uitstelgedrag.ui.activities;

import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Default toolbar implementation.
 *
 * Created by Etienne on 12-8-2016.
 */
public class ToolbarDecoration implements Decoration{

    Base activity;

    public ToolbarDecoration(Base activity) {
        this.activity = activity;
    }

    @Override
    public void decorate() {
        Toolbar toolbar = activity.getToolbar();
        if (toolbar == null) {
            Log.w("ToolbarDecoration", "No toolbar found. Are you sure it's bound?");
            return;
        }

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(activity.getCurrentActivity().name);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
    }
}
