package be.xplore.cookbook.core.domain.user;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;

import java.util.List;

public record UserPreferences(
        List<Category> excludedCategories,
        List<Ingredient> excludedIngredients
) {
    public UserPreferences {
        excludedCategories = excludedCategories == null ? List.of() : excludedCategories;
        excludedIngredients = excludedIngredients == null ? List.of() : excludedIngredients;
    }

    public static UserPreferences empty() {
        return new UserPreferences(List.of(), List.of());
    }
}
