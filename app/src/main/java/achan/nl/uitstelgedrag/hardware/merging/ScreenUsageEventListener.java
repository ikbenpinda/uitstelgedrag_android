package achan.nl.uitstelgedrag.hardware.merging;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Vibrator;
import android.util.Log;

/**
 * SensorEventListener that detects whether the screen is facing downwards or is too close to the user,
 * and should turn off/on.
 *
 * Created by Etienne on 03/11/16.
 */
public class ScreenUsageEventListener extends BaseSensorListener {

    public static final int SAMPLING_RATE = 1_000_000; // in microseconds.

    private static final int   DEVICE_ORIENTATION_DOWNWARDS    = 1;
    private static final int   DEVICE_ORIENTATION_NONDOWNWARDS = 0;
    private static final int   COUNT_THRESHOLD = 7; // arbitrary amount of minimum cycles before setState();
    // This can be set by either incrementing the count_threshold or this,
    // but the count_threshold is unreliable because the polling rate may not be enforced.

    private static final float DOWNWARDS               = -9; // -90 with filtering?
    private static final float Z_ANGLE_THRESHOLD       = 2;  // x10 with filtering? | arbitrary number of deviation in degrees.
    private static final int   MEASURED_ACCELERATION_Z = 2;

    public boolean useFallbackSensor = false;
    private int device_z_orientation = 0;
    private int accel_count = 0;                  // setState() counter that changes on count > threshold.

    Sensor sensor;
    Sensor fallbackSensor;
    Vibrator vibrator;

    OnScreenUsageChangedListener callback;

    public ScreenUsageEventListener(Context context){
        super(context);
        sensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        fallbackSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Current device has broken proximity sensor.
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public ScreenUsageEventListener(Context context, boolean useFallbackSensor, OnScreenUsageChangedListener callback){
        super(context);
        sensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        fallbackSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Current device has broken proximity sensor.
        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        this.useFallbackSensor = useFallbackSensor;
        this.callback = callback;
    }

    @Override
    public void startListening(){
        if (useFallbackSensor)
            manager.registerListener(this, fallbackSensor, SAMPLING_RATE); // FIXME check right sensor being used.
        else
            manager.registerListener(this, sensor, SAMPLING_RATE);
    }

    @Override
    public void stopListening(){
        manager.unregisterListener(this);
    }

    public void checkProximity(SensorEvent event){
        // todo
    }

    /**
     * Detects if the phone is downwards facing or not.
     * Returns orientation state.
     * @param event
     * @return
     */
    public void checkAcceleration(SensorEvent event){

        float device_angle = event.values[MEASURED_ACCELERATION_Z];

        Log.v("Accelerometer","Device z-acceleration: " + device_angle);

        if (device_z_orientation == DEVICE_ORIENTATION_NONDOWNWARDS){
            if (Math.abs(device_angle - DOWNWARDS) < Z_ANGLE_THRESHOLD) {
                Log.v("Accelerometer", "Acceleration difference in range: " + Math.abs(device_angle - DOWNWARDS) + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                accel_count++;
            }
            else
                accel_count = 0;

            if (accel_count > COUNT_THRESHOLD) {
                Log.w("Accelerometer", "Device flagged as downwards facing.");
                vibrator.vibrate(250);
                device_z_orientation = DEVICE_ORIENTATION_DOWNWARDS;
                callback.onScreenUnused();
            }
            else
                Log.v("Accelerometer","Device flagged as non-downwards facing.");
        }
        else {
            if (Math.abs(device_angle - DOWNWARDS) > Z_ANGLE_THRESHOLD) {
                Log.v("Accelerometer", "Acceleration difference out of range: " + Math.abs(device_angle - DOWNWARDS) + "(threshold =" + Z_ANGLE_THRESHOLD + ").");
                accel_count++;
            }
            else
                accel_count = 0;

            if (accel_count > COUNT_THRESHOLD * 2) {// The threshold only goes for 90- degrees, not upwards.
                Log.w("Accelerometer", "Device flagged as non-downwards facing.");
                vibrator.vibrate(250);
                device_z_orientation = DEVICE_ORIENTATION_NONDOWNWARDS;
                callback.onScreenUsed();
            }
            else
                Log.v("Accelerometer","Device flagged as downwards facing.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_PROXIMITY:
                    checkProximity(event);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                    checkAcceleration(event);
                break;
            default:
                Log.e("ScreenEventListener", "Unknown sensor.");
        }
    }

    public interface OnScreenUsageChangedListener {
        void onScreenUnused();
        void onScreenUsed();
    }
}
