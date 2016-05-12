package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import achan.nl.uitstelgedrag.R;

public abstract class BaseActivity extends AppCompatActivity {

    // TODO: 29-4-2016 Title visibility checking

    Context context;
    MenuItem settingsMenuItem;

    public BaseActivity(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // do not set contentview in children?
        settingsMenuItem = (MenuItem) findViewById(R.id.settings_menuitem);
        settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("BaseActivity", "MenuItem called!");
                return false;
            }
        });
    }

}
