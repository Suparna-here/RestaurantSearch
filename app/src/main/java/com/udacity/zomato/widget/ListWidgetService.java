package com.udacity.zomato.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.zomato.R;
import com.udacity.zomato.data.TheRestaurantDBRepository;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.utils.InjectorUtils;

import java.util.List;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Restaurant> restaurantList;
    public static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onCreate");
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        Log.d(LOG_TAG, "su: onDataSetChanged");
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: doing databse operation");
        TheRestaurantDBRepository repository = InjectorUtils.provideRepository(mContext);
        restaurantList = repository.getFavoriteRestaurantsData();

        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: onDataSetChanged Received Database data " + restaurantList);
    }

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        if (restaurantList == null) return 0;
        return restaurantList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.favorite_restaurant_widget_row);
        views.setTextViewText(R.id.widget_restaurant_name, restaurantList.get(position).getName());
        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
       /* Bundle extras = new Bundle();
        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);*/
        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_row_layout, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int position) {
        return restaurantList.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
