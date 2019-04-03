package com.forkexec.hub.domain;


import com.forkexec.hub.domain.exceptions.MaximumCartQuantityException;


public class User {
    private String _userId;
    private Cart _cart = new Cart();

    public User(String userId){
        _userId = userId;
    }

    public String getUserId() {return _userId;}
    public Cart getCart() {return _cart;}

    public void addFood(FoodId foodId, int quantity) throws MaximumCartQuantityException {
        _cart.addFood(foodId, quantity);
    }

    public void clearCart() {
        _cart = new Cart();
    }
}