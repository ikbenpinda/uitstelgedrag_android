package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenterImpl;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.nlopez.smartlocation.SmartLocation;

public class TaskActivity extends Base {

    Context       context;
    List<Task>    tasks;
    TaskAdapter   adapter;
    TaskPresenter presenter;

    @BindView(R.id.AddTaskButton)       Button       AddTaskButton;
    @BindView(R.id.MainList)            RecyclerView list;
    @BindView(R.id.TaskIsPlanned)       CheckBox     planTaskCheckbox;
    @BindView(R.id.imageButton)         ImageButton addLocationButton;
    @BindView(R.id.TaskIsPlannedFor)    Spinner      planTaskSpinner;
    @BindView(R.id.task_labels_layout)  LinearLayout task_labels_Layout;
    @BindView(R.id.task_filter_spinner) Spinner      category_spinner;
    @BindView(R.id.AddTaskCategoryAuto) AutoCompleteTextView labelsview;

    AlertDialog waitingForLocationDialog;

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

        // Prepares dialogs.
        final View waitingForLocationsDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_waitingforlocation, null);
        waitingForLocationDialog = new AlertDialog.Builder(this).setView(waitingForLocationsDialogView)
                .setTitle("Wachten op locatie...")
                .setView(waitingForLocationsDialogView)
                .create();

        // CATEGORY(LOCATION)
        String[] categories = new String[]{"Nieuwe locatie...", "thuis(antoniusstraat)", "school(rachelmolen)"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        labelsview.setAdapter(categoryAdapter);
        labelsview.setOnFocusChangeListener((view, focused) -> {
            if (focused && labelsview.getText().toString().length() == 0)
                    labelsview.showDropDown();
        });
        // Typical listener doesn't work for softkeyboards.
        labelsview.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_COMMA){
                Log.i("KeyLogger", "Comma entered.");
            }
            return true;
        });

        adapter = new TaskAdapter(tasks, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        SmartLocation.with(this).location().oneFix().start(location -> {
            Log.i("LocationManager", "current loc=" + location.toString());
//            for (Task task :
//                    TaskGateway.filterByLocation(tasks, location)) {
                // todo - set background color of cell view.
            adapter.tasks.clear();
            adapter.notifyDataSetChanged();
            for (Task task : TaskGateway.filterByLocation(tasks, location)) {
                adapter.addItem(adapter.getItemCount(), task);
            }
//            adapter = new TaskAdapter(locatedTasks, this);
//            }
        });
    }

    @OnClick(R.id.AddTaskButton) void addTask(View v){
        EditText    descriptionView = (EditText) findViewById(R.id.AddTaskDescription);
        descriptionView.requestFocus();

        if (descriptionView.getText().toString().isEmpty()) {
            descriptionView.setError("Taak kan niet leeg zijn!");
            return;
        }

        List<Task> new_tasks = new ArrayList<>();

        // Split tasks on commas
        String[] descriptions = descriptionView.getText().toString().split(",");
        String labels = labelsview.getText().toString();
        Date deadline = planTaskCheckbox.isChecked() ? parseDate() : null;

        for (String description : descriptions) {
            description = description.trim();
            Task task = new Task(description);
            if (!labels.isEmpty()) // note - Labels are optional, descriptions are not.
                for (String string : labels.split(",")) {
                    Label label = new Label();
                    label.title = string;
                    task.labels.add(label);
                    TextView labelView = new TextView(context);
                    labelView.setBackgroundColor(Color.argb(200, 255, 150, 200));
                    labelView.setText(label.title);
                    labelView.setPadding(4, 4, 4, 4);
                    labelView.setTextSize(16);
                    labelView.setClickable(true);
                    labelView.setOnClickListener(
                            view -> {

                            });
                    task_labels_Layout.addView(labelView);
                }

            task.deadline = deadline;

            new_tasks.add(task);

            if (new_tasks.size() > 1){
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < descriptions.length; i++) {
                    builder.append(descriptions[i]);
                    if (i < descriptions.length - 1) {
                        builder.append(", ");
                    }
                }

                Snackbar.make(v, "Taken toegevoegd: " + builder.toString(), Snackbar.LENGTH_SHORT).show();
            }
            else
                Snackbar.make(v, "Taak toegevoegd: " + task.description, Snackbar.LENGTH_SHORT).show();

            descriptionView.setText("");
        }

        for (Task task : new_tasks) {
            presenter.addTask(task);
            adapter.addItem(adapter.getItemCount(), task);
        }

//            task.labels.addAll(Arrays.asList()); FIXME

        // Either clear focus and dismiss the keyboard, or do neither.
        // Otherwise, the keyboard will continue typing while the field is cleared of focus.
        // This will continue focus after addition of an item to make it easier for the user
        //  to add more items at once.
        //desc.clearFocus();
        //dismissKeyboard();
    }

    @OnClick(R.id.imageButton) void showLocationDialog(){
        Task task;
        String[] tags = labelsview.getText().toString().split(",");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tags);
        final View locationView = LayoutInflater.from(this).inflate(R.layout.dialog_addlocation, null);
        AutoCompleteTextView tagsView = (AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags);
        tagsView.setAdapter(categoryAdapter);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(locationView)
                .setPositiveButton("voeg toe", (dialogInterface, i) -> {
                    Label label = new Label();
                    label.title = ((AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags)).getText().toString();
                    SmartLocation.with(this).location().oneFix().start(location -> {
                        label.location = location;
                    });
                    task.labels.add(label);
                })
                .setNegativeButton("laat maar", (dialogInterface, i) -> {})
                .create()
                .show();
    }

    @OnLongClick(R.id.imageButton) boolean addCurrentLocation(){
        waitingForLocationDialog.show();
        SmartLocation
                .with(context)
                .location()
                .oneFix()
                .start(location -> {
                    Log.i("LocationManager", "Location Registered: " + location.toString());
                    waitingForLocationDialog.dismiss();
//                    Snackbar.make(
//                            layout,
//                            "Locatie geregistreerd: " + location.toString(),
//                            Snackbar.LENGTH_SHORT)
//                            .show();
                });
        return true;
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
