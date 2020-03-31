package com.udacity.zomato.ui.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.udacity.zomato.data.TheRestaurantDBRepository;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;

public class DetailActivityViewModel extends ViewModel {
    // Restaurant data the user is looking at
    private final MutableLiveData<Restaurant> mMovie;
    private final Restaurant restaurant;
    private final TheRestaurantDBRepository mRepository;
    private boolean isFavouriteFlag;

    public static final String LOG_TAG = DetailActivityViewModel.class.getSimpleName();

    public DetailActivityViewModel(TheRestaurantDBRepository repository, Restaurant restaurant) {
        this.restaurant = restaurant;
        this.mRepository = repository;
        this.mMovie = mRepository.getFavoriteRestaurantById(restaurant.getId(), restaurant.getName());
    }

    public void setFavouriteFlag(boolean flag) {
        isFavouriteFlag = flag;
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: setFavouriteFlag("+flag+")");
    }

    public boolean isFavouriteFlag() {
        return isFavouriteFlag;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public MutableLiveData<Restaurant> getFavouriteMovie() {
        return mMovie;
    }

    public void deleteMovieFromFavouriteTable(Restaurant restaurant) {
        mRepository.deleteRestaurantFromFavourite(restaurant.getId(), restaurant.getName());
        setFavouriteFlag(false);
    }

    public void insertMovieToFavouriteTable(Restaurant restaurant) {
        mRepository.insertRestaurantToFavourite(restaurant);
        setFavouriteFlag(true);
    }
}
