package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;

    @BeforeAll
    void setupDatabase() {
        database = new Database();
        cart = new ShoppingCartAdaptor(database);
    }

    @BeforeEach
    void resetBeforeEach() {
        database.resetDatabase();
    }

    @AfterAll
    void tearDown() {
        database.close();
    }

    @Test
    @DisplayName("specification-based: adding items updates database and retrieval works")
    void testAddAndRetrieveItems() {
        Item item = new Item(ItemType.OTHER, "Book", 2, 12.0);
        cart.add(item);
        List<Item> items = cart.getItems();

        assertEquals(1, items.size());
        assertEquals("Book", items.get(0).getName());
        assertEquals(ItemType.OTHER, items.get(0).getType());
    }



}