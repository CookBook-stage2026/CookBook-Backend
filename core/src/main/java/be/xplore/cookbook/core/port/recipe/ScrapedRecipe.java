package be.xplore.cookbook.core.port.recipe;

import java.util.List;

public record ScrapedRecipe(
        String title,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        List<ScrapedIngredient> ingredients
) {
    public ScrapedRecipe {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Scraped recipe title cannot be blank");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Scraped recipe must have at least one step");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Scraped recipe must have at least one ingredient");
        }
        steps = List.copyOf(steps);
        ingredients = List.copyOf(ingredients);
    }
}
