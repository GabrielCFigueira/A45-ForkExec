package com.forkexec.rst.domain;

public class RestaurantMenuOrder{
	
	protected String orderId;
    protected String menuId;
    protected int menuQuantity;
	
	public RestaurantMenuOrder(String orderId, String menuId, int menuQuantity) {
		this.orderId=orderId;
		this.menuId=menuId;
		this.menuQuantity=menuQuantity;
	}
	
	public String getId() {
		return this.orderId;
	}
	
	public String getMenuId() {
		return this.menuId;
	}
	
	public int getMenuQuantity() {
		return this.menuQuantity;
	}
}