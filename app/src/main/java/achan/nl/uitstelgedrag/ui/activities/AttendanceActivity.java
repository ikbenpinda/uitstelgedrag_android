package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.ui.adapters.AttendanceAdapter;

public class AttendanceActivity extends AppCompatActivity {

    UitstelgedragOpenHelper database;
    List<Timestamp> timestamps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        database = new UitstelgedragOpenHelper(this, null);
        timestamps = database.getTimestamps();

        AttendanceAdapter adapter = new AttendanceAdapter(timestamps, this);
        LinearLayoutManager manager    = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView        lv         = (RecyclerView) findViewById(R.id.attendances_lv);
        lv.setLayoutManager(manager);
        lv.setAdapter(adapter);
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }
}
