package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;

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
