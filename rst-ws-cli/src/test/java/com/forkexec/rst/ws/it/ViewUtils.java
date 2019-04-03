package com.forkexec.rst.ws.it;


import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;

public class ViewUtils {
    
    public static Menu createMenu(MenuId menuId, String entree, String plate, 
                        String dessert, int price, int preparationTime) {
        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setEntree(entree);
        menu.setPlate(plate);
        menu.setDessert(dessert);
        menu.setPrice(price);
        menu.setPreparationTime(preparationTime);

        return menu;
    }

    public static MenuInit createMenuInit(Menu menu, int quantity) {
        MenuInit menuInit = new MenuInit();
        menuInit.setMenu(menu);
        menuInit.setQuantity(quantity);
        return menuInit;
    }

    public static MenuId createMenuId(String id) {
        MenuId menuId = new MenuId();
        menuId.setId(id);
        return menuId;
    }
    
}