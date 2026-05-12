package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.ingredient.Unit;

public record ScrapedIngredient(String name, Unit unit, double quantity) {
    public ScrapedIngredient {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scraped ingredient name cannot be blank");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Scraped ingredient unit cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Scraped ingredient quantity must be greater than 0");
        }
    }
}
