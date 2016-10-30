package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenterImpl;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends Base {

    Context       context;
    List<Task>    tasks;
    TaskAdapter   adapter;
    TaskPresenter presenter;

    @BindView(R.id.AddTaskButton)   Button       AddTaskButton;
    @BindView(R.id.MainList)        RecyclerView list;
    @BindView(R.id.TaskIsPlanned)   CheckBox     planTaskCheckbox;
    @BindView(R.id.TaskIsPlannedFor)Spinner      planTaskSpinner;

    @Override
    Activities getCurrentActivity() {
        return Activities.TASKS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext(); // FIXME use BaseContext/this?
        presenter = new TaskPresenterImpl(context);
        tasks = presenter.viewTasks();

        adapter = new TaskAdapter(tasks, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
    }

    @OnClick(R.id.AddTaskButton) void addTask(View v){
        EditText    labelsView  = (EditText) findViewById(R.id.AddTaskCategory);
        EditText    descriptionView = (EditText) findViewById(R.id.AddTaskDescription);
        descriptionView.requestFocus();

        String description = descriptionView.getText().toString();
        String labels = labelsView.getText().toString();

        Task task = new Task(description);

        if (!labels.isEmpty())
            for (String string : labels.split(",")) {
                Label label = new Label();
                label.title = string;
                task.labels.add(label);
            }
//            task.labels.addAll(Arrays.asList()); FIXME

        task.deadline = planTaskCheckbox.isChecked() ? parseDate() : null;


        presenter.addTask(task);
        adapter.addItem(adapter.getItemCount(), task);
        Snackbar.make(v, "Taak toegevoegd: " + task.description, Snackbar.LENGTH_SHORT).show();

        descriptionView.setText("");

        // Either clear focus and dismiss the keyboard, or do neither.
        // Otherwise, the keyboard will continue typing while the field is cleared of focus.
        // This will continue focus after addition of an item to make it easier for the user
        //  to add more items at once.
        //desc.clearFocus();
        //dismissKeyboard();
    }

    private Date parseDate() {
        Date date = new Date();

        Log.i("TaskActivity", "Parsing date for chosen option " + planTaskSpinner.getSelectedItem().toString());
        switch (planTaskSpinner.getSelectedItem().toString()){
            case "vandaag":
                break;
            case "morgen":
                date = new Date(date.getTime() + Timestamp.DAY_IN_MILLIS);
                break;
            case "volgende week":
                date = new Date(date.getTime() + Timestamp.DAY_IN_MILLIS * 7);
                break;
            default:
                date = null;
                break;
        }

        return date;
    }
}
