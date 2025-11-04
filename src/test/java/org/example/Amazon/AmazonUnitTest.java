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

    @Test
    @DisplayName("structural-based: Database.close() with null connection skips close")
    void testDatabaseCloseWithNullConnection() {
        Database db = new Database();
        // forcibly nullify connection to simulate the false branch
        db.close();   // first close ensures connection becomes null
        assertDoesNotThrow(db::close); // second close hits the if(connection != null) false branch
    }

    @Test
    @DisplayName("structural-based: Database constructor skips initialization when connection already exists")
    void testDatabaseConstructorConnectionAlreadyExists() {
        Database db1 = new Database(); // initializes connection
        Database db2 = new Database(); // should hit if(connection != null) return;
        assertNotNull(db2.getConnection()); // still works, connection reused
    }

    @Test
    @DisplayName("structural-based: calculate aggregates multiple rules")
    void testMultipleRulesAggregate() {
        Item item = new Item(ItemType.OTHER, "Notebook", 1, 10.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        when(mockRule.priceToAggregate(List.of(item))).thenReturn(10.0);

        PriceRule mockRule2 = mock(PriceRule.class);
        when(mockRule2.priceToAggregate(List.of(item))).thenReturn(5.0);

        Amazon amazon = new Amazon(mockCart, List.of(mockRule, mockRule2));
        double total = amazon.calculate();

        assertEquals(15.0, total);
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice - zero items returns 0")
    void testDeliveryPrice_zeroItems() {
        DeliveryPrice rule = new DeliveryPrice();
        assertEquals(0.0, rule.priceToAggregate(List.of()));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice - 1..3 items returns 5")
    void testDeliveryPrice_oneToThree() {
        DeliveryPrice rule = new DeliveryPrice();
        // 1 item
        assertEquals(5.0, rule.priceToAggregate(List.of(new Item(ItemType.OTHER, "A", 1, 1.0))));
        // 3 items
        assertEquals(5.0, rule.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0)
        )));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice - 4..10 items returns 12.5")
    void testDeliveryPrice_fourToTen() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> fourItems = List.of(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0),
                new Item(ItemType.OTHER, "D", 1, 1.0)
        );
        assertEquals(12.5, rule.priceToAggregate(fourItems));

        // 10 items
        List<Item> tenItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) tenItems.add(new Item(ItemType.OTHER, "P" + i, 1, 1.0));
        assertEquals(12.5, rule.priceToAggregate(tenItems));
    }

    @Test
    @DisplayName("structural-based: DeliveryPrice - >10 items returns 20.0")
    void testDeliveryPrice_moreThanTen() {
        DeliveryPrice rule = new DeliveryPrice();
        List<Item> elevenItems = new ArrayList<>();
        for (int i = 0; i < 11; i++) elevenItems.add(new Item(ItemType.OTHER, "P" + i, 1, 1.0));
        assertEquals(20.0, rule.priceToAggregate(elevenItems));
    }

    @Test
    @DisplayName("structural-based: ExtraCostForElectronics true/false")
    void testExtraCostForElectronics() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();

        List<Item> noElectronics = List.of(new Item(ItemType.OTHER, "Pen", 1, 2.0));
        assertEquals(0.0, rule.priceToAggregate(noElectronics));

        List<Item> hasElectronics = List.of(new Item(ItemType.ELECTRONIC, "Tablet", 1, 200.0));
        assertEquals(7.5, rule.priceToAggregate(hasElectronics));
    }

    @Test
    @DisplayName("structural-based: RegularCost loop & accumulation")
    void testRegularCostCalculation() {
        RegularCost rule = new RegularCost();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Pencil", 2, 1.5),
                new Item(ItemType.OTHER, "Notebook", 1, 5.0)
        );

        double total = rule.priceToAggregate(items);
        assertEquals(8.0, total);
    }

    @Test
    @DisplayName("structural-based: Database.withSql wraps SQLException as RuntimeException")
    void testDatabaseWithSqlThrowsRuntimeException() {
        Database db = new Database();

        RuntimeException re = assertThrows(RuntimeException.class, () ->
                db.withSql(() -> { throw new SQLException("forced"); })
        );

        assertNotNull(re.getCause());
        assertTrue(re.getCause() instanceof SQLException);
    }

}