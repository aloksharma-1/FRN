package com.alokdev21.foodapk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class Cart_Fragment extends Fragment {


    private RecyclerView recyclerViewCart;
    private DishAdapter cartAdapter;
    private List<Dish> cartItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_, container, false);

        recyclerViewCart = view.findViewById(R.id.recyclerView_Cart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItems = new ArrayList<>();
        cartAdapter = new DishAdapter(getContext(), cartItems, null); // No need to pass ViewModel here
        recyclerViewCart.setAdapter(cartAdapter);

        // Observe the shared ViewModel
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), new Observer<List<Dish>>() {
            @Override
            public void onChanged(List<Dish> dishes) {
                cartItems.clear();
                cartItems.addAll(dishes);
                cartAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}

