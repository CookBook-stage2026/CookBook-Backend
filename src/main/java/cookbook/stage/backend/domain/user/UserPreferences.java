package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.IngredientId;

import java.util.List;

public record UserPreferences(
        List<Category> excludedCategories,
        List<IngredientId> excludedIngredientIds
) {
    public UserPreferences {
        excludedCategories = excludedCategories == null ? List.of() : excludedCategories;
        excludedIngredientIds = excludedIngredientIds == null ? List.of() : excludedIngredientIds;
    }

    public static UserPreferences empty() {
        return new UserPreferences(List.of(), List.of());
    }
}
