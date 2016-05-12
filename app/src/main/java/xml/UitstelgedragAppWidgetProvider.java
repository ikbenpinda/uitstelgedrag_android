package xml;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import achan.nl.uitstelgedrag.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link UitstelgedragAppWidgetConfigureActivity UitstelgedragAppWidgetConfigureActivity}
 */
public class UitstelgedragAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = UitstelgedragAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.uitstelgedrag_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        //https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview
        //RemoteViews Service needed to provide adapter for ListView
        Intent serviceIntent = new Intent(context, UitstelgedragRemoteViewsService.class);
        //passing app widget id to that RemoteViews Service
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        serviceIntent.setData(Uri.parse(
                serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        views.setRemoteAdapter(appWidgetId, serviceIntent);
        //setting an empty view in case of no data
        //views.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            UitstelgedragAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

