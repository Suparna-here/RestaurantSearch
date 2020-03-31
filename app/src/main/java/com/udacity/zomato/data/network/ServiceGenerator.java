package com.udacity.zomato.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String BASE_URL = "https://developers.zomato.com/api/v2.1/";
    public static final String ORDER_CURRENT_LOCATION = "current_location";
    public static final String ORDER_FAVOURITE = "favourite.desc";
    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_SORT_BY="extra_sort_by";
    public static final String EXTRA_TWO_PANE = "extra_two_pane";
    public static final String FRAGMENT_TAG="detail_fragment";
    public static final long LOCATION_UPDATE_INTERVAL=40*60*1000; //40 minutes
    public static final long FASTEST_LOCATION_UPDATE_INTERVAL=20*60*1000; //20 minutes

    public static final String ACTION_UPDATE_FAVORITE_RESTAURANT = "com.udacity.zomato.widget.action.update_restaurant_name";
    public static final String ACTION_UPDATE_RESTAURANT_WIDGETS = "com.udacity.zomato.widget.action.update_restaurant_widgets";


    public static final String YOUTUBE_URL= "http://img.youtube.com/vi/";
    public static final String ORDER_TOPRATED = "vote_average.desc";

    public static final boolean LOCAL_LOGD = true;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}