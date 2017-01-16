package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Dayplan;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
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

        Date                todaysDate   = new Date();
        TaskGateway         taskdatabase = new TaskGateway(this);
        List<Task>          tasks        = taskdatabase.getAll();

        tasks = TaskGateway.sortByPlanned(tasks);
        ArrayList<Task> todaysTasks = new ArrayList<>();
        ArrayList<Task> dayplan = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (Task plan : tasks) {
            if (plan.deadline != null) {
                cal.setTime(plan.deadline);
                int deadline = cal.get(Calendar.DAY_OF_MONTH);
                cal.setTime(todaysDate);
                int today = cal.get(Calendar.DAY_OF_MONTH);
                if (deadline == today)
                    todaysTasks.add(plan);
            }
        }

        // todo - find everything for today at location

        boolean available = tasks.size() > 0;

        if (available){
            switcher.showNext();

            DayplanAdapter      adapter = new DayplanAdapter(new Dayplan(new Date(), todaysTasks), this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            dayplannerList.setAdapter(adapter);
            dayplannerList.setLayoutManager(manager);
        }
    }
}
