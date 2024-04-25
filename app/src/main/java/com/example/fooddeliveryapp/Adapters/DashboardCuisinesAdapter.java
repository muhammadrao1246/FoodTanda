package com.example.fooddeliveryapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Cuisines;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.example.fooddeliveryapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class DashboardCuisinesAdapter extends RecyclerView.Adapter<DashboardCuisinesAdapter.ViewHolder>
{
    public interface OnItemClickListener{
        public void onItemClick(Cuisines cuisine);
    }

    Context context;
    ArrayList<Cuisines> cuisines;

    OnItemClickListener listener;

    public DashboardCuisinesAdapter(Context context, ArrayList<Cuisines> cuisines, OnItemClickListener listener)
    {
        this.context = context;
        this.cuisines = cuisines;
        this.listener = listener;
    }

    //Creating ViewHolder which will hold all the view displayed on screen
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //layout inflated inside the parent RecyclerView group
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_cuisines_layout, parent, false);

        //viewHolder to be prepared and returned then
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //Add Data To Each ViewHolder's ViewItem
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        FirebaseImageStorage.putImageOnImageView( cuisines.get(position).cuisineImage ,holder.cuisineImage, holder.cuisineShimmer);
        holder.cuisineName.setText(cuisines.get(position).cuisineName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(cuisines.get(position));
            }
        });
    }

    //it will tell when to stop adding data in viewHolder viewItems
    //how much items are there
    @Override
    public int getItemCount() {
        return cuisines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView cuisineImage;
        TextView cuisineName;

        //Shimmer Effect
        ShimmerFrameLayout cuisineShimmer;
        public ViewHolder(View itemView)
        {
            super(itemView);
            cuisineName = itemView.findViewById(R.id.cuisineNameText);
            cuisineImage = itemView.findViewById(R.id.cuisineHeadImage);
            cuisineShimmer = itemView.findViewById(R.id.shimmerDashboardCuisines);
        }
    }
}
