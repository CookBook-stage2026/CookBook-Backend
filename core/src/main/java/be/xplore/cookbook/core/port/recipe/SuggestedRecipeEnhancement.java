package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Macro;

import java.util.List;

public record SuggestedRecipeEnhancement(
        int durationInMinutes,
        SuggestedIngredient newIngredient,
        List<String> updatedSteps,
        List<Macro> macros
) {
    public record SuggestedIngredient(
            String name,
            double quantity,
            Unit unit,
            List<Category> categories
    ) {
    }
}
