package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepos = mock(UserRepository.class);
    private CartRepository cartRepos = mock(CartRepository.class);
    private ItemRepository itemRepos = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepos);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepos);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepos);
    }

    @Test
    public void checkAddToCartNoUser() {
        long itemId = 2L;
        String username = "Stephen";
        int quantity = 4;

        ModifyCartRequest cartRequest = makeCartRequest(itemId, quantity, username);
        ArrayList<Item> itemList = new ArrayList<Item>();

        when(userRepos.findByUsername(username)).thenReturn(null);
        when(itemRepos.findById(itemId)).thenReturn(null);

        ResponseEntity<Cart> cartResponse = cartController.addToCart(cartRequest);
        assertNotNull(cartResponse);
        assertEquals(HttpStatus.NOT_FOUND, cartResponse.getStatusCode());
    }

    @Test
    public void checkAddToCartNoItem() {
        Cart cart1 = new Cart();

        Long itemId = 2L;
        String itemName = "Revlon Lipstick";
        String itemDescription = "Luminous Red";
        BigDecimal itemPrice = new BigDecimal("7.50");
        Item item1 = makeItem(itemId, itemName, itemPrice, itemDescription);

        long userId = 2L;
        String username = "Angela";
        String password = "testPass";

        User user1 = makeUser(userId, username, password);
        long cartId = 2L;
        int quantity = 5;

        ModifyCartRequest cartRequest = makeCartRequest(itemId, quantity, username);
        cart1 = makeCart(cartId, null, user1);
        user1.setCart(cart1);
        cart1.setUser(user1);

        when(userRepos.findByUsername(username)).thenReturn(user1);
        when(itemRepos.findById(2L)).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Cart> cartResponse = cartController.addToCart(cartRequest);
        assertNotNull(cartResponse);
        assertEquals(HttpStatus.NOT_FOUND, cartResponse.getStatusCode());

    }

    @Test
    public void checkAddToCart() {
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setItemId(2L);
        cartReq.setUsername("testuser1");
        cartReq.setQuantity(4);

        User user = new User();
        user.setUsername("testuser1");

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setCart(cart);

        Item item1 = new Item();
        item1.setId(3L);
        item1.setName("shampoo");
        item1.setPrice(BigDecimal.valueOf(4.50));

        when (userRepos.findByUsername(cartReq.getUsername())).thenReturn(user);
        when(itemRepos.findById(cartReq.getItemId())).thenReturn(Optional.of(item1));

        final ResponseEntity<Cart> cartRespEntity = cartController.addToCart(cartReq);
        assertNotNull(cartRespEntity);
        assertEquals(200, cartRespEntity.getStatusCodeValue());

        Cart cart1 = cartRespEntity.getBody();

        assertNotNull(cart);
        assertEquals(4, cart.getItems().size());
    }

    @Test
    public void checkRemoveFromCart() {

        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setItemId(3L);
        cartReq.setUsername("testuser1");
        cartReq.setQuantity(2);

        User user = new User();
        user.setUsername("testuser1");

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setCart(cart);

        Item item1 = new Item();
        item1.setId(3L);
        item1.setName("shampoo");
        item1.setPrice(BigDecimal.valueOf(4.50));

        when (userRepos.findByUsername(cartReq.getUsername())).thenReturn(user);
        when(itemRepos.findById(cartReq.getItemId())).thenReturn(Optional.of(item1));

        ResponseEntity<Cart> cartRespEntity = cartController.addToCart(cartReq);
        assertNotNull(cartRespEntity);
        assertEquals(200, cartRespEntity.getStatusCodeValue());

        // decrease the item quantity
        //cartReq = new ModifyCartRequest();
        cartReq.setItemId(3L);
        cartReq.setQuantity(1);
        cartReq.setUsername("testuser1");
        cartRespEntity = cartController.removeFromCart(cartReq);

        when (userRepos.findByUsername(cartReq.getUsername())).thenReturn(user);
        when(itemRepos.findById(cartReq.getItemId())).thenReturn(Optional.of(item1));
        assertNotNull(cartRespEntity);
        //assertEquals(200, cartRespEntity.getStatusCodeValue());

        Cart cart1 = cartRespEntity.getBody();
        assertEquals(200, cartRespEntity.getStatusCodeValue());
        assertEquals(1, cart1.getItems().size());
    }

    ////////// Test Unsuccessful Removal from Cart -- User does not exist //////////
    @Test
    public void checkRemovedFromCartNoUser() {
        long itemId = 3L;
        int quantity = 5;
        String username = "Jane";

        ModifyCartRequest cartRequest = makeCartRequest(itemId, quantity, username);
        when(userRepos.findByUsername(username)).thenReturn(null);

        ResponseEntity<Cart> cartResponse = cartController.removeFromCart(cartRequest);

        assertNotNull(cartResponse);
        assertEquals(HttpStatus.NOT_FOUND, cartResponse.getStatusCode());
    }


    ////////// Test Unsuccessful Removal from Cart - Null Item ////////
    @Test
    public void checkRemovedFromCartNoItem() {
        Cart cart1 = new Cart();

        Long itemId = 2L;
        String itemName = "Revlon Lipstick";
        BigDecimal itemPrice = new BigDecimal("7.50");
        String itemDescription = "Luuminour Red";
        Item item1 = makeItem(2L, itemName, itemPrice, itemDescription);

        long userId = 2L;
        String username = "Linda";
        String password = "testpass";

        User user1 = makeUser(userId, username, password);
        user1.setCart(cart1);

        when (userRepos.findByUsername(username)).thenReturn(user1);
        when(itemRepos.findById(itemId)).thenReturn(Optional.ofNullable(null));
    }

    ///////////////////////  Helper methods ////////////////////////////////////////////////////////
    private Item makeItem (Long itemId, String itemName, BigDecimal itemPrice, String itemDescription) {
        Item myItem = new Item();
        myItem.setId(itemId);
        myItem.setName(itemName);
        myItem.setPrice(itemPrice);
        myItem.setDescription(itemDescription);
        return myItem;
    }

    private User makeUser(long userId, String username, String password) {
        User user1 = new User();
        user1.setId(userId);
        user1.setUsername(username);
        user1.setPassword(password);
        return user1;
    }

    private Cart makeCart (long cartId, ArrayList<Item> items, User user) {
        Cart myCart = new Cart();
        myCart.setId(cartId);
        myCart.setItems(items);
        myCart.setUser(user);
        return myCart;
    }

    private ModifyCartRequest makeCartRequest(long itemId, int quantity, String username) {
        ModifyCartRequest cartReq = new ModifyCartRequest();
        cartReq.setItemId(itemId);
        cartReq.setQuantity(quantity);
        cartReq.setUsername(username);
        return cartReq;
    }
}
