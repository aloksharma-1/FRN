package com.alokdev21.foodapk;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private final Context context;
    private final List<Dish> dishList;
    private CartViewModel cartViewModel;

    public DishAdapter(Context context, List<Dish> dishList, CartViewModel cartViewModel) {
        this.context = context;
        this.dishList = dishList;
        this.cartViewModel = cartViewModel;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.textViewDishName.setText(""+ dish.getName());
        holder.textViewDishPrice.setText("₹" + dish.getPrice());

        Glide.with(context).load(""+dish.getImageUrl()).into(holder.imageViewDish);



        // Handle Add Icon click
        holder.AddButton.setOnClickListener(v -> {
            cartViewModel.addToCart(dish);
            Toast.makeText(context, dish.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewDish;
        TextView textViewDishName, textViewDishPrice;
        Button AddButton;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.imageViewRestaurant);
            textViewDishName = itemView.findViewById(R.id.textDishName);
            textViewDishPrice = itemView.findViewById(R.id.textViewPrice);
            AddButton= itemView.findViewById(R.id.AddButton);
        }
    }
}