package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.user.User;

import java.util.List;

public record Recipe(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        List<RecipeIngredient> ingredients,
        User user
) {
    public Recipe(RecipeId id, RecipeDetails details, List<RecipeIngredient> ingredients, User user) {
        this(id,
             details.name(),
             details.description(),
             details.durationInMinutes(),
             details.servings(),
             details.steps(),
             ingredients,
             user);
    }

    public Recipe {
        if (id == null) {
            throw new IllegalArgumentException("A recipe must have an id");
        }

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one ingredient");
        }

        if (user == null) {
            throw new IllegalArgumentException("A recipe must have a creator");
        }

        ingredients = List.copyOf(ingredients);
    }

    public RecipeSummary summarize() {
        return new RecipeSummary(id, name, description, durationInMinutes);
    }

    public RecipeId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public int getServings() {
        return servings;
    }

    public User getUser() {
        return user;
    }
}
