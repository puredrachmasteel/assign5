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

    // --- STRUCTURAL-BASED TESTS ---

    @Test
    @DisplayName("structural-based: order is not null → loop executes")
    void testOrderLoopRuns() {
        Book book = new Book("999", 5, 1);
        BarnesAndNoble bn = createSystem(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("999", 1);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(5, summary.getTotalPrice());
    }

    @Test
    @DisplayName("structural-based: pads <= 0 equivalent path (no padding case analogue)")
    void testZeroQuantity() {
        Book book = new Book("321", 15, 0);
        BarnesAndNoble bn = createSystem(book);

        Map<String, Integer> order = new HashMap<>();
        order.put("321", 1);

        PurchaseSummary summary = bn.getPriceForCart(order);

        // should add unavailable for full quantity
        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().containsKey(book));
    }

    @Test
    @DisplayName("structural-based: multiple books iteration path")
    void testMultipleBooksPath() {
        Book book1 = new Book("A", 10, 5);
        Book book2 = new Book("B", 20, 2);

        BookDatabase db = new BookDatabase() {
            @Override
            public Book findByISBN(String ISBN) {
                return ISBN.equals("A") ? book1 : book2;
            }
        };
        BuyBookProcess process = new FakeBuyProcess();

        BarnesAndNoble bn = new BarnesAndNoble(db, process);

        Map<String, Integer> order = new HashMap<>();
        order.put("A", 1);
        order.put("B", 2);

        PurchaseSummary summary = bn.getPriceForCart(order);

        assertEquals(50, summary.getTotalPrice()); // 10 + 40
    }

    @Test
    @DisplayName("structural-based: same object reference returns true")
    void testEqualsSameReference() {
        Book book = new Book("123", 10, 2);
        assertEquals(book, book); // this == o
    }

    @Test
    @DisplayName("structural-based: comparing with null returns false")
    void testEqualsNullObject() {
        Book book = new Book("123", 10, 2);
        assertNotEquals(null, book); // o == null
    }

    @Test
    @DisplayName("structural-based: comparing with different class returns false")
    void testEqualsDifferentClass() {
        Book book = new Book("123", 10, 2);
        String notABook = "123";
        assertNotEquals(book, notABook); // getClass() != o.getClass()
    }

    @Test
    @DisplayName("structural-based: comparing different books with same ISBN returns true")
    void testEqualsSameISBN() {
        Book book1 = new Book("XYZ", 15, 3);
        Book book2 = new Book("XYZ", 25, 1); // same ISBN, diff price/qty
        assertEquals(book1, book2); // ISBN.equals(book.ISBN) == true
    }

    @Test
    @DisplayName("structural-based: comparing different books with different ISBN returns false")
    void testEqualsDifferentISBN() {
        Book book1 = new Book("ABC", 10, 3);
        Book book2 = new Book("DEF", 10, 3);
        assertNotEquals(book1, book2); // ISBN.equals(book.ISBN) == false
    }

    @Test
    @DisplayName("structural-based: explicit null check path in equals")
    void testEqualsExplicitNullPath() {
        Book book = new Book("123", 10, 2);
        Object o = null;
        assertNotEquals(book, o); // hits o == null true branch
    }


}