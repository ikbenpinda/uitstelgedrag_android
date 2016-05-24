package achan.nl.uitstelgedrag.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    String[] draweritems;
    ActionBarDrawerToggle mDrawerToggle;

    //@BindView(R.id.settings_menuitem) MenuItem      settingsMenuItem; // FIXME: unnecessary view?
    @BindView(R.id.drawer_layout)     DrawerLayout  drawer;
    @BindView(R.id.left_drawer)       ListView      drawerlist;         // FIXME: 21-5-2016 recview
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Use the constructor with the toolbar animation to fix navigation issues.
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.draweraccessopen, R.string.draweraccessclose)
        {

            public void onDrawerClosed(View view)
            {
                supportInvalidateOptionsMenu();
                //drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView)
            {
                supportInvalidateOptionsMenu();
                //drawerOpened = true;
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // https://medium.com/@ngdelamo/using-drawerlayout-the-material-way-i-716bba2b5705#.yeqv0jln5
        drawer.setStatusBarBackground(R.color.colorPrimary);

        draweritems = new String[]{
                "Overzicht",
                "Dagplanner",
                "Taken",
                "Aanwezigheid",
                "Instellingen"
        };

        // Set the adapter for the list view
        drawerlist.setAdapter(new ArrayAdapter<>(getBaseContext(),
                R.layout.drawer_item, R.id.drawer_item, draweritems));

        // Set the list's click listener
        drawerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class target;

                switch (draweritems[position]){
                    case "Overzicht":
                            Log.i("DRAWER", "Taken");
                            target = OverviewActivity.class;
                        break;
                    case "Dagplanner":
                            Log.i("DRAWER", "Taken");
                            target = DayplannerActivity.class;
                        break;
                    case "Taken":
                            Log.i("DRAWER", "Taken");
                            target = TaskActivity.class;
                        break;
                    case "Aanwezigheid":
                            Log.i("DRAWER", "Aanwezigheid");
                            target = AttendanceActivity.class;
                        break;
                    case "Instellingen":
                            Log.i("DRAWER", "Instellingen");
                            target = SettingsActivity.class;
                        break;
                    default:
                        Log.i("DRAWER", "" + position);
                        return;
                }

                startActivity(new Intent(getBaseContext(), target));
            }
        });

        // do not set contentview in children?

//        settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Log.i("BaseActivity", "MenuItem called!");
//                return false;
//            }
//        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

}
