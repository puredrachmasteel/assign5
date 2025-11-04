# BarnesAndNoble Testing — Assignment 5 Part 1

## Project Overview
This project implements a simplified **Barnes & Noble checkout system** consisting of the following classes:

- `BarnesAndNoble` — Main class that coordinates book retrieval and purchase summarization.
- `Book` — Represents individual book items with ISBN, price, and quantity.
- `BookDatabase` — Interface for locating books by ISBN.
- `BuyBookProcess` — Interface for simulating book purchases.
- `PurchaseSummary` — Tracks total cost and unavailable quantities.

The goal of this part was to **practice writing and organizing tests** by performing both:
1. **Specification-Based Testing** — Derived directly from requirements and expected behavior.
2. **Structural-Based Testing** — Created to ensure coverage of all code branches, including the `equals()` method in `Book` and conditional paths in `BarnesAndNoble`.

### Specification-Based Tests
These verify correctness according to the intended specifications:
- Null inputs (`order == null`) should return `null`.
- Books fully in stock calculate correct total price.
- Books partially unavailable correctly register missing quantities and adjust totals.

---

# Assignment 5 Part 2

[![SE333_CI](https://github.com/puredrachmasteel/assign5/actions/workflows/SE333_CI.yml/badge.svg)](https://github.com/puredrachmasteel/assign5/actions/workflows/SE333_CI.yml)

---

# Amazon Testing — Assignment 5 Part 3

## Project Overview
This project implements a simplified **Amazon checkout system** consisting of the following classes:

- `Amazon` — Main class that coordinates shopping cart interactions and price calculation.
- `Item` — Represents individual items with type, name, quantity, and price per unit.
- `ShoppingCart` — Interface for cart operations such as adding items and retrieving contents.
- `ShoppingCartAdaptor` — Database-backed implementation of `ShoppingCart`.
- `Database` — Naive in-memory database connection class.
- `PriceRule` and its implementations (`RegularCost`, `DeliveryPrice`, `ExtraCostForElectronics`) — Represent rules for aggregating item prices.

The goal of this part was to **practice writing integration and unit tests**, including both specification-based and structural-based tests, while using **mocking** to isolate unit test dependencies.

### Specification-Based Tests
These verify correctness according to the intended specifications:
- Adding items to the cart persists them correctly in the database.
- `Amazon.calculate()` returns correct totals when combining multiple pricing rules.
- `Amazon.addToCart()` properly delegates to the shopping cart interface.
- `Database.resetDatabase()` clears the cart as expected.

### Structural-Based Tests
These were created to ensure **all code branches are executed**, including:
- Each branch of `DeliveryPrice` for 0, 1–3, 4–10, and >10 items.
- Looping and accumulation in `RegularCost`.
- `ExtraCostForElectronics` for carts with and without electronic items.
- Exception wrapping in `Database.withSql(...)`.

#### Note on Unreachable Branches
Coverage tools may indicate that the following AND conditions in `DeliveryPrice` are missing false hits:

```java
if(totalItems >= 1 && totalItems <= 3)
if(totalItems >= 4 && totalItems <= 10)

// totalItems >= 1 and totalItems >= 4 will never be false, 
// because the previous statement ensures totalItems is above 0,
// and then above 3.