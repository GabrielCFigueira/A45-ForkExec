package com.forkexec.hub.domain;


public class FoodId {
    private String _restaurantId;
    private String _menuId;

    public FoodId(String restaurantId, String menuId) {
        _restaurantId = restaurantId;
        _menuId = menuId;
    }

    public String getRestaurantId() {return _restaurantId;}
    public String getMenuId() {return _menuId;}
}
