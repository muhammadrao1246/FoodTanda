package com.example.fooddeliveryapp.Models;

import java.util.Map;

public class Products {
    public String productName, productDescription, restaurantName, productCuisine, productImage;
    public Double productPrice;
    public Map<String, Double> productTypes;

    public Products(String productName, String productDescription, String restaurantName, String productCuisine, String productImage, Double productPrice, Map<String, Double> productTypes) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.restaurantName = restaurantName;
        this.productCuisine = productCuisine;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productTypes = productTypes;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", productCuisine='" + productCuisine + '\'' +
                ", productImage='" + productImage + '\'' +
                ", productPrice=" + productPrice +
                ", productTypes=" + productTypes +
                '}';
    }
}
