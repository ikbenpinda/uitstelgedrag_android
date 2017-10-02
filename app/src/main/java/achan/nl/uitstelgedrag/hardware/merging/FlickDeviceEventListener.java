package achan.nl.uitstelgedrag.hardware.merging;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * SensorEventListener that detects if the user flick its device forward/backward.
 *
 * Created by Etienne on 11/11/16.
 */
public class FlickDeviceEventListener extends BaseSensorListener{

    public static final int SAMPLING_RATE = 2_000_000; // Sensor sampling rate in microseconds.
    public static final int THRESHOLD = 4;
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_TRIGGERED = 1;
    public OnFlickListener callback;

    private int state = STATE_DEFAULT;
    private Sensor gyroscope;

    public FlickDeviceEventListener(Context context) {
        super(context);
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public FlickDeviceEventListener(Context context, OnFlickListener callback){
        super(context);
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.callback = callback;
    }

    public FlickDeviceEventListener setOnFlickListener(OnFlickListener callback){
        this.callback = callback;
        return this;
    }

    @Override
    public void startListening(){
        manager.registerListener(this, gyroscope, SAMPLING_RATE);
    }

    @Override
    public void stopListening(){
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int MEASURED_X_ROTATION = 0;
        float x = Math.abs(event.values[MEASURED_X_ROTATION]);
        Log.v("Gyroscope", "Measured X-rotation: " + x);
        if (x > THRESHOLD && state == STATE_DEFAULT){
            state = STATE_TRIGGERED;
            callback.onFlick();
        } else
            state = STATE_DEFAULT;
    }

    public interface OnFlickListener{
        void onFlick();
    }
}
