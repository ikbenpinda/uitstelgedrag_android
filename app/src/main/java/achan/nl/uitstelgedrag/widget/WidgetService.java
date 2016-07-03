package achan.nl.uitstelgedrag.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Etienne on 8-5-2016.
 */
public class WidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetViewsFactory dataProvider = new WidgetViewsFactory(
                getApplicationContext(), intent);
        return dataProvider;
    }
}
