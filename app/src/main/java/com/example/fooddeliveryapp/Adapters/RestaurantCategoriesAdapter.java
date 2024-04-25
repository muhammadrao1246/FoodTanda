package com.example.fooddeliveryapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Products;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.example.fooddeliveryapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantCategoriesAdapter extends RecyclerView.Adapter<RestaurantCategoriesAdapter.ViewHolder>
{
    Context context;
    HashMap<String, ArrayList<Products>> products;
    int layoutResource = 0;
    ArrayList<String> cuisineNames;

    RestaurantProductsAdapter.OnItemClickListener listener;


    public RestaurantCategoriesAdapter(Context context, HashMap<String, ArrayList<Products>> products, int layoutResource, RestaurantProductsAdapter.OnItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.layoutResource = layoutResource;
        this.listener = listener;

        cuisineNames = new ArrayList<>();
        for( String cuisineName : products.keySet() )
        {
            cuisineNames.add(cuisineName);
        }
    }

    //Creating ViewHolder which will hold all the view displayed on screen
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //layout inflated inside the parent RecyclerView group
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);

        //viewHolder to be prepared and returned then
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //Add Data To Each ViewHolder's ViewItem
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        holder.cuisineName.setText(StringFormatter.capitalizeWord(cuisineNames.get(position)));

        RestaurantProductsAdapter adapter = new RestaurantProductsAdapter(context, products.get(cuisineNames.get(position)), R.layout.restaurant_products_layout, listener);


        holder.restaurantProductsContainer.setAdapter(adapter);
    }

    //it will tell when to stop adding data in viewHolder viewItems
    //how much items are there
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView cuisineName;
        RecyclerView restaurantProductsContainer;

        public ViewHolder(View itemView)
        {
            super(itemView);
            cuisineName = itemView.findViewById(R.id.restaurantCategoryName);
            restaurantProductsContainer = itemView.findViewById(R.id.restaurantProductsContainer);
            restaurantProductsContainer.setLayoutManager(new LinearLayoutManager(context));
        }

    }
}
