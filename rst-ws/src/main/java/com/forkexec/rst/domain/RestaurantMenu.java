package com.forkexec.rst.domain;

public class RestaurantMenu{
	
	private String menuId;
	private String entree;
	private String plate;
	private String dessert;
	private int price;
	private int preparationTime;
	
	public RestaurantMenu(String id, String entree, String plate, String dessert, int price, int preparationTime) {
		this.menuId=id;
		this.entree=entree;
		this.plate=plate;
		this.dessert=dessert;
		this.price=price;
		this.preparationTime=preparationTime;
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
}