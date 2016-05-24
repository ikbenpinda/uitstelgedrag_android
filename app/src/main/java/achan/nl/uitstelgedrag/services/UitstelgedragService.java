package achan.nl.uitstelgedrag.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Etienne on 12-5-2016.
 */
public class UitstelgedragService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public UitstelgedragService() {
        super();
    }
}
