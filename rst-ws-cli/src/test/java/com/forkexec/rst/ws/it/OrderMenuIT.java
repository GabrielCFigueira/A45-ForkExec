package com.forkexec.rst.ws.it;


import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.util.List;
import java.util.ArrayList;


import com.forkexec.rst.ws.MenuOrder;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.BadInitFault_Exception;
import com.forkexec.rst.ws.InsufficientQuantityFault_Exception;
import com.forkexec.rst.ws.BadQuantityFault_Exception;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;

import static org.junit.Assert.assertEquals;


public class OrderMenuIT extends BaseIT {

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

    @Test
    public void success1() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("2"), 2);
        assertEquals(menuOrder.getMenuId().getId(), "2");
        assertEquals(menuOrder.getMenuQuantity(), 2);
        assertEquals(client.getMenu(menuOrder.getMenuId()).getPlate(), "cozido à portuguesa");
        assertEquals(client.getMenu(menuOrder.getMenuId()).getDessert(), "fruta");
    }

    @Test
    public void success2() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("5"), 1);
        assertEquals(menuOrder.getMenuId().getId(), "5");
        assertEquals(menuOrder.getMenuQuantity(), 1);
        assertEquals(client.getMenu(menuOrder.getMenuId()).getEntree(), "caracóis");
        assertEquals(client.getMenu(menuOrder.getMenuId()).getPrice(), 200);
    }

    @Test (expected=InsufficientQuantityFault_Exception.class)
    public void updateMenuQuantity() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("8"), 3);
        assertEquals(menuOrder.getMenuId().getId(), "8");
        assertEquals(menuOrder.getMenuQuantity(), 1);
        client.orderMenu(createMenuId("8"), 1));
    }

    @Test (expected=BadMenuIdFault_Exception.class)
    public void inexistantMenu1() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("bomdia"), 1);
    }

    @Test (expected=BadMenuIdFault_Exception.class)
    public void inexistantMenu2() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("0"), 1);
    }

    @Test (expected=BadMenuIdFault_Exception.class)
    public void nullMenu() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(null, 1);
    }

    @Test (expected=BadMenuIdFault_Exception.class)
    public void nullMenuId() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId(null), 1);
    }

    @Test (expected=BadQuantityFault_Exception.class)
    public void invalidQuantity1() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("1"), -1);
    }

    @Test (expected=BadQuantityFault_Exception.class)
    public void invalidQuantity2() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("5"), 0);
    }

    @Test (expected=InsufficientQuantityFault_Exception.class)
    public void insufficientQuantity1() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("3"), 4);
    }

    @Test (expected=InsufficientQuantityFault_Exception.class)
    public void insufficientQuantity2() throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
        MenuOrder menuOrder = client.orderMenu(createMenuId("7"), 300000000);
    }

    @After
    public void tearDown() {
        client.ctrlClear();
    }


}