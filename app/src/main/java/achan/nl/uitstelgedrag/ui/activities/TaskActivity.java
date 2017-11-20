package achan.nl.uitstelgedrag.ui.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mikepenz.iconics.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.ChanDownParser;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;
import achan.nl.uitstelgedrag.persistence.gateways.LabelGateway;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
import achan.nl.uitstelgedrag.ui.adapters.LabelAdapter;
import achan.nl.uitstelgedrag.ui.adapters.LabelSpinnerAdapter;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenter;
import achan.nl.uitstelgedrag.ui.presenters.TaskPresenterImpl;
import achan.nl.uitstelgedrag.ui.views.ColorPicker;
import achan.nl.uitstelgedrag.ui.views.TaskRecyclerView;
import achan.nl.uitstelgedrag.widget.WidgetProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TaskActivity extends Base {

    // note/pseudo - task filtering

    // get user location
    // get list of tasks
    // sort by label on most nearby
    // sort location-less items either at the top or bottom / VERIFY

    // allow the user to smoothly transition to his own filtering
    // timeout on location-measuring

    // note/pseudo - default tasks usage:

    // kies label
    // voeg label toe
    // locatie instellen
    // locatie toevoegen aan label
    // kleur instellen
    // kleur toevoegen aan label
    // voeg gekozen labels toe aan taak
    // kies deadline
    // voeg gekozen deadline aan taak toe
    // voeg taak toe aan lijst.

    private static final int REQUEST_CODE = 1; // Arbitrary activity-identifying permission-request code.

    private static final String DEFAULT_LIST_TITLE = "Alles"; // fixme - use localized resources.getstring, and check casing!

    @BindView(R.id.task_coord)          CoordinatorLayout   coordlayout;
    @BindView(R.id.AddTaskButton)       Button              AddTaskButton;
    @BindView(R.id.MainList)            TaskRecyclerView    list;
    @BindView(R.id.emptyListView)       TextView            emptyView;
    @BindView(R.id.TaskIsPlanned)       CheckBox            planTaskCheckbox;
    @BindView(R.id.imageButton)         ImageButton         addLocationButton;
    @BindView(R.id.TaskIsPlannedFor)    Spinner             planTaskSpinner;
    //    @BindView(R.id.task_labels_layout)  LinearLayout      task_labels_Layout;
    @BindView(R.id.task_filter_spinner) Spinner             category_spinner;
    @BindView(R.id.til_labelview)       TextInputLayout     tilLabelView;
    @BindView(R.id.til_add_task_description)        TextInputLayout tilAddTaskDescription;
    @BindView(R.id.AddTaskCategoryAuto) AppCompatMultiAutoCompleteTextView labelsview;
//    @BindView(R.id.provisional_label_color_picker) LinearLayout ll;
    @BindView(R.id.tv_add_label_vs_trigger) TextView viewSwitcherTrigger;
    @BindView(R.id.vs_add_label_view)   ViewSwitcher viewSwitcher;
    //    @BindView(R.id.bottomsheet)         View bottomsheet;
//    @BindView(R.id.bottomsheet_add_task) TextView bottomsheetAddTask;
//    @BindView(R.id.invis_atv)            LinearLayout invisible_addtaskview_layout;
    @BindView(R.id.btn_add_label)       Button addLabelButton;
    @BindView(R.id.task_labels_list)    LinearLayout tasklabelslist;
    @BindView(R.id.edit_label_btn)      TextView editLabelButton;

    @BindView(R.id.colorpicker) ColorPicker colorPicker;
// note - replaced with custom view above.
//    @BindView(R.id.provisional_label_color_picker_card_00) CardView colorcard00;
//    @BindView(R.id.provisional_label_color_picker_card_0A) CardView colorcard0A;
//    @BindView(R.id.provisional_label_color_picker_card_0B) CardView colorcard0B;
//    @BindView(R.id.provisional_label_color_picker_card_0C) CardView colorcard0C;
//    @BindView(R.id.provisional_label_color_picker_card_0D) CardView colorcard0E;
//    @BindView(R.id.provisional_label_color_picker_card_0E) CardView colorcard0D;
//    @BindView(R.id.provisional_label_color_picker_card_0F) CardView colorcard0F;
//    @BindView(R.id.     provisional_label_color_picker_card_0G) CardView colorcard0G;
//    @BindView(R.id.provisional_label_color_picker_card_0H) CardView colorcard0H;

    int selectedColor = 0;

    AlertDialog waitingForLocationDialog;
//    BottomSheetBehavior bottomSheetBehavior;

    // reminder - you can use spannable string to fix the label views.

    Context context;
    List<Task> tasks;
    TaskAdapter adapter;
    TaskPresenter presenter;
    LabelGateway labeldb;
    ChanDownParser parser = new ChanDownParser();
    LabelAdapter categoryAdapter;
    List<Label> allLabels;

    Label templateLabel;
    achan.nl.uitstelgedrag.domain.models.Location lastLocation;

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

    // Fixme - figure out why one of the listeners fucks up autocomplete
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

            // TODO: 25-10-2017 custom view for location
//            ArrayAdapter<Label> suggestionsAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.rowlayout_label, R.id.label_title, filteredResults);
            LabelAdapter suggestionsAdapter = new LabelAdapter(context, R.layout.rowlayout_label, filteredResults);
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
                ClickableSpan   onClickMenu = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Log.i("ClickableSpan", "Item clicked.");
                        View colorpicker = getLayoutInflater().from(context).inflate(R.layout.provisional_color_picker, null);
                    }
                };

                spannable.setSpan(background, 0, label.length(), 0);
                spannable.setSpan(foreground, 0, label.length(), 0);
                spannable.setSpan(onClickMenu, 0, label.length(), 0);

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

    TextWatcher SingleAddLabelListener = new TextWatcher() {

        final int NO_COLOR_SELECTED = -1;

        String previous = "";
        boolean changed = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            // note - Prevent an infinite loop by not setting the event flag when the styling is updated.
            if (!previous.equals(charSequence.toString())) {
                previous = charSequence.toString();
                changed = true;
            } else {
                changed = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (!changed)
                return;

            List<Label> categories = parser.parseLabels(labelsview.getText().toString(), allLabels);

            for (Label label : categories) {
                Log.i("ChanDownParser", label.toString());

                Log.i("Spanner", "categories_size=" + categories.size());
                Log.i("Spanner", "Spanning label for label '" + label + "'");

                boolean colorIsSelected = colorPicker.getSelectedColor() != NO_COLOR_SELECTED;
                boolean labelAlreadyColored = label.color != null && !label.color.isEmpty();

                Log.i("Spanner", "labelAlreadyColored: " + labelAlreadyColored);
                Log.i("Spanner", "colorIsSelected: " + colorIsSelected);
                // Color certain words in the text if these equal colored labels.

                if (labelAlreadyColored | colorIsSelected) {
                    Spannable spannable = createSpannable(label);
                    replaceSpannable(editable, spannable, label);
                }
            }
        }

        private Spannable createSpannable(Label label){

            SpannableString spannable = new SpannableString(label.title);

//                int backgroundColor = Color.parseColor("#222222");// todo - set background to auto, random, or user-defined color.
            boolean labelColorIsSet = label.color != null && !label.color.isEmpty();
            int foregroundColor = labelColorIsSet?
                    Integer.parseInt(label.color) : selectedColor; // todo - set foreground to black if necessary

//                BackgroundColorSpan background = new BackgroundColorSpan(backgroundColor);
            ForegroundColorSpan foreground = new ForegroundColorSpan(foregroundColor);
//                ClickableSpan onClickMenu = new ClickableSpan() {
//                    @Override
//                    public void onClick(View widget) {
//                        Log.i("ClickableSpan", "Item clicked.");
//                        View colorpicker = getLayoutInflater().from(context).inflate(R.layout.provisional_color_picker, null);
//                    }
//                };

//                spannable.setSpan(background, 0, label.length(), 0);
            spannable.setSpan(foreground, 0, label.title.length(), 0);
//                spannable.setSpan(onClickMenu, 0, label.length(), 0);

            return spannable;
        }

        private Editable replaceSpannable(Editable editable, Spannable spannable, Label label){
            int st = editable.toString().indexOf(label.title);
            int en = st + label.title.length(); // note - indexoutofbounds

            Log.i("Spanner", "About to replace '" + editable.subSequence(st, en).toString() + "' in '" + editable.toString() + "', with '" + label.title + "'");
            Log.i("Spanner", "Spannable:" + spannable.toString());
            Log.i("Spanner", "Label:" + label.title);
            Log.i("Spanner", "Editable:" + editable.toString());

            return editable.replace(st, en, spannable, 0, spannable.length()); // note - infinite loop
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

        // Handle intent in case we returned from the label editing screen.
        Intent intent = getIntent();
        ContentValues locationValues = intent.getParcelableExtra("location");
        if (locationValues != null) {
            lastLocation = new achan.nl.uitstelgedrag.domain.models.Location(
                    locationValues.getAsDouble(Locations.LATITUDE.name),
                    locationValues.getAsDouble(Locations.LONGITUDE.name),
                    locationValues.getAsString(Locations.CITY.name),
                    locationValues.getAsString(Locations.ADDRESS.name),
                    locationValues.getAsString(Locations.POSTAL_CODE.name)
            );
            Log.i("TaskActivity", "lastLocation updated by setLocationActivity to " + lastLocation.toString());
        }

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
//
//        bottomsheetAddTask.setOnClickListener(v -> {
//            View dialogview = LayoutInflater.from(this).inflate(R.layout.add_task_view, null);
//            AlertDialog.Builder addtaskdialog = new AlertDialog.Builder(this)
//                    .setTitle("Nieuwe taak")
//                    .setView(dialogview)
//                    .setNegativeButton("annuleren", null)
//                    .setPositiveButton("toevoegen", (dialog, which) -> {
//
//                    });
//            addtaskdialog.create().show();
//        });

//        // note - disabled for now due to insufficient UX improvement.
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
//        bottomsheetAddTask.setOnClickListener(v -> {
//
//            //Note - if you want to use dp: Utils.convertDpToPx(context, height)
//            bottomSheetBehavior.setPeekHeight(bottomsheetAddTask.getHeight());
//            bottomsheetAddTask.requestLayout(); // note - getHeight won't be applied without this call.
//
//            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//            else if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED){
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            }
//        });


        labelsview.addTextChangedListener(SingleAddLabelListener);
        labeldb = new LabelGateway(this);
        allLabels = labeldb.getAll();

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Label selected = (Label)category_spinner.getSelectedItem();

                if(selected.title.equals(DEFAULT_LIST_TITLE))
                    editLabelButton.setVisibility(View.INVISIBLE);
                else
                    editLabelButton.setVisibility(View.VISIBLE);

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
        // fixme - this adapter gets replaced with the suggestionsadapter after typing!
        categoryAdapter = new LabelAdapter(this, R.layout.rowlayout_label, allLabels);

        labelsview.setAdapter(categoryAdapter);
        labelsview.setThreshold(MIN_INPUT);
        // note - replaced with labelparser.
        labelsview.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {
            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                return 0; // // TODO: 20-11-2017 write own tokenizer;
                //todo - use code from parser/adapter filter for testability
                //todo - see adapter filter for details.

            }

            @Override
            public int findTokenEnd(CharSequence text, int cursor) {
                return 0;
            }

            @Override
            public CharSequence terminateToken(CharSequence text) {
                return null;
            }
        });

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

        viewSwitcherTrigger.setOnClickListener(v -> {
            viewSwitcher.showNext();
        });

        ColorPicker.OnSelectionChangedListener colorListener = () -> {
            String label = labelsview.getText().toString();
            SpannableString spannable = new SpannableString(label);

//                int backgroundColor = Color.parseColor("#222222");// todo - set background to auto, random, or user-defined color.
            int foregroundColor = selectedColor; // todo - set foreground to black if necessary
            if (selectedColor == ResourcesCompat.getColor(getResources(), R.color.primary, getTheme()))
                selectedColor = ResourcesCompat.getColor(getResources(), R.color.accent, getTheme()); // Default color since making text transparent is kind of unusable.
//                BackgroundColorSpan background = new BackgroundColorSpan(backgroundColor);
            ForegroundColorSpan foreground = new ForegroundColorSpan(foregroundColor);
//                ClickableSpan onClickMenu = new ClickableSpan() {
//                    @Override
//                    public void onClick(View widget) {
//                        Log.i("ClickableSpan", "Item clicked.");
//                        View colorpicker = getLayoutInflater().from(context).inflate(R.layout.provisional_color_picker, null);
//                    }
//                };

//                spannable.setSpan(background, 0, label.length(), 0);
            spannable.setSpan(foreground, 0, label.length(), 0);

            labelsview.getText().replace(0, labelsview.getText().toString().length(), spannable, 0, spannable.length());
        };

        colorPicker.setOnSelectionChangedListener(colorListener);

        addLabelButton.setOnClickListener(v -> {

            // FIXME - don't use the entire labelsview string, delimit and parse first.
            // FIXME - reset the holder after adding tasks.
            // FIXME - make removal possible without hacking it by restarting the activity.

            for (Label label : parser.parseLabels(labelsview.getText().toString(), allLabels)) {

                TextView labelView = new TextView(context);
                labelView.setText(label.title);
                if (label.color != null && !label.color.isEmpty())
                    labelView.setTextColor(Integer.parseInt(label.color));
                else if (selectedColor == 0)
                    labelView.setTextColor(getColor(R.color.accent)); // FIXME: 26-10-2017 compatibility issues.
                else
                    labelView.setTextColor(selectedColor);

                int padding = Utils.convertDpToPx(this, 8f);
                labelView.setPadding(padding, padding, padding, padding);

                tasklabelslist.addView(labelView);
            }

            super.dismissKeyboard();
            tilLabelView.clearFocus(); // The TextInputLayout is what actually clears focus.
        });

        editLabelButton.setOnClickListener(v -> {
            Intent editLabelIntent = new Intent(this, Activities.SETLOCATION.activity);
            Label label = (Label) category_spinner.getSelectedItem();
            editLabelIntent.putExtra("label", Labels.toValues(label));
            startActivity(editLabelIntent);
        });

        // Get user location
        Log.i("TaskActivity", "Requesting user location...");
        SmartLocation.with(context).location().oneFix().start(location -> {
            Log.i("TaskActivity", "Finding matches for current location.");
            List<Label> results = filterItems(location);

            if (results.isEmpty())
                return;

            // Filter tasks for location
            Label priority = results.get(0);

            Log.i("TaskActivity", "Match found for current location: " + priority);


            Location labelLocation = new Location("");
            labelLocation.setLatitude(location.getLatitude());
            labelLocation.setLongitude(location.getLongitude());

            // TODO: 26-10-2017 Create compound list of nearby tags to filter with?
            if (TaskGateway.isNearby(location, labelLocation)){
                // use this location
                for (int i = 0; i < category_spinner.getCount(); i++) {
                    Log.i("TaskActivity", "Spinner item in matcher: " + category_spinner.getItemAtPosition(i).toString());
                    if (((Label) category_spinner.getItemAtPosition(i)).title.equalsIgnoreCase(priority.title)) {
                        category_spinner.setSelection(i);
                        Log.i("TaskActivity", "Match found in matcher: " + category_spinner.getItemAtPosition(i).toString());
                    }
                }
            }
            else{
                // use full list / default("do nothing")
            }

            // sort items by deadline
            // sort items by date added
        });

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
//        ArrayAdapter<Label> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_labels);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        category_spinner.setAdapter(adapter);
        category_spinner.setAdapter(new LabelSpinnerAdapter(this, R.layout.spinner_dropdown_label_item, spinner_labels));
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

        // Tasks available but not with labels.
        for (Task task : results){
            Label taskLabel = null;

            if (!task.labels.isEmpty())
                taskLabel = task.labels.get(0);

            Log.i("Filter", "Results: " + taskLabel);
        }

        adapter.tasks = results;
        list.getAdapter().notifyDataSetChanged(); // FIXME/VERIFY - More efficient solutions?
    }

    /**
     * Returns a list of labels sorted on closest distance to the user.
     * @param location
     * @return
     */
    private List<Label> filterItems(Location location){

        Map<Label, Double> distances = new HashMap<>();
        for (Label label : labeldb.getAll()) {
            // check for location.
            if (label.location != null) {
                Location labelLocation = new Location("");
                labelLocation.setLatitude(label.location.latitude);
                labelLocation.setLongitude(label.location.longitude);

                // check distance.
                double distance = TaskGateway.measureDistance(labelLocation, location);
                // map distances.
                distances.put(label, distance);
            }
        }
        // Put the entries of the map in a list so we can iterate over them.
        List<Map.Entry<Label, Double>> sortedLabels = new LinkedList<>(distances.entrySet());
        // sort by distances.
        Collections.sort(sortedLabels, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        // Convert the list of map entries back to a list of labels.
        List<Label> results = new LinkedList<>();
        // Foreach will be in order of nearest by because of the linking.
        for (Map.Entry<Label, Double> entry : sortedLabels){
            results.add(entry.getKey());
        }

        return results;
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
                        Log.i("Location Services", "Current location: " + location + " / " + current_address);
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
        String descriptionInput = descriptionView.getText().toString();
        String labelInput = labelsview.getText().toString();// FIXME use tasklabelslist

        if (descriptionInput.trim().isEmpty()) { // todo - espresso
            descriptionView.setError("Taak kan niet leeg zijn!");
            return;
        }

        // Check all labels for location.
        // If matches, use those instead.
        List<Task> new_tasks = new ArrayList<>();

        // FIXME - temp workaround for tasklabelslist not being an actual listview.
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < tasklabelslist.getChildCount(); i++) {
                n.append(((TextView) tasklabelslist.getChildAt(i)).getText())
                .append(", ");
        }
        labelInput = n.toString();

        List<Label> processedLabels = parser.parseLabels(labelInput, labeldb.getAll());
        List<Task> tasks = parser.parseTasks(descriptionInput);

        Date deadline = planTaskCheckbox.isChecked() ? parseDate() : null;

        for (Task task : tasks) {
            if (!processedLabels.isEmpty()) // note - Labels are optional, descriptions are not.
                for (Label label : processedLabels) {//FIXME  - use and reset tasklabelslist
                    Log.i("TaskActivity", "Adding processed labels...");
                    Log.i("TaskActivity", "label id: " + label.id);

                    // check if color is selected and doesn't override an existing selection,
                    // since the preliminary view resets color by default.
                    if (!labeldb.getAll().contains(label)) {
                        if (selectedColor != 0)
                            label.color = String.valueOf(selectedColor);

                        if (lastLocation != null) {
                            label.location = lastLocation;
                            lastLocation = null;
                        }
                    }

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
        }

        for (Task task : new_tasks) {
            adapter.addItem(adapter.getItemCount(), task);
            presenter.addTask(task); // FIXME: 3-10-2017 - SQLiteConstraintException: UNIQUE constraint failed.
            setFilterOptions();
        }

        // Show feedback after adding.
        // String builder for user snackbar.
        String single = "Taak toegevoegd";
        String plural = "Taken toegevoegd";
        StringBuilder builder;
        builder = new StringBuilder(new_tasks.size() > 1? plural: single);
        for (int i = 0; i < tasks.size(); i++) {
            builder.append(tasks.get(i));
            if (i < tasks.size() - 1) {
                builder.append(", ");
            }
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

        Snackbar.make(v, builder.toString(), Snackbar.LENGTH_SHORT).show();
        descriptionView.setText("");
        labelsview.setText("");
        tasklabelslist.removeAllViews();
        tilAddTaskDescription.clearFocus();
    }

    @OnClick(R.id.imageButton) void addLocation(){ // note - switched to autocomplete instead.
        String[] tags = labelsview.getText().toString().split(",");

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tags);
        final View locationView = LayoutInflater.from(this).inflate(R.layout.dialog_addlocation, null);
        TextInputEditText addressView = (TextInputEditText) locationView.findViewById(R.id.dialog_addlocation_address);
        AutoCompleteTextView tagsView = (AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags);
        tagsView.setAdapter(categoryAdapter);

        SmartLocation.with(this).location().oneFix().start(location -> {
            SmartLocation.with(context).geocoding().reverse(location, (location1, list1) -> {
                // note - reverse the found GPS coordinates to a human readable address.
                Address address = list1.get(0);

                lastLocation = new achan.nl.uitstelgedrag.domain.models.Location(address);

//                addressView.setText(address.getAddressLine(0));
                addressView.setText(lastLocation.name);
            });
        });

        dialog.setView(locationView)
                .setPositiveButton("voeg toe", (dialogInterface, i) -> {
//                    templateLabel = new Label(); note - not necessary, just use location in addTask.
//                    templateLabel.title = ((AutoCompleteTextView) locationView.findViewById(R.id.dialog_addlocation_tags)).getText().toString();
//                    templateLabel.location = lastLocation;
                    //task.labels.add(label);
                    Log.i("Location", "No code for adding location to label in OnClickListener yet.");
                })
                .setNegativeButton("laat maar", (dialogInterface, i) -> {
                    lastLocation = null;
                })
                .create()
                .show();
    }

    @OnLongClick(R.id.imageButton) boolean addCurrentLocation(View v){
        Intent setCustomLocationIntent = new Intent(context, SetLocationActivity.class);
        startActivity(setCustomLocationIntent);
        return true;
        /*
        waitingForLocationDialog.show();
        smartLocation
                .location()
                .config(LocationParams.NAVIGATION)
                .oneFix()
                .start(location -> {
                    Log.i("LocationManager", "Location Registered: " + location.toString());
                    current = location;
                    waitingForLocationDialog.dismiss();

                    // note - var 'this.context' is not a child of AppCompat, so we have to use 'this'.
                    final EditText view = new EditText(this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Naam voor locatie")
                    .setView(view)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        Label label = new Label();
                        label.location = new achan.nl.uitstelgedrag.domain.models.Location();
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
        */
    }

//    public static final int TODAY = 0;
//    public static final int TOMORROW = 1;
//    public static final int NEXT_WEEK = 2;
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
