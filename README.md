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