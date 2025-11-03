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
}