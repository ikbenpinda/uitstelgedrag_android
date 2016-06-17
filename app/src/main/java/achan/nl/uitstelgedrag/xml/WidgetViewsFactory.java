package achan.nl.uitstelgedrag.xml;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
public class WidgetViewsFactory implements WidgetService.RemoteViewsFactory{

    List<Task> tasks;
    Context context;
    Intent intent;

    public WidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        tasks = new TaskGateway(context).getAll();
    }

    @Override
    public void onDataSetChanged() {
        tasks = new TaskGateway(context).getAll();
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
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_rowlayout);
        views.setTextViewText(R.id.widget_id_textview, String.valueOf(tasks.get(position).id));
        views.setTextViewText(R.id.widget_desc_textview, tasks.get(position).description);
        Log.i("Widget", "View set to task("+tasks.get(position).description+")");

        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        //Bundle extras = new Bundle();
        //extras.putInt(WidgetProvider.OPEN_APP, position);
        //Intent fillInIntent = new Intent();
        //fillInIntent.putExtras(extras);

        // Make it possible to distinguish the individual on-click
        // action of a given item
        //views.setOnClickFillInIntent(R.id.widget_listviewitem, fillInIntent);

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
        return true;
    }
}
