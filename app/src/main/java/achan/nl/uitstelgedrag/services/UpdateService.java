package achan.nl.uitstelgedrag.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import achan.nl.uitstelgedrag.api.Backend;

/**
 * Created by Etienne on 18-7-2016.
 */
public class UpdateService extends Service {

    Backend backend;
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void start(){
        backend.checkForUpdate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        backend = new Backend(context);
        start();
        return super.onStartCommand(intent, flags, startId);
    }
}
