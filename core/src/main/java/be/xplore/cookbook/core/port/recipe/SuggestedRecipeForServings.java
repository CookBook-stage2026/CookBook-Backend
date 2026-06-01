package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Macro;

import java.util.List;

public record SuggestedRecipeForServings(
        int servings,
        int durationInMinutes,
        List<String> updatedSteps,
        List<ScaledIngredient> scaledIngredients,
        List<Macro> macros
) {

    public record ScaledIngredient(String name, double quantity, Unit unit) {
    }
}
