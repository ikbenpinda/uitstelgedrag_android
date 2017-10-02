package achan.nl.uitstelgedrag.hardware.merging;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Vibrator;
import android.util.Log;

/**
 * SensorEventListener that opens the camera when the device is held in landscape mode.
 * fixme / Samsung is being a bitch again with the camera API.
 * Created by Etienne on 03/11/16.
 */
public class OnLandscapeEventListener extends BaseSensorListener {

    public static final int SAMPLING_RATE = 1_000_000;

    Sensor accelerometer;
    Vibrator vibrator;
    OnLandscapeListener callback;

    public OnLandscapeEventListener(Context context) {
        super(context);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public OnLandscapeEventListener(Context context, OnLandscapeListener callback) {
        super(context);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        this.callback = callback;
    }

    public void setOnLandscapeListener(OnLandscapeListener listener){
        callback = listener;
    }

    // todo - move to activity because of onActivityResult, see Uitstelgedrag.
    // todo - custom (surface)view for camera?
    // todo - listener onSensorChanged and startListening/stopListening

    @Override
    public void startListening(){
        manager.registerListener(this, accelerometer, SAMPLING_RATE);
    }

    @Override
    public void stopListening(){
        manager.unregisterListener(this);
    }


    // note - no high-pass filter for gravity applied!
    // todo - background thread!

    static final int   DEVICE_ORIENTATION_HORIZONTAL    = 1;
    static final int   DEVICE_ORIENTATION_NONHORIZONTAL = 0;
    static final int   COUNT_THRESHOLD = 7; // arbitrary amount of minimum cycles before setState();
    // This can be set by either incrementing the count_threshold or this,
    // but the count_threshold is unreliable because the polling rate may not be enforced.

    static final float HORIZONTAL              = 10; // -15 with filtering?
    static final float X_ANGLE_THRESHOLD       = 2;
    static final int   MEASURED_ACCELERATION_X = 0;

    int device_x_orientation = 0; // portrait/landscape. Avoids android:screenOrientation constraint issues.
    int x_accel_count        = 0;                  // setState() counter that changes on count > threshold.

    public void listen(SensorEvent event){

        // todo - state cohesion and refactorino.
        // todo - prevent trigger on threshold edge.

        float device_x_angle = event.values[MEASURED_ACCELERATION_X];

        // Check landscape/portrait.
        // todo - only check if portrait beforehand / light level OK / not recording.
        // todo - tweak thresholds.
        // Both negative and positive are OK values here as long as they are within the threshold.
        // Where 0 means the location of the home button: [ -9 <-- 0 --> +9 ]
        float x_difference = Math.abs(Math.abs(device_x_angle) - HORIZONTAL);
        if (device_x_orientation == DEVICE_ORIENTATION_NONHORIZONTAL){
            if (x_difference < X_ANGLE_THRESHOLD){
                Log.v("Accelerometer", "Acceleration difference in range: " + x_difference + "(threshold =" + X_ANGLE_THRESHOLD + ").");
                x_accel_count++;
            }
            else{
                Log.v("Accelerometer", "Acceleration difference out of range, not changing state.");
                x_accel_count = 0;
            }

            if (x_accel_count > COUNT_THRESHOLD){
                vibrator.vibrate(500);
                Log.w("Accelerometer", "Device flagged as held in landscape.");
                device_x_orientation = DEVICE_ORIENTATION_HORIZONTAL;
                openCamera(); // todo - only on right light level?
            }

        }
        if (device_x_orientation == DEVICE_ORIENTATION_HORIZONTAL) {
            if (x_difference > X_ANGLE_THRESHOLD) {
                Log.v("Accelerometer", "Acceleration difference out of range: " + x_difference + "(threshold =" + X_ANGLE_THRESHOLD + ").");
                x_accel_count++;
            } else {
                Log.v("Accelerometer", "Acceleration difference in range, not changing state.");
                x_accel_count = 0;
            }

            if (x_accel_count > COUNT_THRESHOLD * 2) {
                vibrator.vibrate(500);
                Log.w("Accelerometer", "Device flagged as held in non-landscape.");
                device_x_orientation = DEVICE_ORIENTATION_NONHORIZONTAL;
                    /* todo - check for nondownwards/ only activate when held / not-recording */
                if (true) {
                    // todo - create attachment for picture, close camera.
                }
            }
        }
    }

    private void openCamera() {
        callback.onLandscape();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        listen(event);
    }

    public interface OnLandscapeListener{
        void onLandscape();
    }
}
