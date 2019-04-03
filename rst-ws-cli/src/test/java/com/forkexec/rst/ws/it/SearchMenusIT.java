package com.forkexec.rst.ws.it;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.BadInitFault_Exception;
import com.forkexec.rst.ws.BadTextFault_Exception;

import java.util.List;
import java.util.ArrayList;

import static com.forkexec.rst.ws.it.ViewUtils.*;
import static org.junit.Assert.assertEquals;


public class SearchMenusIT extends BaseIT {
    
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
        client.ctrlInit(initialMenus);
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionEmptyString() throws BadTextFault_Exception {
        client.searchMenus("");
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionNull() throws BadTextFault_Exception {
        client.searchMenus(null);
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionWithSpaces1() throws BadTextFault_Exception {
        client.searchMenus("peru estufado");
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionWithSpaces2() throws BadTextFault_Exception {
        client.searchMenus("frango ");
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionWithSpaces3() throws BadTextFault_Exception {
        client.searchMenus(" pizza");
    }

    @Test (expected = BadTextFault_Exception.class)
    public void descriptionWithSpaces4() throws BadTextFault_Exception {
        client.searchMenus("gelado de chocolate");
    }

    @Test
    public void descriptionNonExistant() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("bomdia");
        assertEquals(menus.size(), 0);
    }

    @Test 
    public void success1() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("picanha");
        assertEquals(menus.size(), 1);
        assertEquals(menus.get(0).getId().getId(), "10");
        assertEquals(menus.get(0).getPrice(), 250);
    }

    @Test 
    public void success2() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("tremoço");
        assertEquals(menus.size(), 1);
        assertEquals(menus.get(0).getId().getId(), "4");
        assertEquals(menus.get(0).getPlate(), "cachupa");
    }

    @Test 
    public void success3() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("gelado");
        assertEquals(menus.size(), 2);
    }

    @Test 
    public void success4() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("azeitonas");
        assertEquals(menus.size(), 2);
    }

    @Test 
    public void success5() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("pizza");
        assertEquals(menus.size(), 2);
    }

    @Test 
    public void success6() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("tarte");
        assertEquals(menus.size(), 2);
    }

    @Test 
    public void success7() throws BadTextFault_Exception {
        List<Menu> menus = client.searchMenus("pão");
        assertEquals(menus.size(), 3);
    }

    @After
    public void tearDown() {
        client.ctrlClear();
    }


}