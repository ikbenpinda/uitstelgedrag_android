package xml;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Etienne on 8-5-2016.
 */
public class UitstelgedragRemoteViewsService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new UitstelgedragRemoteViewsFactory(getApplicationContext(), intent);
    }
}
