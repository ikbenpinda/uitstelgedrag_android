package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class Base extends AppCompatActivity implements Decoratable {

//    String[]              draweritems;
//    ActionBarDrawerToggle DrawerToggle;
    List<Decoration> decorations = new ArrayList<>(); // Additional items like drawers, (toolbar)menus, etc.

    //@BindView(R.id.settings_menuitem) MenuItem      settingsMenuItem; // FIXME: unnecessary view?
    //@BindView(R.id.drawer_layout)     DrawerLayout  drawer;
    //@BindView(R.id.left_drawer)       ListView      drawerlist;
    //@BindView(R.id.navigation_view)NavigationView navigationView;
    @Nullable // not all activities have a toolbar.
    @BindView(R.id.toolbar) Toolbar toolbar = null;

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.settings_menuitem) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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
