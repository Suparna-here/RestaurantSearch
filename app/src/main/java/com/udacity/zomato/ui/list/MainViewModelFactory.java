package com.udacity.zomato.ui.list;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.udacity.zomato.data.TheRestaurantDBRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TheRestaurantDBRepository mRepository;


    public MainViewModelFactory(TheRestaurantDBRepository repository) {
        this.mRepository = repository;

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
