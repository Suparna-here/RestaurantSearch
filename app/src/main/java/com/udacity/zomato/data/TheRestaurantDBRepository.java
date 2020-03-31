package com.udacity.zomato.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.udacity.zomato.AppExecutors;
import com.udacity.zomato.BuildConfig;
import com.udacity.zomato.data.database.Restaurant;
import com.udacity.zomato.data.database.Restaurant_;
import com.udacity.zomato.data.database.RestaurantsDao;
import com.udacity.zomato.data.database.ResturantResponse;
import com.udacity.zomato.data.network.ServiceGenerator;
import com.udacity.zomato.data.network.ZomatoService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TheRestaurantDBRepository {
    private static final String LOG_TAG = TheRestaurantDBRepository.class.getSimpleName();
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static TheRestaurantDBRepository mInstance;
    private final RestaurantsDao mRestaurantsDao;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;
    private MutableLiveData<List<Restaurant>> restaurantsData;
    private MutableLiveData<Restaurant> restaurantMutableLiveData;
    private String sort_by;

    private TheRestaurantDBRepository(RestaurantsDao moviesDao, AppExecutors executors) {
        mRestaurantsDao = moviesDao;
        mExecutors = executors;
        restaurantsData = new MutableLiveData<List<Restaurant>>();
        restaurantMutableLiveData = new MutableLiveData<Restaurant>();
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsListFromRepository() {
        return restaurantsData;
    }

    public synchronized static TheRestaurantDBRepository getInstance(RestaurantsDao mRestaurantsDao, AppExecutors executors) {
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: Getting the repository");
        if (mInstance == null) {
            synchronized (LOCK) {
                mInstance = new TheRestaurantDBRepository(mRestaurantsDao, executors);
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: Made new repository");
            }
        }
        return mInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    public synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;
    }

    /**
     * Database related operations
     **/
    public void insertRestaurantToFavourite(final Restaurant restaurant) {
        initializeData();
        Handler handler = new Handler(Looper.getMainLooper());
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRestaurantsDao.insertMovieToFavourite(restaurant);
                restaurantMutableLiveData.postValue(restaurant);
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: restaurant Insertion");

                final LiveData<List<Restaurant>> movieList = mRestaurantsDao.getFavouriteRestaurants();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        movieList.observeForever(new Observer<List<Restaurant>>() {
                            @Override
                            public void onChanged(@Nullable List<Restaurant> restaurants) {
                                if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
                                    restaurantsData.postValue(restaurants);
                                    if (ServiceGenerator.LOCAL_LOGD)
                                        Log.d(LOG_TAG, "su: insertRestaurantToFavourite Select LiveData Number of Favourite restaurants=" + movieList.getValue().size());
                                }
                            }
                        });
                    }
                });

            }
        });

    }

    public void deleteRestaurantFromFavourite(final long id, final String restaurant_name) {
        initializeData();
        Handler handler = new Handler(Looper.getMainLooper());
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mRestaurantsDao.countInFavourite(id, restaurant_name);
                if (count > 0) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: count =" + count + " deleteRestaurantFromFavourite " + id);
                    mRestaurantsDao.deleteMovieFromFavourite(id, restaurant_name);
                    restaurantMutableLiveData.postValue(null);
                } else {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: No movie for Deletion");
                }

                final LiveData<List<Restaurant>> movieList = mRestaurantsDao.getFavouriteRestaurants();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        movieList.observeForever(new Observer<List<Restaurant>>() {
                            @Override
                            public void onChanged(@Nullable List<Restaurant> restaurants) {
                                if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
                                    restaurantsData.postValue(restaurants);
                                    if (ServiceGenerator.LOCAL_LOGD)
                                        Log.d(LOG_TAG, "su: deleteRestaurantFromFavourite Select LiveData Number of Favourite restaurants=" + restaurants.size());
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void setFavouriteRestaurantById(final long movieId, final String restaurant_name) {
        initializeData();
        Handler handler = new Handler(Looper.getMainLooper());
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final LiveData<Restaurant> movieLiveData = mRestaurantsDao.getFavouriteMovieById(movieId, restaurant_name);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        movieLiveData.observeForever(new Observer<Restaurant>() {
                            @Override
                            public void onChanged(@Nullable Restaurant restaurant) {
                                restaurantMutableLiveData.postValue(movieLiveData.getValue());
                            }
                        });
                    }
                });
            }
        });
    }

    public MutableLiveData<Restaurant> getFavoriteRestaurantById(final long movieId, String restaurant_name) {
        setFavouriteRestaurantById(movieId, restaurant_name);
        return restaurantMutableLiveData;
    }

    public void setRestaurantsBasedOnFavourite(String sort_order) {
        this.sort_by = sort_order;
        initializeData();
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: returning setRestaurantsBasedOnFavourite " + sort_by);
        Handler handler = new Handler(Looper.getMainLooper());
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int count = mRestaurantsDao.countAllFavouriteRestaurants();
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d(LOG_TAG, "su: number of Favourite movies " + count);
                final LiveData<List<Restaurant>> movieList = mRestaurantsDao.getFavouriteRestaurants();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        movieList.observeForever(new Observer<List<Restaurant>>() {
                            @Override
                            public void onChanged(@Nullable List<Restaurant> restaurants) {
                                if (sort_by.equals(ServiceGenerator.ORDER_FAVOURITE)) {
                                    restaurantsData.postValue(restaurants);
                                    if (ServiceGenerator.LOCAL_LOGD)
                                        Log.d(LOG_TAG, "su: setRestaurantsBasedOnFavourite Select LiveData Number of Favourite restaurants=" + restaurants.size());
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    public List<Restaurant> getFavoriteRestaurantsData() {
        initializeData();
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: returning getMoviesBasedOnFavourite()");

        int count = mRestaurantsDao.countAllFavouriteRestaurants();
        if (ServiceGenerator.LOCAL_LOGD)
            Log.d(LOG_TAG, "su: number of Favourite restaurants " + count);
        List<Restaurant> favoriteRestaurantList = mRestaurantsDao.getFavoriteRestaurantsData();
        return favoriteRestaurantList;
    }

    public String getSort_by() {
        return sort_by;
    }

    /**
     * This is used to get the Movies response from TMDB Server
     */
    public void setRestaurantsFromZomatoServer(String lat, String lon, String sort_order) {
        this.sort_by = sort_order;
        initializeData();
        ZomatoService.ZomatoAPI client = ServiceGenerator.createService(ZomatoService.ZomatoAPI.class);
        Call<ResturantResponse> call = client.getRestaurantList(BuildConfig.API_KEY,lat, lon, "10000", "55", "rating", "desc");

//        call = client.getRestaurantList("40.742051", "-74.004821", "10000", "55", "rating", "desc");
        /*if (sort_by.equals(ServiceGenerator.ORDER_POPULARITY)| sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            call = client.getRestaurantList("40.742051", "-74.004821", "10000", "55", "rating", "desc");
        }   else if (sort_by.equals(ServiceGenerator.ORDER_TOPRATED)) {
            call = client.getTopRatedMovieList(BuildConfig.API_KEY);
        }*/

        call.enqueue(new Callback<ResturantResponse>() {
            @Override
            public void onResponse(Call<ResturantResponse> call, Response<ResturantResponse> response) {
                if (!sort_by.equals(ServiceGenerator.ORDER_CURRENT_LOCATION)) return;
                if (response.isSuccessful()) {
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network SUCCESS");
                    // user object available
                    List<Restaurant> movielist = new ArrayList<Restaurant>();

                    List<Restaurant_> movie_list = response.body().getRestaurant_List();
                    for (Restaurant_ restaurant_ : movie_list) {
                        movielist.add(restaurant_.getRestaurant());
                    }
                    restaurantsData.postValue(movielist);
                } else {
                    // error response, no access to resource?
                    // user object not available
                    if (ServiceGenerator.LOCAL_LOGD)
                        Log.d(LOG_TAG, "su: Network Error");
                    restaurantsData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResturantResponse> call, Throwable t) {
                // something went completely south (like no internet connection)
                if (ServiceGenerator.LOCAL_LOGD)
                    Log.d("Network", "No Internet" + t.getMessage());
                restaurantsData.postValue(null);
            }
        });
    }
}
