package achan.nl.uitstelgedrag;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

/**
 * Created by Etienne on 9-8-2016.
 */
public class Application extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);

        // todo ORM initialization or whatevs

        // todo handle singleton initialization
        // todo handle time-based or async stuff that should happen on startup/service communication
    }

}
