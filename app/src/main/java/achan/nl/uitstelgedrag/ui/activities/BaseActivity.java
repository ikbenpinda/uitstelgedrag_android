package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.LinkedHashMap;
import java.util.Map;

import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    String[]              draweritems;
    ActionBarDrawerToggle DrawerToggle;

    //@BindView(R.id.settings_menuitem) MenuItem      settingsMenuItem; // FIXME: unnecessary view?
    //@BindView(R.id.drawer_layout)     DrawerLayout  drawer;
    //@BindView(R.id.left_drawer)       ListView      drawerlist;         // FIXME: 21-5-2016 recview
    //@BindView(R.id.navigation_view)NavigationView navigationView;
    @BindView(R.id.toolbar)           Toolbar       toolbar;

    /**
     * Returns the layout id of the current activity.
     * @return the layout id of the activity extending BaseActivity or its children.
     */
    @LayoutRes abstract int getLayoutResource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);

        final Map<String, Class> mapping = new LinkedHashMap<>();
        mapping.put("Overzicht",       OverviewActivity.class);
        mapping.put("Planner",       DayplannerActivity.class);
        mapping.put("Taken",            TaskActivity.class);
        mapping.put("Aanwezigheid",       AttendanceActivity.class);
        //mapping.put("Help",                 HelpActivity.class); TODO
        mapping.put("Instellingen",         SettingsActivity.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem   overzicht = new PrimaryDrawerItem().withName("Overzicht");
        PrimaryDrawerItem   planner = new PrimaryDrawerItem().withName("Planner");
        PrimaryDrawerItem   taken = new PrimaryDrawerItem().withName("Taken");
        PrimaryDrawerItem   aanwezigheid = new PrimaryDrawerItem().withName("Aanwezigheid");
        SecondaryDrawerItem help = (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Help");
        SecondaryDrawerItem instellingen = (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Instellingen");

        //create the drawer and remember the `Drawer` result object
        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        overzicht,
                        planner,
                        taken,
                        aanwezigheid,
                        new DividerDrawerItem(),
                        help,
                        instellingen

                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    Log.i("Drawer", "Item clicked!");
                    startActivity(new Intent(getBaseContext(),
                            mapping.get(((PrimaryDrawerItem)drawerItem).getName().getText()))
                    );
                    return true;
                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
