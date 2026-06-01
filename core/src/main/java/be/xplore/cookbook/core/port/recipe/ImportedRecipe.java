package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.recipe.Macro;

import java.util.List;

public record ImportedRecipe(
        String title,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        List<ImportedIngredient> ingredients,
        List<Macro> macros
) {
    public ImportedRecipe {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Scraped recipe title cannot be blank");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Scraped recipe must have at least one step");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Scraped recipe must have at least one ingredient");
        }
        if (macros == null || macros.isEmpty()) {
            throw new IllegalArgumentException("Scraped recipe must have at least one macro");
        }
        steps = List.copyOf(steps);
        ingredients = List.copyOf(ingredients);
    }
}
