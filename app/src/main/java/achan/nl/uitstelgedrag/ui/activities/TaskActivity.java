package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.LabelGateway;
import achan.nl.uitstelgedrag.ui.adapters.LabelAdapter;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenterImpl;
import achan.nl.uitstelgedrag.ui.views.TaskRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

public class TaskActivity extends Base {


    @BindView(R.id.task_coord)
    CoordinatorLayout coordlayout;
    @BindView(R.id.AddTaskButton)
    Button AddTaskButton;
    @BindView(R.id.MainList)
    TaskRecyclerView list;
    @BindView(R.id.emptyListView)
    TextView emptyView;
    @BindView(R.id.TaskIsPlanned)
    CheckBox planTaskCheckbox;
    @BindView(R.id.imageButton)
    ImageButton addLocationButton;
    @BindView(R.id.TaskIsPlannedFor)
    Spinner planTaskSpinner;
    //    @BindView(R.id.task_labels_layout)  LinearLayout      task_labels_Layout;
//    @BindView(R.id.task_filter_spinner) Spinner           category_spinner;
    @BindView(R.id.AddTaskCategoryAuto)
    AutoCompleteTextView labelsview;

    AlertDialog waitingForLocationDialog;

    // reminder - you can use spannable string to fix the label views.

    Context context;
    List<Task> tasks;
    TaskAdapter adapter;
    TaskPresenter presenter;
    LabelGateway labeldb;
    LabelAdapter categoryAdapter;
    List<Label> allLabels;

    boolean filterByLocation = true;

    /**
     * As polled for when this activity starts.
     */
    SmartLocation smartLocation;
    Location current;
    Address current_address;

    /**
     * Keeps the labels in cache until a task is added or all fields are cleared.
     */
    List<Label> cachedlabels;

    // Chndwn
    char LOCATION_SIGN = '@';
    char LABEL_SIGN = '#';
    String DELIMITER_SIGN = ",";
    int MIN_INPUT = 2;

    TextWatcher locationLabelListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // note - do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // FIXME - use custom filter for performance and uncoupling from this view.
//            if (charSequence.toString().contains("Huidige locatie") && current == null)
//                addCurrentLocation();
            String[] categories = charSequence.toString().split(DELIMITER_SIGN);
            for (String s : categories) {
                if (s == null || s.isEmpty())
                    return;

                if (s.charAt(0) == LABEL_SIGN & s.length() > MIN_INPUT){
                    Label label1 = new Label();
                    label1.title = "thuis1";
                    Label label2 = new Label();
                    label2.title = "werk2";
                    Label label3 = new Label();
                    label3.title = "school3";
                    Handler handler = new Handler(Looper.getMainLooper());  //  Fixes IllegalStateException: redraw while calculating layout
                    handler.post(() -> {
                        categoryAdapter.clear();
                        categoryAdapter.insert(label1, 0);
                        categoryAdapter.insert(label2, 1);
                        categoryAdapter.insert(label3, 2);
                    });
                }
                else if (s.charAt(0) == LOCATION_SIGN & s.length() > MIN_INPUT)
                    smartLocation
                            .geocoding()
                            .direct(s.substring(1), (s1, results) -> {
                                // name is the same you introduced in the parameters of the call
                                // results could come empty if there is no match, so please add some checks around that
                                // LocationAddress is a wrapper class for Address that has a Location based on its data
                                if (results.size() > 0) {
                                    Location suggestion = results.get(0).getLocation();
                                    Address suggestion_address = results.get(0).getAddress();
                                    Log.i("Location Services", "Geocoding: Suggestion found: " + suggestion.toString() + " / " + suggestion_address.toString());

                                    smartLocation
                                            .geocoding()
                                            .reverse(suggestion, (location, results2) -> {
                                        if (results2.size() > 0) { // NOTE - you want current, locality, and specific address.
                                            Address suggested_address = results2.get(0);
                                            Log.i("Location Services", "Reverse geocoding: Suggestion found: " + suggested_address.toString());

                                            Label current_label = new Label();
                                            current_label.location = current;
                                            current_label.title = "Huidige locatie";
                                            current_label.description = current_address.getThoroughfare() + ", " + current_address.getLocality();

                                            Label suggested_label = new Label();
                                            suggested_label.location = location;
                                            suggested_label.title = suggested_address.toString();

                                            Label suggested_locality_label = new Label();
                                            suggested_locality_label.location = location;
                                            suggested_locality_label.title = suggested_address.getLocality();
                                            suggested_locality_label.description = "plaats";

                                            Label suggested_address_label = new Label();
                                            suggested_address_label.location = location;
                                            suggested_address_label.title = suggested_address.getThoroughfare();
                                            suggested_address_label.description = "adres";
                                            categoryAdapter.clear();
                                            categoryAdapter.insert(current_label, 0);
                                            categoryAdapter.insert(suggested_locality_label, 1);
                                            categoryAdapter.insert(suggested_address_label, 2);

                                        } else
                                            Log.w("Location Services", "Reverse geocoding: Nothing found!");
                                    });
                                    // [...] Do your thing! :D
                                } else
                                    Log.w("Location Services", "Geocoding: Nothing found!");

                            });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // note - do nothing, maybe filter for commas or something.
        }
    };

    @Override
    Activities getCurrentActivity() {
        return Activities.TASKS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new TaskPresenterImpl(context);
        tasks = presenter.viewTasks();
        smartLocation = SmartLocation.with(this);
        Log.i("TaskActivity", "# of items: " + tasks.size());

        labelsview.addTextChangedListener(locationLabelListener);
        labeldb = new LabelGateway(this);
        allLabels = labeldb.getAll();

//        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (category_spinner.getSelectedItem().toString().equals("alles")) {
//                    Log.w("FILTERS", "Filtering enabled!");
//                    filterByLocation = false;
//                }
//                else {
//                    Log.w("FILTERS", "Filtering disabled!");
//                    filterByLocation = true;
//                }
//
//                filterItems();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        // Prepares dialogs.
        final View waitingForLocationsDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_waitingforlocation, null);
        waitingForLocationDialog = new AlertDialog.Builder(this).setView(waitingForLocationsDialogView)
                .setTitle("Wachten op locatie...")
                .setView(waitingForLocationsDialogView)
                .create();

        // CATEGORY(LOCATION) - todo - can also be a list. - currently mocked, fixme.
        List<Label> labels = new ArrayList<>();

        Label label1 = new Label();
        label1.title = "thuis";
        Label label2 = new Label();
        label2.title = "werk";
        Label label3 = new Label();
        label3.title = "school";
//        Label label4 = new Label();
//        label4.title = "oma";
//        Label label5 = new Label();
//        label5.title = "pap";
//        Label label6 = new Label();
//        label6.title = "mam";
//        Label label7 = new Label();
//        label7.title = "familie";
//        Label label8 = new Label();
//        label8.title = "vrienden";
//        Label label9 = new Label();
//        label9.title = "collegas";
//        Label label10 = new Label();
//        label10.title = "Huidige locatie";
//        Label label11 = new Label();
//        label11.title = "Nieuwe locatie";

        labels.add(label1);
        labels.add(label2);
        labels.add(label3);
//        labels.add(label4);
//        labels.add(label5);
//        labels.add(label6);
//        labels.add(label7);
//        labels.add(label8);
//        labels.add(label9);
//        labels.add(label10);
//        labels.add(label11);

        categoryAdapter = new LabelAdapter(this, R.layout.rowlayout_label, labels);

        labelsview.setAdapter(categoryAdapter);
        labelsview.setOnFocusChangeListener((view, focused) -> {
            if (focused && labelsview.getText().toString().length() == 0)
                    labelsview.showDropDown();
        });

        // Typical listener doesn't work for softkeyboards.
//        labelsview.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_COMMA){
//                Log.i("KeyLogger", "Comma entered.");
//            }
//            return true;
//        });


//        filterItems();
        adapter = new TaskAdapter(tasks, context);
        adapter.setOnListChangedListener(() -> checkListSize());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        list.setEmptyView(emptyView);

//        checkListSize();
    }

    public final int VIEW_LIST = 0;
    public final int VIEW_EMPTY = 1;

    private void checkListSize() {

        // FIXME set view _before_ adding shit.

//        if (tasks.isEmpty())
//            listSwitcher.setDisplayedChild(VIEW_EMPTY);
//        else
//            listSwitcher.setDisplayedChild(VIEW_LIST); // fixme - inconsistency detected
    }

    private void filterItems(){
//
//        ArrayList new_items = new ArrayList();
//        if (filterByLocation == false){
////            adapter.tasks.clear();
////            list.getRecycledViewPool().clear();
////            adapter.notifyDataSetChanged();
//            for (Task task : tasks) {
//                new_items.add(task);
////                adapter.addItem(adapter.getItemCount(), task);
//                Log.w("FILTERS", "Added unfiltered item:" + task.toString());
//            }
//        } else {
////            adapter.tasks.clear();
////            list.getRecycledViewPool().clear();
////            adapter.notifyDataSetChanged();
//            for (Task task : tasks) {
//                for (Label label : task.labels) {
//                    if (label.title.equals("thuis")) { // Demo hack.
//                        new_items.add(task);
////                        adapter.addItem(adapter.getItemCount(), task);
//                        Log.w("FILTERS", "Added filtered item for thuis:" + task.toString());
//                    }
//                }
//            }
//            tasks = new_items;
//            // Get location and show a new list of location-relevant items.
//            //SmartLocation.with(this).location().oneFix().start(location -> {
//                //Log.i("LocationManager", "current loc=" + location.toString());
////            for (Task task :
////                    TaskGateway.filterByLocation(tasks, location)) {
//                // todo - set background color of cell view.
//                //adapter.tasks.clear();
//                //adapter.notifyDataSetChanged();
//                //for (Task task : TaskGateway.filterByLocation(tasks, location)) {
//                    //adapter.addItem(adapter.getItemCount(), task);
//                //}
////            adapter = new TaskAdapter(locatedTasks, this);
////            }
//            //});
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        waitingForLocationDialog.show();
        smartLocation
                .location()
                .config(LocationParams.NAVIGATION)
                .oneFix()
                .start(location -> {
                    Log.i("Location Services", "Location Registered: " + location.toString());
                    current = location;
                    smartLocation
                            .geocoding()
                            .reverse(location, (location1, list1) -> {
                        if (list1.size() > 0)
                            current_address = list1.get(0);
                        Log.i("Location Services", "Current location: " + location.toString() + " / " + current_address.toString());
                    });
//                    waitingForLocationDialog.dismiss();
//                    final EditText view = new EditText(context);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Naam voor locatie")
//                            .setView(view)
//                            .setPositiveButton("OK", (dialogInterface, i) -> {
//                                Label label = new Label();
//                                label.location = location;
//                                label.title = view.getText().toString();
//                                Snackbar.make(
//                                        v,
//                                        "Locatie geregistreerd: " + location.toString(),
//                                        Snackbar.LENGTH_SHORT)
//                                        .show();
//                            })
//                            .setNegativeButton("", (dialogInterface, i) -> {
//                                // nothing to do here, the view will kill itself.
//                            }).create().show();
                });
    }

    @OnClick(R.id.AddTaskButton) void addTask(View v){

        // FIXME - Inconsistency detected.
        // note - happens when list is not already shown when adding
        // note - activity list vs adapter list.

        //if (listSwitcher.view)
//        listSwitcher.setDisplayedChild(VIEW_LIST);
        EditText    descriptionView = (EditText) findViewById(R.id.AddTaskDescription);
        descriptionView.requestFocus();

        if (descriptionView.getText().toString().trim().isEmpty()) { // todo - espresso
            descriptionView.setError("Taak kan niet leeg zijn!");
            return;
        }

        // Check all labels for location.
        // If matches, use those instead.
        List<Task> new_tasks = new ArrayList<>();
        String[] unprocessedLabels = labelsview.getText().toString().split(",");
        List<Label> processedLabels = new ArrayList<>();
        for (String unprocessedLabel : unprocessedLabels) {
            Label processedLabel = new Label();
            processedLabel.title = unprocessedLabel;

            for (Label label : allLabels) {
                if (label.title.equals(unprocessedLabel))
                    processedLabel = label;
                else
                    labeldb.insert(null, label);
            }

            processedLabels.add(processedLabel);
        }

        // Split tasks on commas
        String[] descriptions = descriptionView.getText().toString().split(",");
        String labels = labelsview.getText().toString();
        Date deadline = planTaskCheckbox.isChecked() ? parseDate() : null;

        for (String description : descriptions) {
            description = description.trim();
            Task task = new Task(description);
            if (!labels.isEmpty()) // note - Labels are optional, descriptions are not.
                for (Label label : processedLabels) {
                    task.labels.add(label);
//                    TextView labelView = new TextView(context);
//                    labelView.setBackgroundColor(Color.argb(0, 0, 0, 255));
//                    labelView.setText(label.title);
//                    labelView.setTextColor(Color.WHITE);
//                    labelView.setPadding(4, 4, 4, 4);
//                    labelView.setTextSize(16);
//                    labelView.setClickable(true);
//                    labelView.setOnClickListener(
//                            view -> {
//
//                            });
//                    task_labels_Layout.addView(labelView);
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
            adapter.addItem(adapter.getItemCount(), task);
            presenter.addTask(task);
        }

//        filterItems();
//        checkListSize();
//            task.labels.addAll(Arrays.asList()); FIXME

        // Either clear focus and dismiss the keyboard, or do neither.
        // Otherwise, the keyboard will continue typing while the field is cleared of focus.
        // This will continue focus after addition of an item to make it easier for the user
        //  to add more items at once.
        //desc.clearFocus();
        //dismissKeyboard();
    }

    @OnClick(R.id.imageButton) void showLocationDialog(){ // note - switched to autocomplete instead.
//        String[] tags = labelsview.getText().toString().split(",");
//        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_dropdown_item_1line, tags);
//        final View locationView = LayoutInflater.from(this).inflate(R.layout.dialog_addlocation, null);
//        AutoCompleteTextView tagsView = (AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags);
//        tagsView.setAdapter(categoryAdapter);
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setView(locationView)
//                .setPositiveButton("voeg toe", (dialogInterface, i) -> {
//                    Label label = new Label();
//                    label.title = ((AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags)).getText().toString();
//                    SmartLocation.with(this).location().oneFix().start(location -> {
//                        label.location = location;
//                    });
//                    task.labels.add(label);
//                })
//                .setNegativeButton("laat maar", (dialogInterface, i) -> {})
//                .create()
//                .show();
    }

    @OnLongClick(R.id.imageButton) boolean addCurrentLocation(View v){
        waitingForLocationDialog.show();
        smartLocation
                .location()
                .config(LocationParams.NAVIGATION)
                .oneFix()
                .start(location -> {
                    Log.i("LocationManager", "Location Registered: " + location.toString());
                    current = location;
                    waitingForLocationDialog.dismiss();
                    final EditText view = new EditText(context);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Naam voor locatie")
                    .setView(view)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        Label label = new Label();
                        label.location = location;
                        label.title = view.getText().toString();
                    Snackbar.make(
                            v,
                            "Locatie geregistreerd: " + location.toString(),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    })
                    .setNegativeButton("", (dialogInterface, i) -> {
                        // nothing to do here, the view will kill itself.
                    }).create().show();
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
