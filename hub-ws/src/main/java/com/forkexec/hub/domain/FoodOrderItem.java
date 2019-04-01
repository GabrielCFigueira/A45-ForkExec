package com.forkexec.hub.domain;


public class FoodOrderItem {
    private FoodId _foodId;
    private int _foodQuantity;

    public FoodOrderItem(FoodId foodId, int foodQuantity){
        _foodId = foodId;
        _foodQuantity = foodQuantity;
    }

    public FoodId getFoodId() {return _foodId;}
    public int getFoodQuantity() {return _foodQuantity;}
}
