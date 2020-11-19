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
}
