package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ViewSwitcher;

import java.util.Date;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Dayplan;
import achan.nl.uitstelgedrag.persistence.DayplanRepository;
import achan.nl.uitstelgedrag.persistence.gateways.DayplanGateway;
import achan.nl.uitstelgedrag.ui.adapters.DayplanAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Dayplanner extends Base {

    @BindView(R.id.vs_dayplanning)
    ViewSwitcher switcher;
    @BindView(R.id.dayplanner_list)
    RecyclerView dayplannerList;

    @Override
    Activities getCurrentActivity() {
        return Activities.DAYPLANNER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Date                today    = new Date();
        DayplanRepository   dayplans = new DayplanGateway(this);
        Dayplan             plan     = dayplans.get(today);

        int size = plan != null? plan.tasks.size(): 0;
        Log.i("Dayplanner", "plan(" + size + " items) = " + plan);

        boolean available = size > 0;

        if (available){
            switcher.showNext();

            DayplanAdapter      adapter = new DayplanAdapter(plan, this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            dayplannerList.setAdapter(adapter);
            dayplannerList.setLayoutManager(manager);
        }
    }
}
