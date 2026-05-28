package be.xplore.cookbook.core.domain.ingredient;

import be.xplore.cookbook.core.domain.user.User;

import java.util.List;

public record Ingredient(
        IngredientId id,
        String name,
        Unit defaultUnit,
        List<Category> categories,
        User user
) {
    public Ingredient {
        if (id == null) {
            throw new IllegalArgumentException("Ingredient id cannot be null!");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank!");
        }
        if (defaultUnit == null) {
            throw new IllegalArgumentException("Ingredient default unit cannot be null!");
        }
        if (categories == null) {
            throw new IllegalArgumentException("Ingredient categories cannot be null!");
        }

        name = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
        categories = List.copyOf(categories);
    }
}
