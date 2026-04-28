package be.xplore.cookbook.core.domain.recipe;

import java.util.List;

public record RecipeDetails(
        String name,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps
) {
    public RecipeDetails {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A recipe must have a name");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A recipe must have a description");
        }

        if (durationInMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        if (servings <= 0) {
            throw new IllegalArgumentException("Servings must be greater than 0");
        }

        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one step");
        }

        steps = List.copyOf(steps);
    }
}
