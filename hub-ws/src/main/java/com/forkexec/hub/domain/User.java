package com.forkexec.hub.domain;


import com.forkexec.hub.domain.exceptions.MaximumCartQuantityException;
import com.forkexec.hub.domain.exceptions.InvalidFoodQuantityException;


public class User {
    private String _userId;
    private Cart _cart = new Cart();

    public User(String userId){
        _userId = userId;
    }

    public String getUserId() {return _userId;}
    public synchronized Cart getCart() {return _cart;}

    public synchronized void addFood(FoodId foodId, int quantity) throws MaximumCartQuantityException, InvalidFoodQuantityException {
        _cart.addFood(foodId, quantity);
    }

    public synchronized void clearCart() {
        _cart = new Cart();
    }
}