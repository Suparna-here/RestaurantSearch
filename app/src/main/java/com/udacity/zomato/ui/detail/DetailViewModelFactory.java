package com.udacity.zomato.ui.detail;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.udacity.zomato.data.TheRestaurantDBRepository;
import com.udacity.zomato.data.database.Restaurant;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    private final TheRestaurantDBRepository mRepository;
    private final Restaurant restaurant;

    public DetailViewModelFactory(TheRestaurantDBRepository repository, Restaurant restaurant) {
        this.mRepository = repository;
        this.restaurant = restaurant;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailActivityViewModel(mRepository,restaurant);
    }
}
