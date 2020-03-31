package com.udacity.zomato.data.database;

import java.util.List;

public class ResturantResponse {
//    "restaurants"
    private List<Restaurant_> restaurants;

    public ResturantResponse(List<Restaurant_> restaurants) {
        this.restaurants = restaurants;
    }

    public List<Restaurant_> getRestaurant_List() {
        return restaurants;
    }
}
