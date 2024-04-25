package com.example.fooddeliveryapp.Adapters;

import android.content.Context;
import android.icu.number.NumberFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.ApplicationUtilities.FirebaseImageStorage;
import com.example.fooddeliveryapp.ApplicationUtilities.StringFormatter;
import com.example.fooddeliveryapp.Models.Products;
import com.example.fooddeliveryapp.Models.Restaurants;
import com.example.fooddeliveryapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.NumberFormat;
import java.util.ArrayList;

public class RestaurantProductsAdapter extends RecyclerView.Adapter<RestaurantProductsAdapter.ViewHolder>
{
    public interface OnItemClickListener{
        void onItemClick(Products product);
    }


    Context context;
    ArrayList<Products> products;
    int layoutResource = 0;

    OnItemClickListener listener;

    public RestaurantProductsAdapter(Context context, ArrayList<Products> products, int layoutResource, OnItemClickListener listener) {
        this.context = context;
        this.products = products;
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
        FirebaseImageStorage.putImageOnImageView( products.get(position).productImage ,holder.productImage, holder.shimmerProduct);

        holder.productName.setText(products.get(position).productName);
        holder.productDescription.setText(products.get(position).productDescription);
        holder.productPrice.setText("Rs. "+ NumberFormat.getNumberInstance().format(products.get(position).productPrice));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(products.get(position));
            }
        });
    }

    //it will tell when to stop adding data in viewHolder viewItems
    //how much items are there
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView productImage;
        TextView productName, productDescription, productPrice;

        ShimmerFrameLayout shimmerProduct;
        public ViewHolder(View itemView)
        {
            super(itemView);
            productImage = itemView.findViewById(R.id.restaurantsProductImage);
            productName = itemView.findViewById(R.id.restaurantsProductName);
            productDescription = itemView.findViewById(R.id.restaurantsProductDescription);
            productPrice = itemView.findViewById(R.id.restaurantsProductPrice);
            shimmerProduct = itemView.findViewById(R.id.shimmerRestaurantProduct);
        }

    }
}
