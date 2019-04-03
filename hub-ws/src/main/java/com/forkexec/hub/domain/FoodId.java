package com.forkexec.hub.domain;


import java.lang.Comparable;

public class FoodId implements Comparable<FoodId> {
    private String _restaurantId;
    private String _menuId;

    public FoodId(String restaurantId, String menuId) {
        _restaurantId = restaurantId;
        _menuId = menuId;
    }

    public String getRestaurantId() {return _restaurantId;}
    public String getMenuId() {return _menuId;}

    @Override
    public int compareTo(FoodId foodId) {
        int res = _restaurantId.compareTo(foodId.getRestaurantId());
        if (res == 0)
            res = _menuId.compareTo(foodId.getMenuId());
        return res;
    }
}
