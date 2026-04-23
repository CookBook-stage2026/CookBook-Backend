package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.user.UserId;

import java.util.List;

// Will not convert to record class or add final since other issues include editing the Recipe class.
public class Recipe {
    private final RecipeId id;
    private String name;
    private String description;
    private int durationInMinutes;
    private int servings;
    private final List<String> steps;
    private final List<RecipeIngredient> ingredients;
    private final UserId userId;

    public Recipe(RecipeId id, RecipeDetails details, List<RecipeIngredient> ingredients, UserId userId) {
        if (id == null) {
            throw new IllegalArgumentException("A recipe must have an id");
        }

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one ingredient");
        }

        if (userId == null) {
            throw new IllegalArgumentException("A recipe must have a creator");
        }

        this.id = id;
        this.name = details.name();
        this.description = details.description();
        this.durationInMinutes = details.durationInMinutes();
        this.servings = details.servings();
        this.steps = details.steps();
        this.ingredients = List.copyOf(ingredients);
        this.userId = userId;
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

    public UserId getUserId() {
        return userId;
    }
}
