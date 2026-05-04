package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.user.UserPreferences;

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
