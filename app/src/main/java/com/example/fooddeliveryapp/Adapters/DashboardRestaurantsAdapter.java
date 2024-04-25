package com.example.fooddeliveryapp.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.TextUtilsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.example.fooddeliveryapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.w3c.dom.Text;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import io.grpc.okhttp.internal.Util;

public class DashboardRestaurantsAdapter extends RecyclerView.Adapter<DashboardRestaurantsAdapter.ViewHolder>
{
    public interface OnItemClickListener{
        void onItemClick(Restaurants restaurant);
    }


    Context context;
    ArrayList<Restaurants> restaurants;
    int layoutResource = 0;

    OnItemClickListener listener;

    public DashboardRestaurantsAdapter(Context context, ArrayList<Restaurants> restaurants, int layoutResource, OnItemClickListener listener)
    {
        this.context = context;
        this.restaurants = restaurants;
        this.layoutResource = layoutResource;
        this.listener = listener;
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
        FirebaseImageStorage.putImageOnImageView( restaurants.get(position).restaurantImage ,holder.restaurantImage, holder.shimmerRestaurant);

        holder.restaurantName.setText(restaurants.get(position).restaurantName);
        String foodCategories = "$$$ ·";
        for( String cuisine : restaurants.get(position).restaurantFoodCategories )
        {
            cuisine = StringFormatter.capitalizeWord(cuisine);
            if( restaurants.get(position).restaurantFoodCategories.get(0).equalsIgnoreCase(cuisine) )
            {
                foodCategories += " " + cuisine;
            }
            else{
                foodCategories += ", " + cuisine;
            }
        }
        holder.restaurantFoodCategories.setText(foodCategories);
        holder.restaurantTotalDeliveryTime.setText("10 min");
        holder.restaurantEstimatedDeliveryPrice.setText("PKR 10 delivery fee");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(restaurants.get(position));
            }
        });
    }

    //it will tell when to stop adding data in viewHolder viewItems
    //how much items are there
    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView restaurantImage;
        TextView restaurantName, restaurantFoodCategories, restaurantTotalDeliveryTime, restaurantEstimatedDeliveryPrice;

        ShimmerFrameLayout shimmerRestaurant;
        public ViewHolder(View itemView)
        {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurantHeadImage);
            restaurantName = itemView.findViewById(R.id.restaurantNameText);
            restaurantFoodCategories = itemView.findViewById(R.id.restaurantFoodCategories);
            restaurantEstimatedDeliveryPrice = itemView.findViewById(R.id.restaurantEstimatedDeliveryPrice);
            restaurantTotalDeliveryTime = itemView.findViewById(R.id.restaurantTotalDeliveryTime);
            shimmerRestaurant = itemView.findViewById(R.id.shimmerDashboardRestaurant);
        }

    }
}
