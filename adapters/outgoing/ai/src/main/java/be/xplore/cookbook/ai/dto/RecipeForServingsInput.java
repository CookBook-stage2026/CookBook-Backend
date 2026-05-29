package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.Recipe;

import java.util.List;

public record RecipeForServingsInput(
        String name,
        String description,
        int durationInMinutes,
        int currentServings,
        int desiredServings,
        List<String> steps,
        List<IngredientInput> ingredients
) {

    public record IngredientInput(String name, double quantity, String unit) {
    }

    public static RecipeForServingsInput fromDomain(Recipe recipe, int desiredServings) {
        List<IngredientInput> ingredientInputs = recipe.getIngredients().stream()
                .map(ri -> new IngredientInput(
                        ri.ingredient().name(),
                        ri.baseQuantity(),
                        ri.unit().name()))
                .toList();

        return new RecipeForServingsInput(
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getServings(),
                desiredServings,
                recipe.getSteps(),
                ingredientInputs
        );
    }
}
