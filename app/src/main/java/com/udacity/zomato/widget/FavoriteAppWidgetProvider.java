package com.udacity.zomato.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.zomato.R;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.ui.list.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FavoriteAppWidgetProvider extends AppWidgetProvider {
    public static final String TAG=FavoriteAppWidgetProvider.class.getSimpleName();

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews rv = getFavoriteListRemoteView(context);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the ListView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the ListView mode widget
     */
    private static RemoteViews getFavoriteListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_restaurants_app_widget);
        // Set the ListWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(appIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent appPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//      PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        // Handle empty favorite restaurants
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);
        return views;
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(TAG, "su: updateRecipeWidgets");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       // There may be multiple widgets active, so update all of them
       FavoriteRestaurantIntentService.startActionUpdateRestaurantWidgets(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        FavoriteRestaurantIntentService.startActionUpdateRestaurantWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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

