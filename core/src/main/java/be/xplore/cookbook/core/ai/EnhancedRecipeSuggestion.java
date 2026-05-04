package be.xplore.cookbook.core.ai;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;

public record EnhancedRecipeSuggestion(
        int durationInMinutes,
        NewAiIngredient newIngredient,
        List<String> updatedSteps
) {
    public record NewAiIngredient(
            String name,
            double quantity,
            Unit unit,
            List<Category> categories
    ) {
    }
}
