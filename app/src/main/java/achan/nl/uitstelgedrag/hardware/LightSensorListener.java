package achan.nl.uitstelgedrag.hardware;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import static achan.nl.uitstelgedrag.ui.activities.NoteActivity.LIGHTSENSOR_SAMPLING_RATE_MS;

/**
 * Light sensor listener that detects dark / light environments.
 *
 * todo - use for day/night theming.
 * todo - coupling with BaseActivity.
 * Created by Etienne on 7-11-2016.
 */
public class LightSensorListener extends SensorEventListenerBase{

    public static final int MEASURED_LUX                  = 0;
    public static final int LIGHT_LEVEL_DARK              = 1;
    public static final int LIGHT_LEVEL_LIGHT             = 10;
    public static final int LIGHTSENSOR_CHANGE_DELAY      = 2500 / LIGHTSENSOR_SAMPLING_RATE_MS; // Compensates for incorrect readings.

    int lightSensorPreChangeCount = 0;
    int lightSensorState          = LIGHT_LEVEL_LIGHT;

    public SensorCallback callback;

    @Override
    public void onSensorChanged(SensorEvent event) {// IMPORTANT unable to compensate for day/night: night typically returns 0 lux;
        // See ProximitySensorListener logic.
        if (callback != null)
            callback.execute(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
    }
}
