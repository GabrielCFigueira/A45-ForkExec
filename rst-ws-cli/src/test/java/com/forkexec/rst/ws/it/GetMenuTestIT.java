package com.forkexec.rst.ws.it;


import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.BadInitFault_Exception;

import java.util.List;
import java.util.ArrayList;


import static org.junit.Assert.assertEquals;

public class GetMenuTestIT extends BaseIT {

    @Before
    public void setup() throws BadInitFault_Exception {
        List<MenuInit> initialMenus = new ArrayList<MenuInit>();
        initialMenus.add(createMenuInit(createMenu(createMenuId("1"), "pão", "francesinha", "mousse", 100, 2), 1));
        initialMenus.add(createMenuInit(createMenu(createMenuId("2"), "pão de alho", "cozido à portuguesa", "fruta", 150, 4), 2));
        initialMenus.add(createMenuInit(createMenu(createMenuId("3"), "azeitonas", "feijoada", "baba de camelo", 130, 5), 3));
        initialMenus.add(createMenuInit(createMenu(createMenuId("4"), "tremoço", "cachupa", "tarte de maçã", 105, 4), 1));
        initialMenus.add(createMenuInit(createMenu(createMenuId("5"), "caracóis", "francesinha", "tarte de alfarroba", 200, 2), 1));
        initialMenus.add(createMenuInit(createMenu(createMenuId("6"), "pão", "polvo", "gelado de chocolate", 150, 3), 4));
        initialMenus.add(createMenuInit(createMenu(createMenuId("7"), "azeitonas", "pizza", "gelado de manga", 110, 1), 2));
        initialMenus.add(createMenuInit(createMenu(createMenuId("8"), "milho", "frango assado", "sumo de laranja", 80, 2), 3));
        initialMenus.add(createMenuInit(createMenu(createMenuId("9"), "pizza", "peru estufado", "vinho", 120, 3), 1));
        initialMenus.add(createMenuInit(createMenu(createMenuId("10"), "cerveja", "picanha", "mousse", 250, 3), 2));
        System.err.println(initialMenus.size());
        client.ctrlInit(initialMenus);
    }


    @Test
    public void success() throws BadMenuIdFault_Exception {
        Menu menu = client.getMenu(createMenuId("1"));
        assertEquals(menu.getEntree(), "pão");

        menu = client.getMenu(createMenuId("2"));
        assertEquals(menu.getDessert(), "fruta");

        menu = client.getMenu(createMenuId("3"));
        assertEquals(menu.getPlate(), "feijoada");

        menu = client.getMenu(createMenuId("4"));
        assertEquals(menu.getPrice(), 105);

        menu = client.getMenu(createMenuId("5"));
        assertEquals(menu.getPreparationTime(), 2);

        menu = client.getMenu(createMenuId("6"));
        assertEquals(menu.getDessert(), "gelado de chocolate");

        menu = client.getMenu(createMenuId("7"));
        assertEquals(menu.getEntree(), "azeitonas");

        menu = client.getMenu(createMenuId("8"));
        assertEquals(menu.getPrice(), 80);

        menu = client.getMenu(createMenuId("9"));
        assertEquals(menu.getPlate(), "peru estufado");

        menu = client.getMenu(createMenuId("10"));
        assertEquals(menu.getPreparationTime(), 3);
    }

    @Test (expected = BadMenuIdFault_Exception.class)
    public void nonExistantMenu() throws BadMenuIdFault_Exception {
        client.getMenu(createMenuId("11"));
    }


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

    @After
    public void clear() {
        client.ctrlClear();
    }
}