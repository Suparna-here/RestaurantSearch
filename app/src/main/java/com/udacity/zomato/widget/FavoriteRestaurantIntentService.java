package com.udacity.zomato.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.udacity.zomato.R;
import com.udacity.zomato.data.network.ServiceGenerator;

public class FavoriteRestaurantIntentService extends IntentService {

    private static final String LOG_TAG = FavoriteRestaurantIntentService.class.getName();

    public FavoriteRestaurantIntentService() {
        super("FavoriteRestaurantIntentService");
    }

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionUpdateFavoriteRestaurant(Context context) {
        Intent intent = new Intent(context, FavoriteRestaurantIntentService.class);
        intent.setAction(ServiceGenerator.ACTION_UPDATE_FAVORITE_RESTAURANT);
        context.startService(intent);
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateRestaurantWidgets(Context context) {
        Intent intent = new Intent(context, FavoriteRestaurantIntentService.class);
        intent.setAction(ServiceGenerator.ACTION_UPDATE_RESTAURANT_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ServiceGenerator.ACTION_UPDATE_FAVORITE_RESTAURANT.equals(action)) {
                handleActionUpdateFavoriteRestaurant();
            } else if (ServiceGenerator.ACTION_UPDATE_RESTAURANT_WIDGETS.equals(action)) {
                handleActionUpdateRestaurantWidgets();
            }

        }
    }

    /**
     * Handle action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateFavoriteRestaurant() {
        // Always update widgets
        if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG,"su:"+"startActionUpdateRecipeWidgets" );

        startActionUpdateRestaurantWidgets(this);
    }


    private void handleActionUpdateRestaurantWidgets() {
        //Update widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FavoriteAppWidgetProvider.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Now update all widgets
        FavoriteAppWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds);
    }
}
