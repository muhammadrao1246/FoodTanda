package com.example.fooddeliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.fooddeliveryapp.Adapters.DashboardCuisinesAdapter;
import com.example.fooddeliveryapp.Adapters.DashboardRestaurantsAdapter;
import com.example.fooddeliveryapp.ApplicationUtilities.Debugger;
import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.ImageProgressIndicator;
import com.example.fooddeliveryapp.ApplicationUtilities.LoaderActivator;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Cuisines;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.crisp.client.ChatActivity;
import im.crisp.client.Crisp;

public class App_Dashboard extends AppCompatActivity {

    String userName, userEmail, userImagePath;
    LoaderActivator loader;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    //Fetching Custom Debugger
    Debugger debug;

    //declaring references
    MaterialToolbar topAppBar;
    SearchBar topSearchBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //Recycler view
    RecyclerView restaurantsView, cuisinesView, searchResultContainer;

    //Shimmer Effect
    ShimmerFrameLayout cuisineShimmer, restaurantShimmer;


    //Search Mechanism
    SearchView searchView;
    ViewSwitcher searchViewManager;
    ListView searchViewOptions;
    TextView searchNotification;


    //Declaring listeners
    //---when clicked on any item of the drawer navigation
    NavigationView.OnNavigationItemSelectedListener drawerItemClickListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            navigationView.setCheckedItem(item);
            drawerLayout.closeDrawer(GravityCompat.START);

            switch(item.getItemId())
            {
                case R.id.header_profile:
                    debug.show("Click on Profile item");
                    break;
                case R.id.header_notifications:
                    debug.show("Clicked on notifications item");
                    break;
                case R.id.header_history:
                    debug.show("Clicked on history item");
                    break;
                case R.id.header_settings:
                    debug.show("Clicked on settings item");
                    break;
                case R.id.header_customer_support:
                    debug.show("Clicked on customer support item");
                    Crisp.configure(getApplicationContext(), getString(R.string.crispKey));
                    Crisp.resetChatSession(getApplicationContext());
                    Crisp.setUserEmail(auth.getCurrentUser().getEmail());
                    Intent chatActivity = new Intent(App_Dashboard.this, ChatActivity.class);
                    startActivity(chatActivity);
                    break;
                case R.id.header_terms_and_conditions:
                    debug.show("Clicked on terms and conditions item");
                    break;
            }


            return true;
        }
    };

    //watch when user typing text query
    TextWatcher searchQueryWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            populateSearchResults(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void popuplateRestaurantsContainer()
    {
        //storing all restaurants data in the ArrayList
        ArrayList<Restaurants> all_restaurants = new ArrayList<>();

        //fetching data from FireStore of All Restaurants
        firestore.collection("restaurants")
                .orderBy("restaurantRatings", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for ( DocumentSnapshot row : list )
                        {
                            all_restaurants.add(new Restaurants(row.getString("restaurantName"), row.getString("restaurantImage"), row.get("restaurantFoodCategories"), row.getGeoPoint("restaurantLocation"), row.getDouble("restaurantRatings"), row.getDouble("restaurantTotalRatings")));
                            System.out.println(row.get("restaurantFoodCategories"));
                        }

                        DashboardRestaurantsAdapter adapter = new DashboardRestaurantsAdapter(
                                getApplicationContext(),
                                all_restaurants,
                                R.layout.dashboard_restaurants_layout,
                                new DashboardRestaurantsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Restaurants restaurant) {
                                        debug.show(restaurant.restaurantName);
                                        redirectToRestaurantProductsActivity(restaurant.restaurantName);
                                    }
                                });
                        restaurantsView.setAdapter(adapter);
                        loader.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        debug.show("Failed to load restaurants Data");
                    }
                });

    }
    public void popuplateCuisinesContainer()
    {
        //storing all restaurants data in the ArrayList
        ArrayList<Cuisines> all_cuisines = new ArrayList<>();

        //fetching data from FireStore of All Restaurants
        firestore.collection("cuisines")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for ( DocumentSnapshot row : list )
                        {
                            all_cuisines.add(new Cuisines( row.getString("cuisineImage"), StringFormatter.capitalizeWord(row.getString("cuisineName"))));
                            System.out.println(row.getString("cuisineName"));
                        }

                        DashboardCuisinesAdapter adapter = new DashboardCuisinesAdapter(
                                getApplicationContext(),
                                all_cuisines,
                                new DashboardCuisinesAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Cuisines cuisine) {
                                        topSearchBar.performClick();
                                        searchView.getEditText().setText(cuisine.cuisineName);
                                        redirectFromCuisineName(cuisine.cuisineName.toLowerCase());
                                    }
                                });
                        cuisinesView.setAdapter(adapter);
                        loader.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        debug.show("Failed to load cusines Data");
                    }
                });


    }

    public void populateSearchResults(String newQuery)
    {
        loader.show();
        ArrayList<String> all_cuisines = new ArrayList<>();
        searchViewManager.setDisplayedChild(0);

        firestore.collection("cuisines")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> all_rows = queryDocumentSnapshots.getDocuments();
                        ArrayAdapter<String> adapter;

                        Pattern pattern = Pattern.compile(".*"+newQuery+".*",Pattern.CASE_INSENSITIVE);


                        for(DocumentSnapshot row : all_rows)
                        {
                            String option = row.getString("cuisineName");
                            Matcher matcher = pattern.matcher(option);
                            if( option.matches("(?i).*("+newQuery+").*") )
                                all_cuisines.add(option);
                        }

                        System.out.println(all_cuisines);
                        if( all_cuisines.size() == 0 )
                        {
                            all_cuisines.add("No Items Found With \""+newQuery+"\"");
                        }



                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dashboard_search_options, all_cuisines);

                        searchViewOptions.setAdapter(adapter);
                        loader.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        searchViewOptions.setAdapter(null);
                        loader.hide();
                    }
                });

        searchViewOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView cuisineName = (TextView) view;
                String cuisine = cuisineName.getText().toString();
                redirectFromCuisineName(cuisine);
            }
        });


    }
    public void redirectFromCuisineName(String cuisine)
    {
        loader.show();

        //storing all restaurants data in the ArrayList
        ArrayList<Restaurants> all_restaurants = new ArrayList<>();

        //fetching data from FireStore of All Restaurants
        firestore.collection("restaurants")
                .whereArrayContains("restaurantFoodCategories", cuisine)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for ( DocumentSnapshot row : list )
                        {
                            all_restaurants.add(new Restaurants(row.getString("restaurantName"), row.getString("restaurantImage"), row.get("restaurantFoodCategories"), row.getGeoPoint("restaurantLocation"), row.getDouble("restaurantRatings"), row.getDouble("restaurantTotalRatings")));
                            System.out.println(row.get("restaurantFoodCategories"));
                        }

                        if( !all_restaurants.isEmpty() )
                            searchViewManager.setDisplayedChild(1);

                        DashboardRestaurantsAdapter adapter = new DashboardRestaurantsAdapter(getApplicationContext(), all_restaurants, R.layout.dashboard_searched_restaurants_layout,
                                new DashboardRestaurantsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Restaurants restaurant) {
                                        debug.show(restaurant.restaurantName);

                                        redirectToRestaurantProductsActivity(restaurant.restaurantName);
                                    }
                                });
                        searchResultContainer.setAdapter(adapter);
                        loader.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        debug.show("Failed to load restaurants Data");
                        loader.hide();
                    }
                });

    }

    public void redirectToRestaurantProductsActivity(String restaurantName)
    {
        debug.show(restaurantName);
        Intent goToRestaurant = new Intent(App_Dashboard.this, RestaurantProductsActivity.class);
        goToRestaurant.putExtra("restaurantName", restaurantName);
        startActivity(goToRestaurant);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( navigationView.getCheckedItem() != null )
            navigationView.getCheckedItem().setChecked(false);
    }

    //fetching user data
    @Override
    protected void onStart() {
        super.onStart();
        firestore.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userName = documentSnapshot.getString("userName");
                        userEmail = documentSnapshot.getString("userEmail");
                        userImagePath = documentSnapshot.getString("userPhoto");
                        //Setting User Info
                        TextView profileName = navigationView.getHeaderView(0).findViewById(R.id.header_profile_name);
                        profileName.setText(userName);

                        ImageView profilePhoto = navigationView.getHeaderView(0).findViewById(R.id.header_profile_image);


                        if( userImagePath != null )
                        {
                            Picasso.with(getApplicationContext())
                                    .load(Uri.parse(userImagePath))
                                    .into(profilePhoto);
                        }
                        else{

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.signout:
                auth.signOut();
                Intent goToAuth = new Intent(App_Dashboard.this, Welcome_Page.class);
                goToAuth.putExtra("intention", "Sign In");
                startActivity(goToAuth);
                finish();
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //pre run configs
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if( user == null ){
            Intent goToAuth = new Intent(App_Dashboard.this, Authentication_Page.class);
            startActivity(goToAuth);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main_activity);

        //post run configs
        //fetching or declaring references
        firestore = FirebaseFirestore.getInstance();
        debug = new Debugger(this, Toast.LENGTH_SHORT);
        loader = new LoaderActivator(this);
        topAppBar = findViewById(R.id.topAppBar_Dashboard);
        drawerLayout = findViewById(R.id.drawerLayout_Dashboard);
        navigationView = findViewById(R.id.navigationView_Dashboard);
        restaurantsView = findViewById(R.id.dashboardRestaurantsContainer);
        cuisinesView = findViewById(R.id.dashboardCuisinesContainer);
        topSearchBar = findViewById(R.id.topSearchBar_Dashboard);
        searchView = findViewById(R.id.dashboardSearchView);
        searchViewManager = findViewById(R.id.searchViewManager);
        searchViewOptions = findViewById(R.id.searchOptionsList);
        searchResultContainer = findViewById(R.id.searchResultContainer);

        //setting result mechanism elements configs
        searchViewManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        searchViewManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
        searchViewManager.setDisplayedChild(0);

        LinearLayoutManager searchResultsLayout = new LinearLayoutManager(this);
        searchResultContainer.setLayoutManager(searchResultsLayout);

        //setting custom toolbar
        setSupportActionBar(topAppBar);
        loader.show();

        //setting recycler configs
        LinearLayoutManager restaurantsRowLayout = new LinearLayoutManager(this);
        restaurantsRowLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        restaurantsView.setLayoutManager(restaurantsRowLayout);
        popuplateRestaurantsContainer();

        StaggeredGridLayoutManager cuisinesRowsLayout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        cuisinesView.setLayoutManager(cuisinesRowsLayout);
        popuplateCuisinesContainer();

        //setting up navigation drawer with configs
        ActionBarDrawerToggle toggleOptionsHandler = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggleOptionsHandler);
        toggleOptionsHandler.syncState(); //to sync with state opened drawer/ closed drawer
        navigationView.bringToFront();


        //setting listeners
        navigationView.setNavigationItemSelectedListener(drawerItemClickListener);
        searchView.getEditText().addTextChangedListener(searchQueryWatcher);


        //---when clicked on any item of the drawer navigation
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        topAppBar.setNavigationIcon(R.drawable.navigation_drawer);


    }

    @Override
    public void onBackPressed() {
        if( drawerLayout.isDrawerOpen(GravityCompat.START) )
        {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if( searchView.isShowing() )
        {

            return;
        }

        super.onBackPressed();
    }

}