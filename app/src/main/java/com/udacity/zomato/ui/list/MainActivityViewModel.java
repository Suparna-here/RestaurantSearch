package com.udacity.zomato.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.udacity.zomato.data.TheRestaurantDBRepository;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.network.ServiceGenerator;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    public static final String LOG_TAG=MainActivityViewModel.class.getSimpleName();
    // movie list the user is looking at
    private final MutableLiveData<List<Restaurant>> mMovieList;
    private final TheRestaurantDBRepository mRepository;
    private String sort_by;

    public MainActivityViewModel(TheRestaurantDBRepository repository) {
        mRepository=repository;
        mMovieList=mRepository.getRestaurantsListFromRepository();
        if(mRepository.getSort_by()==null) {
            sort_by = ServiceGenerator.ORDER_CURRENT_LOCATION;
        }else{
            sort_by =mRepository.getSort_by();
        }
    }


    public LiveData<List<Restaurant>> getMoviesList(){
        return mMovieList;
    }

    public void setMoviesBasedOnLocationOrderInViewModel(String lat, String lon, String sort_order) {
        this.sort_by=sort_order;
        mRepository.setRestaurantsFromZomatoServer(lat, lon, sort_by);
        if(ServiceGenerator.LOCAL_LOGD)
        Log.d(LOG_TAG,"su: setMoviesBasedOnLocationOrderInViewModel "+sort_by);
    }

    public void setMoviesBasedOnFavouriteOrderInViewModel(String sort_order) {
        this.sort_by=sort_order;
        mRepository.setRestaurantsBasedOnFavourite(sort_by);
        if(ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG,"su: setMoviesBasedOnFavouriteOrderInViewModel "+sort_by);
    }

    String getSort_by() {
       return sort_by;
    }

    void setSort_by(String sort_by) {
        this.sort_by = sort_by;
    }
}
