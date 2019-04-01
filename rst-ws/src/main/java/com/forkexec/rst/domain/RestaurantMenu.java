package com.forkexec.rst.domain;

public class RestaurantMenu{
	
	private String menuId;
	private String entree;
	private String plate;
	private String dessert;
	private int price;
	private int preparationTime;
	private int quantity;
	
	public RestaurantMenu(String id, String entree, String plate, String dessert, int price, int preparationTime, int quantity) {
		this.menuId=id;
		this.entree=entree;
		this.plate=plate;
		this.dessert=dessert;
		this.price=price;
		this.preparationTime=preparationTime;
		this.quantity=quantity;
	}
	
	public String getId() {
		return this.menuId;
	}
	
	public String getEntree() {
		return this.entree;
	}
	
	public String getPlate() {
		return this.plate;
	}
	
	public String getDessert() {
		return this.dessert;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public int getPreparationTime() {
		return this.preparationTime;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}