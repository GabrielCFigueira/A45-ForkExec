package com.forkexec.hub.domain;


import java.util.List;
import java.util.ArrayList;

public class Cart {
    private List<FoodOrderItem> _food = new ArrayList<FoodOrderItem>();

    public void addFood(FoodId foodId, int quantity) {
        _food.add(new FoodOrderItem(foodId, quantity));
    }

    public List<FoodOrderItem> getFood() {return _food;}

    public boolean isEmpty() {return _food.isEmpty();}
}