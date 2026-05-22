package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Macro;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;

public record ImportedIngredient(String name, Unit unit, double quantity, List<Category> categories,
                                 List<Macro> macros) {
    public ImportedIngredient {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scraped ingredient name cannot be blank");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Scraped ingredient unit cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Scraped ingredient quantity must be greater than 0");
        }
        if (macros == null) {
            throw new IllegalArgumentException("Ingredient macros cannot be null!");
        }
    }
}
