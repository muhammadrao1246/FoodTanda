package com.example.fooddeliveryapp.Fragments;

import android.icu.number.NumberFormatter;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddeliveryapp.Adapters.RestaurantCategoriesAdapter;
import com.example.fooddeliveryapp.Adapters.RestaurantProductsAdapter;
import com.example.fooddeliveryapp.ApplicationUtilities.Debugger;
import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Products;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.example.fooddeliveryapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantProductsFragment extends Fragment
{
    FirebaseFirestore firestore;
    Debugger debug;
    Handler.Callback onProductClick;
    Restaurants restaurant;

    ImageView restaurantImage;
    ShimmerFrameLayout shimmerRestaurantImage;
    TextView restaurantName, restaurantFoodCategories, restaurantRatings, restaurantTotalRatings;
    MaterialButton restaurantAllReviews;

    RecyclerView restaurantCategoriesContainer;

    public RestaurantProductsFragment(Handler.Callback onProductClick, Restaurants restaurant) {
        this.onProductClick = onProductClick;
        this.restaurant = restaurant;
    }


    public void fetchAndLoadDataFromDatabase()
    {
        ArrayList<String> cuisineNames = restaurant.restaurantFoodCategories;
        HashMap<String, ArrayList<Products>> eachCuisineProducts = new HashMap<>();
        for( String cuisineName : cuisineNames )
        {
            eachCuisineProducts.put(cuisineName, new ArrayList<>());
        }


            firestore.collection("products")
                    .whereEqualTo("restaurantName", restaurant.restaurantName.toLowerCase())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> all_products = queryDocumentSnapshots.getDocuments();
                            for( DocumentSnapshot row : all_products )
                            {
                                Map<String, Double> productTypes = (Map<String, Double>) row.get("productType");

                                ArrayList<Products> cuisineProduct = eachCuisineProducts.get(row.getString("productCuisine"));
                                cuisineProduct.add(new Products(row.getString("productName"), row.getString("productDescription"), row.getString("restaurantName"), row.getString("productCuisine"), row.getString("productImage"), row.getDouble("productPrice"), productTypes));
                                eachCuisineProducts.put(row.getString("productCuisine"), cuisineProduct);
                            }

                            //delete the empty key from map
                            for ( String checkEmptyCuisine : cuisineNames )
                            {
                                if( eachCuisineProducts.get(checkEmptyCuisine).isEmpty() ) {
                                    eachCuisineProducts.remove(checkEmptyCuisine);
                                }
                            }
                            System.out.println(eachCuisineProducts);

                            RestaurantCategoriesAdapter adapter = new RestaurantCategoriesAdapter(
                                    getContext(),
                                    eachCuisineProducts,
                                    R.layout.restaurant_category_layout,
                                    new RestaurantProductsAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Products product) {
                                            debug.show(product.productName);
                                        }
                                    });
                            restaurantCategoriesContainer.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            restaurantCategoriesContainer.setAdapter(null);

                        }
                    });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_products, container, false);
        firestore = FirebaseFirestore.getInstance();
        debug = new Debugger(getContext(), Toast.LENGTH_SHORT);

        //fetching references
        restaurantImage = view.findViewById(R.id.restaurantImage);
        restaurantName = view.findViewById(R.id.restaurantName);
        restaurantFoodCategories = view.findViewById(R.id.restaurantFoodCategories);
        restaurantRatings = view.findViewById(R.id.restaurantRatings);
        restaurantTotalRatings = view.findViewById(R.id.restaurantTotalRatings);
        restaurantAllReviews = view.findViewById(R.id.restaurantAllReviews);
        shimmerRestaurantImage = view.findViewById(R.id.shimmerRestaurantImage);
        restaurantCategoriesContainer = view.findViewById(R.id.restaurantCategoriesContainer);

        //setting recyler settings
        restaurantCategoriesContainer.setLayoutManager(new LinearLayoutManager(getContext()));

        //fetching data from database
        fetchAndLoadDataFromDatabase();

        //inserting restaurant data
        shimmerRestaurantImage.showShimmer(true);
        FirebaseImageStorage.putImageOnImageView(restaurant.restaurantImage, restaurantImage, shimmerRestaurantImage);
        restaurantName.setText(restaurant.restaurantName);
        String foodCategories = "$$$ ·";
        for( String cuisine : restaurant.restaurantFoodCategories )
        {
            cuisine = StringFormatter.capitalizeWord(cuisine);
            if( restaurant.restaurantFoodCategories.get(0).equalsIgnoreCase(cuisine) )
            {
                foodCategories += " " + cuisine;
            }
            else{
                foodCategories += ", " + cuisine;
            }
        }
        restaurantFoodCategories.setText(foodCategories);
        restaurantRatings.setText(getRating());
        restaurantTotalRatings.setText("("+getTotalRatingRoundedOff()+"+)");

        return view;
    }

    public String getRating( )
    {
        if( restaurant.restaurantRatings <= 0 )
            return "0.0";
        Double temp = (restaurant.restaurantRatings / (double)restaurant.restaurantTotalRatings);

        return String.format("%.1f", temp);
    }

    public Integer getTotalRatingRoundedOff()
    {
        Integer percentage = ((int) (restaurant.restaurantTotalRatings / 100.0));
        Integer totalRatingsRoundedOff = percentage * 100;
        if( restaurant.restaurantTotalRatings <= 100 )
            return restaurant.restaurantTotalRatings.intValue();
        return totalRatingsRoundedOff;
    }
}