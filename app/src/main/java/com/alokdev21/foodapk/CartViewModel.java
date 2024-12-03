package com.alokdev21.foodapk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<Dish>> cartItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Dish>> getCartItems() {
        return cartItems;
}

    public void addToCart(Dish dish) {
        List<Dish> currentCart = cartItems.getValue();
        if (currentCart != null) {
            currentCart.add(dish);
            cartItems.setValue(currentCart); // Notify observers
        }
    }
}
