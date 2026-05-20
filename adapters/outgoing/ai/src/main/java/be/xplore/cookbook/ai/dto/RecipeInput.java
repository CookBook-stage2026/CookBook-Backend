package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;

import java.util.List;

public record RecipeInput(
        String name,
        String description,
        int durationInMinutes,
        List<String> steps,
        List<IngredientInput> ingredients,
        int servings
) {
    public static RecipeInput fromDomain(Recipe recipe) {
        List<IngredientInput> ingredientInputs = recipe.getIngredients().stream()
                .map(IngredientInput::fromDomain)
                .toList();

        return new RecipeInput(
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                ingredientInputs,
                recipe.getServings()
        );
    }

    public record IngredientInput(
            String name,
            double quantity,
            Unit unit,
            List<Category> categories
    ) {
        public static IngredientInput fromDomain(RecipeIngredient ri) {
            Ingredient ingredient = ri.ingredient();
            return new IngredientInput(
                    ingredient.name(),
                    ri.baseQuantity(),
                    ingredient.unit(),
                    ingredient.categories()
            );
        }
    }
}
