package achan.nl.uitstelgedrag.ui.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Attachment;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.gateways.NoteGateway;
import achan.nl.uitstelgedrag.ui.adapters.NoteAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends Base {

    public static final int LIGHTSENSOR_SAMPLING_RATE_MS  = 500;
    public static final int ACCELEROMETER_SAMPLING_RATE_MS= 1000;
    public static final int ACCELEROMETER_STATE_LANDSCAPE = 1;
    public static final int ACCELEROMETER_STATE_PORTRAIT  = 2;
    public static final int ACCELEROMETER_STATE_DOWNWARDS = 3;

    Repository<Note> notes;
    NoteAdapter adapter;

    SensorEventListener listener;
    SensorManager sensors; // important - basic sensors are the proximity sensor and accelerometer.
    //    Sensor proximitySensor; // FIXME Proximity sensor currently broken on device.
    //    Sensor lightSensor; // doesn't compensate for nighttime light levels.
    Sensor accelerometer;
    Vibrator vibrator;
    Recorder recorder;

    @BindView(R.id.noteview_list_notes) RecyclerView notelist;

    @Override
    Activities getCurrentActivity() { return Activities.NOTES; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        notes = new NoteGateway(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        recorder = new Recorder();
        sensors = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new AccelerometerListener();
        sensors.registerListener(listener, accelerometer, ACCELEROMETER_SAMPLING_RATE_MS);
//        proximitySensor = sensors.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        lightSensor = sensors.getDefaultSensor(Sensor.TYPE_LIGHT);
//        sensors.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
//        Log.i("ProximitySensor", "Firing up proximity sensor with range of " + proximitySensor.getMaximumRange() + "(cm?)");
//        sensors.registerListener(this, lightSensor, LIGHTSENSOR_SAMPLING_RATE_MS);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new NoteAdapter(notes.getAll(), this);
        notelist.setLayoutManager(manager);
        notelist.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // todo - persistence
        // todo - attachments
        fab.setOnClickListener(view -> {
            View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
            new AlertDialog.Builder(this)
                    .setTitle("Nieuwe notitie")
                    .setView(dialog)
                    .setNegativeButton("Annuleren", null)
                    .setPositiveButton("Toevoegen", (dialog1, which) -> {
                        TextInputEditText content = (TextInputEditText) dialog.findViewById(R.id.dialog_add_note_content);
                        Note note = new Note();
                        note.text = content.getText().toString();
                        note.created = new Date();
//                        note.attachment = null; todo
                        notes.insert(note);
                        adapter.addItem(adapter.getItemCount(), note);
//                        Snackbar.make()
                    })
                    .create()
                    .show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensors.unregisterListener(listener);
        Log.e("NoteActivity", "Paused activity.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensors.registerListener(listener, accelerometer, ACCELEROMETER_SAMPLING_RATE_MS);
//        sensors.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
//        sensors.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.e("NoteActivity", "Resumed activity.");
    }

    private void enableScreen() {
        Log.w("ScreenManager", "Screen is turned on.");
    }

    private void disableScreen() {
        Log.w("ScreenManager", "Screen is turned off.");
    }

    public class Recorder{

        MediaRecorder recorder;
        String filename;
        boolean isRecording = false;

        public String stopRecording() {
            vibrator.vibrate(250);

            recorder.stop();
            recorder.release();
            recorder = null;

            isRecording = false;
            Log.w("Recorder", "Recording saved to " + filename);
            return filename;
        }

        public void startRecording() {
            vibrator.vibrate(250);

            filename = getFilesDir().getAbsolutePath() + String.format(Locale.ENGLISH, "recording_%d_%s", 4, Timestamp.formatDate(new Date()));
            recorder = new MediaRecorder(); // FIXME: 30-10-2016 illegal state
//            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(filename);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("Recorder", "prepare() failed: " + e.getMessage());
            }

            recorder.start();
            isRecording = true;
            Log.w("Recorder", "Recording message.");
        }
    }

    public class SensorEventListenerBase implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
        }
    }

    // region Legacy sensorlisteners.
    public class ProximitySensorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.w("Proximity Sensor", "Proximity sensor state(broken/working) unreliable!");
            //                Log.i("NoteActivity", "ah! i-i didn't know you liked me that much~ <3");
            //                Log.i("NoteActivity", "Proximity sensor distance: " + event.values[0]);
            //                float lux = event.values[MEASURED_LUX];
            //                Log.i("NoteActivity", "I-it's dark in here! T_T");
            //                Log.i("NoteActivity", "Light sensor measured lux: " + lux);
            //
            //                if (lightSensorState == LIGHT_LEVEL_DARK) {
            //
            //                    if (lux > 0)
            //                        lightSensorPreChangeCount++;
            //                    else
            //                        lightSensorPreChangeCount = 0;
            //
            //                    if (lightSensorPreChangeCount >= LIGHTSENSOR_CHANGE_DELAY) {
            //                        stopRecording();
            //                        enableScreen();
            //                        lightSensorState = LIGHT_LEVEL_LIGHT;
            //                    }
            //                }
            //                else {
            //
            //                    if (lux == 0)
            //                        lightSensorPreChangeCount++;
            //                    else
            //                        lightSensorPreChangeCount = 0;
            //
            //                    if (lightSensorPreChangeCount >= LIGHTSENSOR_CHANGE_DELAY) {
            //                        disableScreen();
            //                        startRecording();
            //                        lightSensorState = LIGHT_LEVEL_DARK;
            //                    }
            //                }
            //
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
        }
    }

    public class LightSensorListener implements SensorEventListener{ // important - light sensor does not detect light as >0 in dimly lit environments.

        static final int MEASURED_LUX                  = 0;
        static final int LIGHT_LEVEL_DARK              = 1;
        static final int LIGHT_LEVEL_LIGHT             = 2;
        static final int LIGHTSENSOR_CHANGE_DELAY      = 2500 / LIGHTSENSOR_SAMPLING_RATE_MS; // Compensates for incorrect readings.

        int lightSensorPreChangeCount = 0;
        int lightSensorState          = LIGHT_LEVEL_LIGHT;

        @Override
        public void onSensorChanged(SensorEvent event) {// IMPORTANT unable to compensate for day/night: night typically returns 0 lux;
            // See ProximitySensorListener logic.
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
        }
    }
    // endregion

    public class AccelerometerListener extends SensorEventListenerBase{// IMPORTANT - no high-pass filter for gravity applied!

        static final int   DEVICE_ORIENTATION_DOWNWARDS    = 1;
        static final int   DEVICE_ORIENTATION_NONDOWNWARDS = 0;


        static final int COUNT_THRESHOLD = 7; // arbitrary amount of minimum cycles before setState();
                                              // This can be set by either incrementing the count_threshold or this,
                                              // but the count_threshold is unreliable because the polling rate may not be enforced.

        static final float DOWNWARDS               = -9; // -90 with filtering?
        static final float Z_ANGLE_THRESHOLD       = 2;  // x10 with filtering? | arbitrary number of deviation in degrees.
        static final int   MEASURED_ACCELERATION_Z = 2;

        int device_z_orientation = 0;
        int accel_count = 0;                  // setState() counter that changes on count > threshold.

        @Override
        public void onSensorChanged(SensorEvent event) {
            // todo - accelerometer for camera
            // todo - if acceleration suddenly changes > threshold
            // todo - prevent trigger on threshold edge.

            float device_angle = event.values[MEASURED_ACCELERATION_Z];

            Log.i("Accelerometer","Device z-acceleration: " + device_angle);

            float difference = Math.abs(device_angle - DOWNWARDS);
            if (device_z_orientation == DEVICE_ORIENTATION_NONDOWNWARDS){
                if (difference < Z_ANGLE_THRESHOLD) {
                    Log.i("Accelerometer", "Acceleration difference in range: " + Math.abs(device_angle - DOWNWARDS) + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                    accel_count++;
                }
                else{
                    Log.i("Accelerometer", "Acceleration difference out of range, not changing state.");
                    accel_count = 0;
                }

                if (accel_count > COUNT_THRESHOLD) {
                    Log.w("Accelerometer", "Device flagged as downwards facing.");
                    vibrator.vibrate(250);
                    device_z_orientation = DEVICE_ORIENTATION_DOWNWARDS;
                    disableScreen();
                    recorder.startRecording();
                }
            }
            else {
                if (difference > Z_ANGLE_THRESHOLD) {
                    Log.i("Accelerometer", "Acceleration difference out of range: " + Math.abs(device_angle - DOWNWARDS) + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                    accel_count++;
                }
                else {
                    Log.i("Accelerometer", "Acceleration difference in range, not changing state.");
                    accel_count = 0;
                }

                if (accel_count > COUNT_THRESHOLD * 2) {// The threshold only goes for 90- degrees, not upwards.
                    Log.w("Accelerometer", "Device flagged as non-downwards facing.");
                    vibrator.vibrate(250);
                    device_z_orientation = DEVICE_ORIENTATION_NONDOWNWARDS;
                    if (recorder.isRecording) {
                        recorder.stopRecording();
                        enableScreen();
                        Note note = new Note();
                        note.text = "Opname";
                        note.created = new Date();
                        note.attachment = new Attachment();
                        note.attachment.path = recorder.filename;
                        note.attachment.type = Attachment.ATTACHMENT_TYPE_AUDIO;
                        notes.insert(note);
                        adapter.addItem(adapter.getItemCount(), note);
                    }
                }
            }

            //                todo | if orientation == horizontal (&& lightlevel = light // what if user is in bed or on couch during daytime?)
            //                openCamera();
        }
    }
}
