package achan.nl.uitstelgedrag.hardware;

import android.hardware.SensorEvent;

/**
 * Created by Etienne on 10/01/17.
 */
public interface SensorCallback {
    void execute(SensorEvent event);
}
