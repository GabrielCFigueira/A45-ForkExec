package com.forkexec.hub.domain;



public class User {
    private String _userId;
    private Cart _cart = new Cart();

    public User(String userId){
        _userId = userId;
    }

    public String getUserId() {return _userId;}
    public Cart getCart() {return _cart;}

    public void addFood(FoodId foodId, int quantity) {
        _cart.addFood(foodId, quantity);
    }

    public void clearCart() {
        _cart = new Cart();
    }
}