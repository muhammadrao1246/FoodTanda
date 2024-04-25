package com.example.fooddeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.fooddeliveryapp.ApplicationUtilities.Debugger;
import com.example.fooddeliveryapp.ApplicationUtilities.FragmentHandler;
import com.example.fooddeliveryapp.Fragments.RestaurantProductsFragment;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class RestaurantProductsActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    Debugger debug;
    FragmentHandler fragmentLoader;

    //reference variables
    FrameLayout restaurantProductsContainer;

    MaterialToolbar topAppBar_Restaurant;

    //data variables
    String restaurantName;
    Restaurants restaurant;


    //------Product Selection Adding To Cart Variables
    FrameLayout productSelectionBottomSheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //pre config
        firestore = FirebaseFirestore.getInstance();
        Intent intentData = getIntent();
        restaurantName = intentData.getStringExtra("restaurantName");
        if( restaurantName == null )
        {
            onBackPressed();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_products_activity);

        //post config


        //fetching references
        debug = new Debugger(getApplicationContext(), Toast.LENGTH_SHORT);
        restaurantProductsContainer = findViewById(R.id.restaurantProductsContainer);
        topAppBar_Restaurant = findViewById(R.id.topAppBar_RestaurantProducts);
        productSelectionBottomSheet = findViewById(R.id.restaurantProductSelectedContainer);

        //----Bottom Sheet Configuration
        BottomSheetBehavior productBottomSheetBehaviour = BottomSheetBehavior.from(productSelectionBottomSheet);
        productBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        productSelectionBottomSheet.bringToFront();


//        restaurantName = "howdy";
        //loading restaurant data
        firestore.collection("restaurants")
                .document(restaurantName.toLowerCase())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        restaurant = new Restaurants(documentSnapshot.getString("restaurantName"), documentSnapshot.getString("restaurantImage"), documentSnapshot.get("restaurantFoodCategories"), (GeoPoint) documentSnapshot.get("restaurantLocation"), documentSnapshot.getDouble("restaurantRatings"), documentSnapshot.getDouble("restaurantTotalRatings"));
                                        fragmentLoader = new FragmentHandler(new RestaurantProductsFragment(null, restaurant), RestaurantProductsActivity.this, R.id.restaurantProductsContainer);
                                        fragmentLoader.loadFragment("replace", false);
                                        System.out.println(restaurant);
                                        topAppBar_Restaurant.setTitle(restaurant.restaurantName);
                                    }
                                })
                .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                debug.show("Restaurant Not Found");
                                                onBackPressed();
                                            }
                                        });


        //setting support action bar
        setSupportActionBar(topAppBar_Restaurant);

        //setting listeners

        //----navigation listeners
        topAppBar_Restaurant.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}