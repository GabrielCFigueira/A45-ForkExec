package com.forkexec.hub.domain;


import java.util.Map;
import java.util.TreeMap;

import com.forkexec.hub.domain.exceptions.MaximumCartQuantityException;

public class Cart {
    private Map<FoodId, FoodOrderItem> _food = new TreeMap<FoodId, FoodOrderItem>();
    
    public final static int maximumQuantity = 100;

    public void addFood(FoodId foodId, int quantity) throws MaximumCartQuantityException {
        if (getQuantity() + quantity > maximumQuantity)
            throw new MaximumCartQuantityException("The new order exceeds the allowed cart capacity: " + maximumQuantity);
        if (_food.containsKey(foodId))
            _food.get(foodId).setFoodQuantity(_food.get(foodId).getFoodQuantity() + quantity);
        _food.put(foodId, new FoodOrderItem(foodId, quantity));
    }

    public int getQuantity() {
        int res = 0;
        for (FoodOrderItem foodOrderItem : _food.values())
            res += foodOrderItem.getFoodQuantity();
        return res;
    }

    public Map<FoodId, FoodOrderItem> getFood() {return _food;}

    public boolean isEmpty() {return _food.isEmpty();}
}