package achan.nl.uitstelgedrag.ui.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Attachment;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.hardware.Callback;
import achan.nl.uitstelgedrag.hardware.Recorder;
import achan.nl.uitstelgedrag.hardware.SensorEventListenerBase;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.gateways.NoteGateway;
import achan.nl.uitstelgedrag.ui.adapters.NoteAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends Base {

    public static final int LIGHTSENSOR_SAMPLING_RATE_MS   = 500;
    public static final int ACCELEROMETER_SAMPLING_RATE_MS = 1000;
    public static final int ACCELEROMETER_STATE_LANDSCAPE  = 1;
    public static final int ACCELEROMETER_STATE_PORTRAIT   = 2;
    public static final int ACCELEROMETER_STATE_DOWNWARDS  = 3;

    Repository<Note> notes;
    NoteAdapter adapter;

    SensorEventListener listener;
    SensorManager       sensors; // important - basic sensors are the proximity sensor and accelerometer.
    //    Sensor proximitySensor; // FIXME Proximity sensor currently broken on device.
    //    Sensor lightSensor; // doesn't compensate for nighttime light levels.
    Sensor              accelerometer;
    Vibrator            vibrator;
    Recorder            recorder;
    MediaPlayer player;

    // region Camera intent code.

    private static final int REQUEST_IMAGE_CAPTURE         = 1;

    private String mCurrentPhotoPath;

    Callback callback = () -> {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,  // prefix
                        ".jpg",         // suffix
                        storageDir      // directory
                );

                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPath = "file:" + image.getAbsolutePath();
                photoFile = image;
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Camera", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    @Override // todo - note creation with attachment.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            try {
                //mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                //mImageView.setImageBitmap(mImageBitmap);
                Note note = new Note();
                note.text = "Foto";
                note.created = new Date();
                note.attachment = new Attachment();
                note.attachment.path = mCurrentPhotoPath;
                note.attachment.type = Attachment.ATTACHMENT_TYPE_PHOTO;
                notes.insert(note);
                adapter.addItem(adapter.getItemCount(), note);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    // endregion

    @BindView(R.id.noteview_list_notes) RecyclerView notelist;

    @Override
    Activities getCurrentActivity() { return Activities.NOTES; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        player = new MediaPlayer();
        notes = new NoteGateway(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        recorder = new Recorder(this);
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
        adapter = new NoteAdapter(notes.getAll(), this, player);
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

    // region Legacy sensorlisteners.
    public class ProximitySensorListener extends SensorEventListenerBase {

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
    }

    // todo - use this for day/night theming.
    public class LightSensorListener extends SensorEventListenerBase { // important - light sensor does not detect light as >0 in dimly lit environments.

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
    }
    // endregion

    public class AccelerometerListener extends SensorEventListenerBase {
        // IMPORTANT - no high-pass filter for gravity applied!
        // TODO/IMPORTANT - background thread!

        static final int   DEVICE_ORIENTATION_HORIZONTAL    = 1;
        static final int   DEVICE_ORIENTATION_NONHORIZONTAL = 0;

        static final int   DEVICE_ORIENTATION_DOWNWARDS     = 1;
        static final int   DEVICE_ORIENTATION_NONDOWNWARDS  = 0;

        static final int COUNT_THRESHOLD = 7; // arbitrary amount of minimum cycles before setState();
                                              // This can be set by either incrementing the count_threshold or this,
                                              // but the count_threshold is unreliable because the polling rate may not be enforced.

        static final float HORIZONTAL              = 10; // Uncorrected value.
        static final float DOWNWARDS               = -9; // -90 with filtering?
        static final float X_ANGLE_THRESHOLD       = 2;
        static final float Z_ANGLE_THRESHOLD       = 2;  // x10 with filtering? | arbitrary number of deviation in degrees.

        static final int   MEASURED_ACCELERATION_X = 0;
        static final int   MEASURED_ACCELERATION_Z = 2;

        int device_x_orientation = 0; // portrait/landscape. Avoids android:screenOrientation constraint issues.
        int device_z_orientation = 0; // facing down or upwards.
        int x_accel_count        = 0; // setState() counter that changes on count > threshold.
        int z_accel_count        = 0; // setState() counter that changes on count > threshold.

        @Override
        public void onSensorChanged(SensorEvent event) {
            // todo - state cohesion and refactorino.
            // todo - prevent trigger on threshold edge.

            float device_x_angle = event.values[MEASURED_ACCELERATION_X];
            float device_z_angle = event.values[MEASURED_ACCELERATION_Z];

            Log.i("Accelerometer","\nDevice:\n z-acceleration: " + device_z_angle + ",\nx-acceleration: " + device_x_angle);
            checkVerticalAcceleration(device_z_angle);
            checkHorizontalAcceleration(device_x_angle);
        }

        private void checkVerticalAcceleration(float device_z_angle){

            // Check downwards/nondownwards.
            float z_difference = Math.abs(device_z_angle - DOWNWARDS);
            if (device_z_orientation == DEVICE_ORIENTATION_NONDOWNWARDS){
                if (z_difference < Z_ANGLE_THRESHOLD) {
                    Log.i("Accelerometer", "Acceleration difference in range: " + z_difference + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                    z_accel_count++;
                }
                else{
                    Log.i("Accelerometer", "Acceleration difference out of range, not changing state.");
                    z_accel_count = 0;
                }

                if (z_accel_count > COUNT_THRESHOLD) {
                    Log.w("Accelerometer", "Device flagged as downwards facing.");
                    vibrator.vibrate(250);
                    device_z_orientation = DEVICE_ORIENTATION_DOWNWARDS;
                    disableScreen();
                    recorder.startRecording();
                }
            }
            if (device_z_orientation == DEVICE_ORIENTATION_DOWNWARDS){
                if (z_difference > Z_ANGLE_THRESHOLD) {
                    Log.i("Accelerometer", "Acceleration difference out of range: " + z_difference + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                    z_accel_count++;
                }
                else {
                    Log.i("Accelerometer", "Acceleration difference in range, not changing state.");
                    z_accel_count = 0;
                }

                if (z_accel_count > COUNT_THRESHOLD * 2) {// The threshold only goes for 90- degrees, not upwards.
                    Log.w("Accelerometer", "Device flagged as non-downwards facing.");
                    vibrator.vibrate(250);
                    device_z_orientation = DEVICE_ORIENTATION_NONDOWNWARDS;
                    if (recorder.isRecording) {
                        vibrator.vibrate(250);
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
        }

        private void checkHorizontalAcceleration(float device_x_angle){

            // Check landscape/portrait.
            // todo - only check if portrait beforehand / light level OK / not recording.
            // todo - tweak thresholds.
            // Both negative and positive are OK values here as long as they are within the threshold.
            // Where 0 means the location of the home button: [ -9 <-- 0 --> +9 ]
            float x_difference = Math.abs(Math.abs(device_x_angle) - HORIZONTAL);
            if (device_x_orientation == DEVICE_ORIENTATION_NONHORIZONTAL){
                if (x_difference < X_ANGLE_THRESHOLD){
                    Log.i("Accelerometer", "Acceleration difference in range: " + x_difference + "(threshold =" + X_ANGLE_THRESHOLD + ").");
                    x_accel_count++;
                }
                else{
                    Log.i("Accelerometer", "Acceleration difference out of range, not changing state.");
                    x_accel_count = 0;
                }

                if (x_accel_count > COUNT_THRESHOLD){
                    vibrator.vibrate(250);
                    Log.w("Accelerometer", "Device flagged as held in landscape.");
                    device_x_orientation = DEVICE_ORIENTATION_HORIZONTAL;
                    openCamera(); // todo - only on right light level?
                }

            }
            if (device_x_orientation == DEVICE_ORIENTATION_HORIZONTAL) {
                if (x_difference > X_ANGLE_THRESHOLD) {
                    Log.i("Accelerometer", "Acceleration difference out of range: " + x_difference + "(threshold =" + X_ANGLE_THRESHOLD + ").");
                    x_accel_count++;
                } else {
                    Log.i("Accelerometer", "Acceleration difference in range, not changing state.");
                    x_accel_count = 0;
                }

                if (x_accel_count > COUNT_THRESHOLD * 2) {
                    vibrator.vibrate(250);
                    Log.w("Accelerometer", "Device flagged as held in non-landscape.");
                    device_x_orientation = DEVICE_ORIENTATION_NONHORIZONTAL;
                    /* todo - check for nondownwards/ only activate when held / not-recording */
                    //                    if (true) {
                    //                        // todo - create attachment for picture, close camera.
                    //                        // todo - attachment making on activity callback.
                    //                    }
                }
            }
        }

        private void openCamera() {
            Log.i("Accelerometer", "Firing up camera.");
            callback.execute();
        }
    }
}
