package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.user.UserPreferences;

import java.util.List;

public record UserPreferencesDto(
        List<Category> excludedCategories,
        List<IngredientSummaryDto> excludedIngredients
) {
    public static UserPreferencesDto fromDomain(UserPreferences preferences) {
        return new UserPreferencesDto(
                preferences.excludedCategories(),
                preferences.excludedIngredients().stream()
                        .map(IngredientSummaryDto::fromDomain)
                        .toList()
        );
    }
}
