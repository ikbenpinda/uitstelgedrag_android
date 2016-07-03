package achan.nl.uitstelgedrag.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.ui.activities.OverviewActivity;
import achan.nl.uitstelgedrag.ui.activities.TaskActivity;
import achan.nl.uitstelgedrag.ui.activities.TaskDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    /*
     * Communicating from the widget to the app happens through messages.
     * Since these need to be consistent, use constants.
     */

    public static final String OPEN_APP  = "achan.nl.uitstelgedrag.xml.OPEN_APP";
    public static final String VIEW_ITEM = "achan.nl.uitstelgedrag.xml.VIEW_ITEM";
    public static final String ADD_ITEM  = "achan.nl.uitstelgedrag.xml.ADD_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {
            //AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        Log.i("Widget", "Received intent with action: " + intent.getAction());

        int id = -1;
        switch (intent.getAction()){
            case OPEN_APP:
                Intent openAppIntent = new Intent(context, OverviewActivity.class);
                openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openAppIntent);
                break;
            case VIEW_ITEM:
                id = intent.getExtras().getInt("task_id");
                Intent openAppAndViewTaskIntent = new Intent(context, TaskDetailActivity.class);
                openAppAndViewTaskIntent.putExtra("task_id", id);
                openAppAndViewTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openAppAndViewTaskIntent);
                break;
            case ADD_ITEM:
                Intent openAppAndAddTaskIntent = new Intent(context, TaskActivity.class);
                openAppAndAddTaskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openAppAndAddTaskIntent);
                break;
        }
        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
//
//        CharSequence widgetText = UitstelgedragAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        //https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview
//        //RemoteViews Service needed to provide adapter for ListView
//        Intent serviceIntent = new Intent(context, WidgetService.class);
//        //passing app widget id to that RemoteViews Service
//        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        //setting a unique Uri to the intent
//        //don't know its purpose to me right now
//        serviceIntent.setData(Uri.parse(
//                serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
//
//        //setting adapter to listview of the widget
//        views.setRemoteAdapter(appWidgetId, serviceIntent);
//
//        //setting an empty view in case of no data
//        //views.setEmptyView(R.id.widget_listview, R.id.empty_view); FIXME

        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int i = 0; i < appWidgetIds.length; i++) {
//
//
//            // Sets up the intent that points to the StackViewService that will
//            // provide the views for this collection.
//            Intent intent = new Intent(context, WidgetService.class);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//
//            // When intents are compared, the extras are ignored, so we need to embed the extras
//            // into the data so that the extras will not be ignored.
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
//            rv.setRemoteAdapter(R.id.widget_listview, intent);
//
//            // The empty view is displayed when the collection has no items. It should be a sibling
//            // of the collection view.
//            rv.setEmptyView(R.id.widget_listview, R.id.widget_empty_list);
//
//            // This section makes it possible for items to have individualized behavior.
//            // It does this by setting up a pending intent template. Individuals items of a collection
//            // cannot set up their own pending intents. Instead, the collection as a whole sets
//            // up a pending intent template, and the individual items set a fillInIntent
//            // to create unique behavior on an item-by-item basis.
//            Intent toastIntent = new Intent(context, WidgetProvider.class);
//
//            // Set the action for the intent.
//            // When the user touches a particular view, it will have the effect of
//            // broadcasting TOAST_ACTION.
//            toastIntent.setAction(WidgetProvider.ADD_ITEM);
//            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setPendingIntentTemplate(R.id.widget_listview, toastPendingIntent);
//
//            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
//
//
//            updateAppWidget(context, appWidgetManager, i);
//        }

        for (int widgetId : appWidgetIds) {
            RemoteViews views = initViews(context, appWidgetManager, widgetId);

            // Adding collection list item handler
            final Intent onItemClick = new Intent(context, WidgetProvider.class);
            onItemClick.setAction(VIEW_ITEM);
            onItemClick.setData(Uri.parse(onItemClick
                    .toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_listview,
                    onClickPendingIntent);

            // Adding event handler for "new task" button
            final Intent onNewTaskClick = new Intent(context, WidgetProvider.class);
            onNewTaskClick.setAction(ADD_ITEM);
            onNewTaskClick.setData(Uri.parse(onNewTaskClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onNewTaskClickPendingIntent = PendingIntent.getBroadcast(context, 0, onNewTaskClick, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_addtask_button, onNewTaskClickPendingIntent);

            // Adding event handler for title header
            final Intent onHeaderClick = new Intent(context, WidgetProvider.class);
            onHeaderClick.setAction(OPEN_APP);
            onHeaderClick.setData(Uri.parse(onHeaderClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onHeaderClickPendingIntent = PendingIntent.getBroadcast(context, 0, onHeaderClick, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_header, onHeaderClickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        //for (int appWidgetId : appWidgetIds) {
            //UitstelgedragAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        //}
    }

    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widget_listview, intent);

        return mView;
    }
}

