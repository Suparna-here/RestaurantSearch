package com.udacity.zomato.utils;

import android.content.Context;
import android.util.Log;

import com.udacity.zomato.AppExecutors;
import com.udacity.zomato.data.TheRestaurantDBRepository;
import com.udacity.zomato.data.database.RestaurantsDatabase;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.ui.detail.DetailViewModelFactory;
import com.udacity.zomato.ui.list.MainViewModelFactory;

public class InjectorUtils {
    private static final String LOG_TAG =InjectorUtils.class.getName();

    public static TheRestaurantDBRepository provideRepository(Context context ) {
        RestaurantsDatabase database = RestaurantsDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su: get an instance of TheRestaurantDBRepository");
        return TheRestaurantDBRepository.getInstance(database.moviesDao(),executors);
    }

   public static DetailViewModelFactory provideDetailViewModelFactory(Context context, Restaurant restaurant) {
        TheRestaurantDBRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, restaurant);
    }

     public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        TheRestaurantDBRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }
}
