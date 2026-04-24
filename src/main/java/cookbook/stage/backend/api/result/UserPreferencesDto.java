package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Category;

import java.util.List;
import java.util.UUID;

public record UserPreferencesDto(
        List<Category> excludedCategories,
        List<UUID> excludedIngredientIds
) {
}
