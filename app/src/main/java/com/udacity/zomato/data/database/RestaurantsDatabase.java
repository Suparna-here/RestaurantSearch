package com.udacity.zomato.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities={Restaurant.class},version=1, exportSchema = false)
public abstract class RestaurantsDatabase extends RoomDatabase {

    public abstract RestaurantsDao moviesDao();

    private static final String DATABASE_NAME="favourite_restaurants";
    //For Singleton instantiation
    private static final Object LOCK=new Object();
    private static volatile RestaurantsDatabase mInstance;

    public static RestaurantsDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            RestaurantsDatabase.class, RestaurantsDatabase.DATABASE_NAME).build();
                }
            }
        }
        return mInstance;
    }
}
