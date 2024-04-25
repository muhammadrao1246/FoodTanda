package com.example.fooddeliveryapp.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Restaurants {
    public String restaurantName, restaurantImage;
    public Double restaurantRatings;
    public Double restaurantTotalRatings;
    public ArrayList<String> restaurantFoodCategories;
    public GeoPoint restaurantLocation;
    public String restaurantEstimatedDeliveryTime;
    public String restaurantDeliveryFee;

    public Restaurants(String restaurantName, String restaurantImage, Object restaurantFoodCategories, GeoPoint restaurantLocation, Double restaurantRatings, Double restaurantTotalRatings)
    {
        this.restaurantName = restaurantName;
        this.restaurantRatings = restaurantRatings;
        this.restaurantTotalRatings = restaurantTotalRatings;
        this.restaurantFoodCategories = (ArrayList<String>) restaurantFoodCategories;
        this.restaurantLocation = (GeoPoint) restaurantLocation;
        this.restaurantImage = restaurantImage;
    }

    @Override
    public String toString() {
        return "Restaurants{" +
                "restaurantName='" + restaurantName + '\'' +
                ", restaurantRatings='" + restaurantRatings + '\'' +
                ", restaurantFoodCategories='" + restaurantFoodCategories + '\'' +
                ", restaurantLocation=" + restaurantLocation +
                ", restaurantEstimatedDeliveryTime='" + restaurantEstimatedDeliveryTime + '\'' +
                ", restaurantDeliveryFee='" + restaurantDeliveryFee + '\'' +
                '}';
    }
}
