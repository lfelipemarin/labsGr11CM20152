package co.edu.udea.cmovil.gr11.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class YambaWidget extends AppWidgetProvider {

    private static final String TAG = YambaWidget.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, YambaWidget.class)));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");

        Cursor cursor = context.getContentResolver().query(StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
        if (!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));

        PendingIntent operation = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yamba_widget);

            views.setTextViewText(R.id.list_item_text_user, user);
            views.setTextViewText(R.id.list_item_text_message, message);
            views.setTextViewText(R.id.list_item_text_created_at, DateUtils.getRelativeTimeSpanString(createdAt));
            views.setOnClickPendingIntent(R.id.list_item_text_user, operation);
            views.setOnClickPendingIntent(R.id.list_item_text_message, operation);

            appWidgetManager.updateAppWidget(appWidgetId, views);

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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yamba_widget);
        views.setTextViewText(R.id.list_item_text_message, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}

