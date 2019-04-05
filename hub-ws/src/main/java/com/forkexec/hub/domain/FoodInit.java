package com.forkexec.hub.domain;


public class FoodInit {
    private Food _food;
    private int _foodQuantity;

    public FoodInit(Food food, int foodQuantity){
        _food = food;
        _foodQuantity = foodQuantity;
    }

    public Food getFood() {return _food;}
    public int getFoodQuantity() {return _foodQuantity;}

    public void setFoodQuantity(int foodQuantity) {_foodQuantity = foodQuantity;}
}