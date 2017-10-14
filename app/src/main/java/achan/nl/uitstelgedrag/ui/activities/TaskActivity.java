package achan.nl.uitstelgedrag.ui.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import achan.nl.uitstelgedrag.widget.WidgetProvider;
import achan.nl.uitstelgedrag.widget.WidgetService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TaskActivity extends Base {

    private static final int REQUEST_CODE = 1; // Arbitrary activity-identifying permission-request code.

    @BindView(R.id.task_coord)          CoordinatorLayout   coordlayout;
    @BindView(R.id.AddTaskButton)       Button              AddTaskButton;
    @BindView(R.id.MainList)            TaskRecyclerView    list;
    @BindView(R.id.emptyListView)       TextView            emptyView;
    @BindView(R.id.TaskIsPlanned)       CheckBox            planTaskCheckbox;
    @BindView(R.id.imageButton)         ImageButton         addLocationButton;
    @BindView(R.id.TaskIsPlannedFor)    Spinner             planTaskSpinner;
    //    @BindView(R.id.task_labels_layout)  LinearLayout      task_labels_Layout;
    @BindView(R.id.task_filter_spinner) Spinner             category_spinner;
    @BindView(R.id.AddTaskCategoryAuto) AppCompatMultiAutoCompleteTextView labelsview;

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

    // Note- provisional implementation of the Chandown engine
    char LOCATION_SIGN = '@';
    char LABEL_SIGN = '#';
    String DELIMITER_SIGN = ","; // note '\n' as alternative?
    int MIN_INPUT = 1;

    TextWatcher locationLabelListener = new TextWatcher() {

        String previous = "";
        boolean changed = false;

        String[] categories = new String[]{};
        Random random = new Random();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // note - do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {

            // note - Prevent an infinite loop by not setting the event flag when the styling is updated.
            if (!previous.equals(charSequence.toString())) {
                previous = charSequence.toString();
                changed = true;
            } else {
                changed = false;
            }

            // todo - filter redundant suggestions already in labelsview.
            List<Label> filteredResults = labeldb.getAll();
            List<String> inter = new ArrayList<>();

            for (int i = 0; i < categories.length; i++)
                categories[i] = categories[i].trim().toLowerCase();

            for (Label label : filteredResults)
                inter.add(label.title.toLowerCase());

            for (Label label : labeldb.getAll())
                if (Arrays.asList(categories).contains(label.title))
                    filteredResults.remove(label);

            ArrayAdapter<Label> suggestionsAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.rowlayout_label, R.id.label_title, filteredResults);
            labelsview.setAdapter(suggestionsAdapter);

            // FIXME - use custom filter for performance and uncoupling from this view.
//            if (charSequence.toString().contains("Huidige locatie") && current == null)
//                addCurrentLocation();

//            for (String s : categories) {
//                if (s == null || s.isEmpty())
//                    return;


            // foreach consecutive letter
            //note - works, but incompatible with delimiter and default autocompletion.
/*              // note - this is already done by MultiAutocompleteTextView + CommaTokenizer.
                String currentText = labelsview.getText().toString();
                int start = currentText.lastIndexOf(DELIMITER_SIGN);
                start = start == -1? 0 : start;
                int length = currentText.substring(start).length();
                List<Label> suggestions = new ArrayList<>();
                Log.i("LabelFilter", "start = " + start);
                for (Label label : labeldb.getAll()) {
                    String typed = currentText.substring(start, length);
                    int end = length > label.title.length()? label.title.length(): length;
                    String partial = label.title.substring(0, end);
                    Log.i("LabelFilter", "Finding match for " + typed + " of " + currentText + " in " + label + "("+ partial +")");
                    // equalling that of a specific label,
                    if (partial.toLowerCase().equals(typed.toLowerCase())){
                        Log.i("LabelFilter", "Match found: " + label.title);
                        suggestions.add(label);
                    }
                }
                // update the list
                Log.i("LabelFilter", "Refreshing and displaying dropdown...");

                categoryAdapter.clear();
                categoryAdapter.addAll(suggestions);
                categoryAdapter.notifyDataSetChanged();

//                ArrayAdapter<Label> adapter2 = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, suggestions);
                ArrayAdapter<Label> adapter2 = new ArrayAdapter<>(getBaseContext(), R.layout.rowlayout_label, R.id.label_title, suggestions);
                labelsview.setAdapter(adapter2);
                labelsview.showDropDown();
*/







/*
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

                            });*/
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (!changed)
                return;

            // note - only trigger this on some events to prevent infinite loop.
            categories = labelsview.getText().toString().split(DELIMITER_SIGN);

            Log.i("Spanner", "categories_size=" + categories.length);

            // Don't span the last label as it might still be edited by the user.
            for (int i = 0; i < categories.length - 1; i++) {
                String label = categories[i].trim();
                Log.i("Spanner", "Spanning label for label '" + label + "'");

                SpannableString spannable = new SpannableString(label);

                int backgroundColor = Color.parseColor("#222222");// todo - set background to auto, random, or user-defined color.
                int foregroundColor = Color.WHITE; // todo - set foreground to black if necessary
                BackgroundColorSpan background = new BackgroundColorSpan(backgroundColor);
                ForegroundColorSpan foreground = new ForegroundColorSpan(foregroundColor);

                spannable.setSpan(background, 0, label.length(), 0);
                spannable.setSpan(foreground, 0, label.length(), 0);

                int st = editable.toString().indexOf(label);
                int en = st + label.length(); // note - indexoutofbounds

                Log.i("Spanner", "About to replace '" + editable.subSequence(st, en).toString() + "' in '" + editable.toString() + "', with '" + label + "'");
                Log.i("Spanner", "Spannable:" + spannable.toString());
                Log.i("Spanner", "Label:" + label);
                Log.i("Spanner", "Editable:" + editable.toString());

//                editable.setSpan(spannable, st, en - 1, 0);
                editable.replace(st, en, spannable, 0, spannable.length()); // note - infinite loop
            }

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

        // Android 6.0 - Check permissions
        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
            },
                    REQUEST_CODE);
        }

        context = getApplicationContext();
        presenter = new TaskPresenterImpl(context);
        tasks = presenter.viewTasks();
        smartLocation = SmartLocation.with(this);
        Log.i("TaskActivity", "# of items: " + tasks.size());


        labelsview.addTextChangedListener(locationLabelListener);
        labeldb = new LabelGateway(this);
        allLabels = labeldb.getAll();

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Label selected = (Label)category_spinner.getSelectedItem();
                Log.i("FILTERS", "onItemSelected: " + selected.toString());

                if (selected.title.equals("Alles")) { // todo- architect a default case.
                    filterTasks(null);
                }
                else {
                    filterTasks(selected);
                }

                // todo - filter by location.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Prepares dialogs.
        final View waitingForLocationsDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_waitingforlocation, null);
        waitingForLocationDialog = new AlertDialog.Builder(this).setView(waitingForLocationsDialogView)
                .setTitle("Wachten op locatie...")
                .setView(waitingForLocationsDialogView)
                .create();

        setFilterOptions();

        // note - Applies to the category EditText at the bottom of the layout.
        categoryAdapter = new LabelAdapter(this, R.layout.rowlayout_label, allLabels);

        labelsview.setAdapter(categoryAdapter);
        labelsview.setThreshold(MIN_INPUT);
        labelsview.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        labelsview.setOnFocusChangeListener((view, focused) -> {
            if (focused /*&& labelsview.getText().toString().length() == 0*/)
                    labelsview.showDropDown();
        });

        // Typical listener doesn't work for softkeyboards.
//        labelsview.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_COMMA){
//                Log.i("KeyLogger", "Comma entered.");
//            }
//            return true;
//        });


        adapter = new TaskAdapter(tasks, context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        list.setEmptyView(emptyView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length < 1){
            // permission not granted
            Log.wtf("Permissions", "Fatal permission not granted.");
            Intent intent = new Intent(this, Overview.class);
            startActivity(intent);
        }
    }

    private void setFilterOptions(){

        List<Label> spinner_labels = new ArrayList<>();

        Label label = new Label(); // todo - sensible presets?
        label.title = "Alles"; // fixme dynamic language, code smell

        spinner_labels.add(0, label);
        spinner_labels.addAll(labeldb.getAll());
        ArrayAdapter<Label> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);
    }

    private void filterTasks(Label label){//// FIXME: 7-10-2017 list stays outdated
        List<Task> results = new ArrayList<>();
        if (label == null){
            results = presenter.viewTasks();
        }
        else {
            for (Task task : presenter.viewTasks()) {
                Log.i("Filter", "Labels for task:" + task.labels.get(0).toString());
                if (task.labels.contains(label))
                    results.add(task);
            }
        }

        for (Task task : results){
            Log.i("Filter", "Results: " + task.labels.get(0));
        }

        adapter.tasks = results;
        list.getAdapter().notifyDataSetChanged(); // FIXME/VERIFY - More efficient solutions?
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
        // update widget
        Log.i("Widget", "Triggering widget update...");
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(WidgetProvider.FORCE_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
        Log.i("Widget", "Triggering widget update...broadcast sent.");
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
        List<Label> processedLabels = new ArrayList<>();
        String[] unprocessedLabels = labelsview.getText().toString().split(",");
        for (String unprocessedLabel : unprocessedLabels) {
            Label processedLabel = new Label();
            processedLabel.title = unprocessedLabel;

//            for (Label label : allLabels) {
//                if (label.title.equals(unprocessedLabel))
//                    processedLabel = label;
//                else
//                    labeldb.insert(label);
//            }

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
                    Log.i("TaskActivity", "Adding processed labels...");
                    Log.i("TaskActivity", "label id: " + label.id);

//                    label.id = labeldb.

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

            // fill buffer with new tasks
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
            presenter.addTask(task); // FIXME: 3-10-2017 - SQLiteConstraintException: UNIQUE constraint failed.
            setFilterOptions();
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
