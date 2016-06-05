package xml;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;

/**
 *
 *
 * = Service.
 *
 * Created by Etienne on 8-5-2016.
 */
public class UitstelgedragRemoteViewsFactory implements UitstelgedragRemoteViewsService.RemoteViewsFactory{

    List<Task> tasks;
    Context context;
    Intent intent;

    public UitstelgedragRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        tasks = new TaskGateway(context).getAll();
    }

    @Override
    public void onDataSetChanged() {
        // ?
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rowlayout_widget);
        views.setTextViewText(R.id.widget_desc_textview, "xxx");//datasource.get(position).description);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return datasource.get(position).id;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
