package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepo = mock (ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    ///////// Test 1 : Check Item List ///////////////////
    @Test
    public void checkGetItemsByName() {
        String item1_Name = "Revlon Lipstick";
        String item1_Description = "Luminous Red";
        Long item1_Id = 2L;
        BigDecimal item1_Price = new BigDecimal ("7.50");

        String item2_Name = "MaxFactor Lipstick";
        String item2_Description = "Coral";
        Long item2_Id = 3L;
        BigDecimal item2_Price = new BigDecimal ("10.50");

        Item item1 = makeItem(item1_Name, item1_Description, item1_Id, item1_Price);
        Item item2 = makeItem(item2_Name, item2_Description, item2_Id, item2_Price);

        List<Item> itemList = new ArrayList<Item>();
        itemList.add(item1);
        itemList.add(item2);

        when (itemRepo.findByName(item2_Name)).thenReturn(itemList);

        ResponseEntity<List<Item>> itemResponse = itemController.getItemsByName(item2_Name);
        assertNotNull(itemResponse);
        assertEquals(HttpStatus.OK, itemResponse.getStatusCode());

        List<Item> itemMatch = itemResponse.getBody();
        assertNotNull(itemMatch);
        assertEquals (item2_Id, itemMatch.get(1).getId());
        assertEquals(item2_Name, itemMatch.get(1).getName());
        assertEquals(item2_Description, itemMatch.get(1).getDescription());
        assertEquals(item2_Price, itemMatch.get(1).getPrice());
    }

    //////// Test 2 : Check Item by Id //////////////////////////
    @Test
    public void checkGetItemById() {
        String item1_Name = "Revlon Lipstick";
        String item1_Description = "Luminous Red";
        Long item1_Id = 2L;
        BigDecimal item1_Price = new BigDecimal ("7.50");

        Item item1 = makeItem(item1_Name, item1_Description, item1_Id, item1_Price);

        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));
        ResponseEntity<Item> resp = itemController.getItemById(2L);

        assertNotNull(resp);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        Item item = resp.getBody();
        assertNotNull(item);
        assertEquals(item1_Id, item.getId());
        assertEquals(item1_Name, item.getName());
        assertEquals(item1_Description, item.getDescription());
        assertEquals(item1_Price, item.getPrice());
    }

    ////////// Test 3 : Check for empty list of items /////////////
    @Test
    public void checkGetItems() {
        ResponseEntity<List<Item>> itemResponse = itemController.getItems();
        assertNotNull(itemResponse);
        assertEquals(HttpStatus.OK, itemResponse.getStatusCode());
        assertNotNull(itemResponse.getBody());
        assertEquals(0, itemResponse.getBody().size());
    }

    private Item makeItem(String itemName, String itemDescription, Long itemId, BigDecimal price) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setPrice(price);

        return item;
    }
}
