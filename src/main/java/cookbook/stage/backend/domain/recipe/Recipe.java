package cookbook.stage.backend.domain.recipe;

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

    public Recipe(RecipeId id, String name, String description, int durationInMinutes,
                  List<String> steps, List<RecipeIngredient> ingredients, int servings) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A recipe must have a name");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A recipe must have a description");
        }
        if (durationInMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one step");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("A recipe must have at least one ingredient");
        }
        if (servings <= 0) {
            servings = 1;
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.ingredients = ingredients;
        this.servings = servings;
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
}
