package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.persistence.Settings;
import achan.nl.uitstelgedrag.ui.Themes;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class Base extends AppCompatActivity implements Decoratable {

    List<Decoration> decorations = new ArrayList<>(); // Additional items like drawers, (toolbar)menus, etc.

    @Nullable // not all activities have a toolbar.
    @BindView(R.id.toolbar) Toolbar toolbar;

    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Returns an Activities enum with the metadata about the activity so no additional details have to be sent per class.
     * This means a reduction in boilerplate and easier editing of references to layouts / classes.
     *
     * @return the child's activity.
     */
    abstract Activities getCurrentActivity();

    @Override
    public void addDecoration(Decoration decoration) {
        decorations.add(decoration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeno = new Settings(this).getTheme();
        Log.i("Themer", "Themeno: " + themeno);
        if (themeno > 2)
            themeno = 1;
        Themes theme = Themes.values()[themeno];
        // Setting the theme before onCreate() sets the text color.
        Log.w("Themer", "ThemeBase: " + theme.name + "/#" + themeno);
//        Themer.setTheme(this, theme);
        setTheme(theme.style);

        super.onCreate(savedInstanceState);

        setContentView(getCurrentActivity().layout);
        ButterKnife.bind(this);
        // This can be moved to a seperate activity subclass to differentiate between presets,
        // or be left here to provide a default implementation.
        decorations.add(new ToolbarDecoration(this));
        decorations.add(new MaterialDrawerDecoration(this));

        for (Decoration decor : decorations) {
            decor.decorate();
        }

        // todo settheme
//        Themer.setTheme(this, theme);
        setTheme(theme.style); //verify - needed?
        //        toolbar.setPopupTheme(theme == R.style.AppTheme_Light? R.style.AppTheme_AppBarOverlay_Light: R.style.AppTheme_AppBarOverlay);
    }

    @Override
    protected void onResume() { // todo - handle listeners.
        super.onResume();
    } // todo - handle listeners and whatever.

    @Override
    protected void onPause() { // todo - handle listeners.
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menuitem:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.settings_menuitem_help:
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                break;
            case R.id.settings_menuitem_about:
                //just display an alert with the info, don't bother with an activity tbh.
//                AlertDialog.Builder b;
                break;
        }

        return true;
    }

    /**
     * Dismisses the virtual keyboard, regardless of where you at.
     */
    public void dismissKeyboard(){
        //http://stackoverflow.com/questions/3553779/android-dismiss-keyboard
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
