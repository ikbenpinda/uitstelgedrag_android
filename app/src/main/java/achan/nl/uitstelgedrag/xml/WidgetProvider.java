package achan.nl.uitstelgedrag.xml;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import achan.nl.uitstelgedrag.R;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

//    public static final String VIEW_ITEM = "achan.nl.uitstelgedrag.xml.VIEW_ITEM";
//    public static final String OPEN_APP = "achan.nl.uitstelgedrag.xml.OPEN_APP";

    @Override
    public void onReceive(Context context, Intent intent) {
//        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//        if (intent.getAction().equals(OPEN_APP)) {
//            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID);
//            int viewIndex = intent.getIntExtra(OPEN_APP, 0);
//            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
//        }
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
//        //views.setEmptyView(R.id.widget_listview, R.id.empty_view);

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
//            toastIntent.setAction(WidgetProvider.OPEN_APP);
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
            RemoteViews mView = initViews(context, appWidgetManager, widgetId);
            appWidgetManager.updateAppWidget(widgetId, mView);
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

