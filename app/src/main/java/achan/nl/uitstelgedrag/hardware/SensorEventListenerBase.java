package achan.nl.uitstelgedrag.hardware;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Slightly shorter abstraction of the SensorEventListener.
 *
 * Created by Etienne on 7-11-2016.
 */
public abstract class SensorEventListenerBase implements SensorEventListener{

    @Override
    abstract public void onSensorChanged(SensorEvent event);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
    }
}
