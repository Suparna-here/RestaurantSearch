package com.udacity.zomato.data.network;

import com.udacity.zomato.data.database.ResturantResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class ZomatoService {
    public interface ZomatoAPI {

        @Headers(
                "Accept: application/json"
        )
        @GET("search?")
        Call<ResturantResponse> getRestaurantList(@Header("user-key") String user_key,
                                                  @Query("lat") String lat, @Query("lon") String lon, @Query("radius") String radius,
                                                  @Query("cuisines") String cuisines, @Query("sort") String sort, @Query("order") String order);
    }
}
