package achan.nl.uitstelgedrag.ui;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import achan.nl.uitstelgedrag.hardware.LightSensorListener;
import achan.nl.uitstelgedrag.persistence.Settings;

import static achan.nl.uitstelgedrag.ui.Themes.AUTO;
import static achan.nl.uitstelgedrag.ui.Themes.DARK;
import static achan.nl.uitstelgedrag.ui.Themes.LIGHT;

/**
 * Applies strategies for the apptheme.
 * Created by Etienne on 10/01/17.
 */
public class Themer {

    public static SensorManager manager;
    public static Sensor lightSensor;
    public static LightListener lightListener;
    public static Themes currentTheme;

//    public static int passes = 0; // Executed amount of times setTheme() is called.
    public static boolean listening = false; // For Themes.AUTO only.

    /**
     * Themes the given activity with the right theme.
     * @param activity the superclass of the activity to theme(e.g. Base).
     * @param theme See Base.Themes.
     * @return true if the theme has been changed.
     */
    public static boolean setTheme(Activity activity, Themes theme){

//        lightListener = new LightListener(activity);

        switch (theme) {
            case LIGHT:
                Log.w("Themer", "Theme = Light");
//                unregisterLightListener();
                if (currentTheme != LIGHT) {
                    currentTheme = LIGHT;
                    new Settings(activity).setTheme(LIGHT.id);
                    activity.setTheme(LIGHT.style);
                    return true;
//                    activity.recreate();
                }
                break;
            case DARK:
                Log.w("Themer", "Theme = Dark");
//                unregisterLightListener();
                if (currentTheme != DARK) {
                    currentTheme = DARK;
                    new Settings(activity).setTheme(DARK.id);
                    activity.setTheme(DARK.style);
                    return true;
//                    activity.recreate();
                }
                break;
            case AUTO:
                if (!listening) {
                    listening = true;
                    return true;
                    // register only once.
//                    manager = ((SensorManager) activity.getSystemService(activity.SENSOR_SERVICE));
//                    lightSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
//                    manager.unregisterListener(lightListener);
//                    manager.registerListener(lightListener, lightSensor, 2_500_000);
                }
                break;
        }

        return false;
    };

    public static class LightListener extends LightSensorListener{
        public LightListener(Activity activity) {
            super(activity);
            callback = (event) -> {
                float lux = event.values[LightSensorListener.MEASURED_LUX];
                Log.i("LightSensor", "Light sensor measured lux: " + lux);

                boolean low_light = lux < 10;

                // only set if the theme hasnt already been set *twice*.
                if (low_light) {
                    // Prevent calling recreate() non-stop.
                    // activity.getTheme().equals(R.style.AppTheme never returns true.
                    if (currentTheme == null || currentTheme != Themes.DARK) {
//                        passes++;
                        Log.w("Themer", "Theme = Dark");
                        currentTheme = DARK; // Second pass.
                        new Settings(activity).setTheme(AUTO.id);
                        activity.setTheme(DARK.style);
//                        activity.recreate();
//                        unregisterLightListener();
                        // this will cause suffering.
                        // second pass is handled by Base to theme the decorations.
                    }
                } else {
                    if (currentTheme == null || currentTheme != Themes.LIGHT) {
//                        passes++;
                        Log.w("Themer", "Theme = Light");
                        currentTheme = LIGHT;
                        new Settings(activity).setTheme(AUTO.id);
                        activity.setTheme(LIGHT.style);
//                        activity.recreate();
//                        unregisterLightListener();
                    }
                }
            };
        }
    }

    public static void unregisterLightListener(){
        if (manager != null && lightListener != null)
            manager.unregisterListener(lightListener);
        listening = false;
    }
}
