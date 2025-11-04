package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;
    private PriceRule mockRule;

    @BeforeEach
    void setup() {
        mockCart = mock(ShoppingCart.class);
        mockRule = mock(PriceRule.class);
    }

    @Test
    @DisplayName("specification-based: calculate total with one rule applied")
    void testSingleRuleAppliedCorrectly() {
        Item item = new Item(ItemType.OTHER, "Book", 2, 10.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        when(mockRule.priceToAggregate(List.of(item))).thenReturn(20.0);

        Amazon amazon = new Amazon(mockCart, List.of(mockRule));
        double total = amazon.calculate();

        assertEquals(20.0, total);
        verify(mockRule, times(1)).priceToAggregate(List.of(item));
    }

    @Test
    @DisplayName("specification-based: add item delegates to cart.add")
    void testAddToCartDelegatesProperly() {
        Amazon amazon = new Amazon(mockCart, List.of());
        Item item = new Item(ItemType.ELECTRONIC, "Headphones", 1, 50.0);

        amazon.addToCart(item);
        verify(mockCart, times(1)).add(item);
    }



}