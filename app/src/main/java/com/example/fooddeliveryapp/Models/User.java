package com.example.fooddeliveryapp.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class User {
    String userName, userEmail, userNumber;
    GeoPoint userLocation;


    public User(String userName, String userEmail, String userNumber, GeoPoint userLocation) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
        this.userLocation = userLocation;
    }

    public static Map<String, Object> getMap(String userName, String userEmail, String userNumber, String userPassword, GeoPoint userLocation , String userPhoto)
    {
        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("userName", userName);
        temp.put("userEmail", userEmail);
        temp.put("userNumber", userNumber);
        temp.put("userPassword", userPassword);
        temp.put("userLocation", userLocation);
        temp.put("userPhoto", userPhoto);
        return temp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public GeoPoint getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(GeoPoint userLocation) {
        this.userLocation = userLocation;
    }
}
