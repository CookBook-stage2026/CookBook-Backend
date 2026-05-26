package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.user.User;

import java.util.List;

public class Recipe {

    private final RecipeId id;
    private RecipeDetails details;
    private List<RecipeIngredient> ingredients;
    private boolean isPublic;
    private final User user;
    private List<Macro> macros;

    public Recipe(
            RecipeId id,
            RecipeDetails details,
            List<RecipeIngredient> ingredients,
            boolean isPublic,
            User user,
            List<Macro> macros
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
        if (macros == null) {
            throw new IllegalArgumentException("Recipe macros cannot be null");
        }

        this.id = id;
        this.details = details;
        this.ingredients = List.copyOf(ingredients);
        this.isPublic = isPublic;
        this.user = user;
        this.macros = List.copyOf(macros);
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

    public List<Macro> getMacros() {
        return macros;
    }

    public void changeVisibility(boolean newIsPublic) {
        this.isPublic = newIsPublic;
    }

    public void updateDetails(RecipeDetails newDetails, List<RecipeIngredient> newIngredients) {
        this.details = newDetails;
        this.ingredients = List.copyOf(newIngredients);
    }

    public void updateMacros(List<Macro> newMacros) {
        if (newMacros == null) {
            throw new IllegalArgumentException("Recipe macros cannot be null");
        }
        this.macros = List.copyOf(newMacros);
    }
}
