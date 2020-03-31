package com.udacity.zomato.data.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

@Entity(tableName = "favourite_restaurants", primaryKeys = {"id", "name"}, indices = {@Index(value = {"url"}, unique = true)})
public class Restaurant implements Parcelable {
    /*"id":"16771079",
      "name":"Lombardi's Pizza",
      "url":"https://www.zomato.com/new-york-city/lombardis-pizza-lower-east-side?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1",*/
    @NonNull
    private long id;
    @NonNull
    private String name;
    private String url;

    //"location"
    @Ignore
    private RestaurantLocation location;

    //"cuisines":"Pizza, Italian",
    private String cuisines;

    //"average_cost_for_two":50,
    private String average_cost_for_two;

    //"user_rating"
    @Ignore
    private User_Rating user_rating;

   /* "user_rating":{
                "aggregate_rating":"4.9",
                "rating_text":"Excellent",
                "rating_color":"3F7E00",
                "rating_obj":{
            "title":{
                "text":"4.9"
            },
            "bg_color":{
                "type":"lime",
                        "tint":"800"
            }
        },
        "votes":"1442"
    },*/

    //"featured_image"
    private String featured_image;

    //"thumb"
    private String thumb;

    // Variables for DB
    private String restaurant_full_address;
    private String restaurant_aggregate_rating;
    private String restaurant_votes;
    private String restaurant_latLng;

    public String getRestaurant_full_address() {
        return restaurant_full_address;
    }

    public String getRestaurant_aggregate_rating() {
        return restaurant_aggregate_rating;
    }

    public String getRestaurant_votes() {
        return restaurant_votes;
    }

    public String getRestaurant_latLng() {
        return restaurant_latLng;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public String getThumb() {
        return thumb;
    }

    public User_Rating getUser_rating() {
        return user_rating;
    }

    public RestaurantLocation getLocation() {
        return location;
    }

    public String getCuisines() {
        return cuisines;
    }

    public String getAverage_cost_for_two() {
        return average_cost_for_two;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Restaurant(long id, String name, String url, String cuisines, String average_cost_for_two, String thumb, String featured_image, String restaurant_full_address,
                      String restaurant_aggregate_rating, String restaurant_votes, String restaurant_latLng) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.cuisines = cuisines;
        this.average_cost_for_two = average_cost_for_two;
        this.thumb = thumb;
        this.featured_image = featured_image;
        this.restaurant_full_address = restaurant_full_address;
        this.restaurant_aggregate_rating = restaurant_aggregate_rating;
        this.restaurant_votes = restaurant_votes;
        this.restaurant_latLng = restaurant_latLng;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public Restaurant(Parcel in) {
        id = in.readLong();
        name = in.readString();
        url = in.readString();
        location = (RestaurantLocation) in.readParcelable(RestaurantLocation.class.getClassLoader());
        cuisines = in.readString();
        average_cost_for_two = in.readString();
        user_rating = (User_Rating) in.readParcelable(User_Rating.class.getClassLoader());
        thumb = in.readString();
        featured_image = in.readString();

        restaurant_full_address = in.readString();
        restaurant_aggregate_rating = in.readString();
        restaurant_votes = in.readString();
        restaurant_latLng = in.readString();


//        List<Integer> genre_ids
//        genre_ids = (ArrayList<Integer>) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeLong(id);
        destParcel.writeString(name);
        destParcel.writeString(url);
        destParcel.writeParcelable(location, flags);
        destParcel.writeString(cuisines);
        destParcel.writeString(average_cost_for_two);
        destParcel.writeParcelable(user_rating, flags);
        destParcel.writeString(thumb);
        destParcel.writeString(featured_image);

        destParcel.writeString(restaurant_full_address);
        destParcel.writeString(restaurant_aggregate_rating);
        destParcel.writeString(restaurant_votes);
        destParcel.writeString(restaurant_latLng);
       /* destParcel.writeValue(video);
        destParcel.writeDouble(vote_average);
        destParcel.writeSerializable(genre_ids);*/
    }

}
