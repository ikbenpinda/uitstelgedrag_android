package achan.nl.uitstelgedrag.hardware.merging;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Laziness.
 * Created by Etienne on 03/11/16.
 */
public abstract class BaseSensorListener implements SensorEventListener {

    protected Context context;
    protected SensorManager manager;

    public BaseSensorListener(Context context) {
        this.context = context;
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.w(sensor.getName(), "Accuracy changed: " + accuracy);
    }

    public abstract void startListening();
    public abstract void stopListening();
}
