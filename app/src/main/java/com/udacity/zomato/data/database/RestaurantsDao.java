package com.udacity.zomato.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieToFavourite(Restaurant restaurant);

    @Query("SELECT * from favourite_restaurants WHERE id=:Id and name=:name")
    LiveData<Restaurant> getFavouriteMovieById(long Id, String name);

    @Query("SELECT * from favourite_restaurants")
    List<Restaurant> getFavoriteRestaurantsData();

    @Query("SELECT * from favourite_restaurants")
    LiveData<List<Restaurant>> getFavouriteRestaurants();

    @Query("SELECT count(*) from favourite_restaurants")
    int countAllFavouriteRestaurants();

    @Query("SELECT count(*) from favourite_restaurants  WHERE id=:Id and name=:name")
    int countInFavourite(long Id, String name);

    @Query("DELETE from favourite_restaurants WHERE id=:Id and name=:name")
    void deleteMovieFromFavourite(long Id, String name);
}
