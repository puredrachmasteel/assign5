package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class BarnesAndNobleTest {
    // Simple BookDatabase implementation
    static class FakeBookDatabase implements BookDatabase {
        private final Map<String, Book> store = new HashMap<>();

        public void addBook(Book b) {
            store.put(b.getPrice() + "-" + b.hashCode(), b);
        }

        @Override
        public Book findByISBN(String ISBN) {
            // simple way to test logic – ignore ISBN differences
            return store.values().iterator().next();
        }
    }

    // Simple BuyBookProcess implementation
    static class FakeBuyProcess implements BuyBookProcess {
        int totalBought = 0;
        @Override
        public void buyBook(Book book, int amount) {
            totalBought += amount;
        }
    }

    // Utility method for creating systems under test
    private BarnesAndNoble createSystem(Book b) {
        BookDatabase db = new BookDatabase() {
            @Override
            public Book findByISBN(String ISBN) {
                return b;
            }
        };
        BuyBookProcess process = new FakeBuyProcess();
        return new BarnesAndNoble(db, process);
    }

    // --- SPECIFICATION-BASED TESTS ---

    @Test
    @DisplayName("specification-based: returns null when order is null")
    void testNullOrderReturnsNull() {
        BarnesAndNoble bn = createSystem(new Book("123", 10, 5));
        assertNull(bn.getPriceForCart(null));
    }

    @Test
    @DisplayName("specification-based: valid single book fully in stock")
    void testBookInStockFullQuantity() {
        Book book = new Book("123", 20, 5);
        BarnesAndNoble bn = createSystem(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("123", 3);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(60, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("specification-based: book partially unavailable")
    void testBookPartiallyUnavailable() {
        Book book = new Book("123", 10, 2);
        BarnesAndNoble bn = createSystem(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("123", 5);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(20, summary.getTotalPrice());
        assertEquals(1, summary.getUnavailable().size());
        assertEquals(3, summary.getUnavailable().get(book)); // 5 - 2 unavailable
    }

}