package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepos = mock(UserRepository.class);
    private OrderRepository orderRepos = mock(OrderRepository.class);

    @Before
    public void SetUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepos);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepos);
    }

    //////////////// Test 1 : Check that order is submitted successfully /////////////////
    @Test
    public void checkSubmitOrderSuccess() {

        Item item1 = makeItem();
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);

        User user = makeUser("submitSuccess", "submitPass");

        Cart cart1 = makeCart(user, itemList);
        cart1.setUser(user);
        cart1.setItems(itemList);
        user.setCart(cart1);

        when(userRepos.findByUsername("submitSuccess")).thenReturn(user);
        ResponseEntity<UserOrder> submitOrder = orderController.submit("submitSuccess");
        assertNotNull(submitOrder);
        assertEquals(200, submitOrder.getStatusCodeValue());

        UserOrder order = submitOrder.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    ////////////////////// Test 2 : Order History for user is successful /////////////////////
    @Test
    public void checkOrderHistoryForUser() {

        Item item1 = makeItem();
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);

        User user = makeUser("orderHistSuccess", "OrderHistPass");

        Cart cart1 = makeCart(user, itemList);
        cart1.setUser(user);
        cart1.setItems(itemList);
        user.setCart(cart1);

        when(userRepos.findByUsername("orderHistSuccess")).thenReturn(user);

        final ResponseEntity<List<UserOrder>> userOrderHistory =
                orderController.getOrdersForUser("orderHistSuccess");

        assertNotNull(userOrderHistory);
        assertEquals(200, userOrderHistory.getStatusCodeValue());

        List<UserOrder> orderList = userOrderHistory.getBody();
        assertNotNull(orderList);
    }

    ///////////////////////////// Test 3 : Order cannot be submitted when username does not exist ///////////////////
    @Test
    public void checkSubmitOrderFailed() {

        when(userRepos.findByUsername("submitUserFail")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("submitUserFail");
        assertEquals(404, response.getStatusCodeValue());
    }

    ////////////////////////////// Test 4 : Order history fails when user does not have an order ///////////////
    @Test
    public void checkOrderHistoryFailed() {

        when(userRepos.findByUsername("OrderHistFail")).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("OrderHistFail");
        assertNotEquals(200, response.getStatusCodeValue());
    }

    private Cart makeCart(User user, List<Item> itemList) {
        Cart cart1 = new Cart();
        cart1.setId(2L);
        cart1.setUser(user);
        cart1.setItems(itemList);
        cart1.setTotal(BigDecimal.valueOf(5.00));

        return cart1;
    }

    private Item makeItem() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Big Widget");
        item1.setPrice(BigDecimal.valueOf(5.00));
        item1.setDescription("A big widget");
        return item1;
    }

    private User makeUser(String username, String password) {
        User user = new User();
        user.setId(2);
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }
}
