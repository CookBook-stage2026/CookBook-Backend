package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.user.User;

import java.util.List;

public class Recipe {

    private final RecipeId id;
    private RecipeDetails details;
    private List<RecipeIngredient> ingredients;
    private boolean isPublic;
    private final User user;

    public Recipe(
            RecipeId id,
            RecipeDetails details,
            List<RecipeIngredient> ingredients,
            boolean isPublic,
            User user
    ) {
        if (id == null) {
            throw new IllegalArgumentException("A recipe must have an id");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one ingredient");
        }
        if (user == null) {
            throw new IllegalArgumentException("A recipe must have a creator");
        }

        this.id = id;
        this.details = details;
        this.ingredients = List.copyOf(ingredients);
        this.isPublic = isPublic;
        this.user = user;
    }

    public RecipeSummary summarize() {
        return new RecipeSummary(id, details.name(), details.description(), details.durationInMinutes(), user);
    }

    public RecipeId getId() {
        return id;
    }

    public String getName() {
        return details.name();
    }

    public String getDescription() {
        return details.description();
    }

    public int getDurationInMinutes() {
        return details.durationInMinutes();
    }

    public int getServings() {
        return details.servings();
    }

    public List<String> getSteps() {
        return details.steps();
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public User getUser() {
        return user;
    }

    public void changeVisibility(boolean newIsPublic) {
        this.isPublic = newIsPublic;
    }

    public void updateDetails(RecipeDetails newDetails, List<RecipeIngredient> newIngredients) {
        this.details = newDetails;
        this.ingredients = List.copyOf(newIngredients);
    }
}
