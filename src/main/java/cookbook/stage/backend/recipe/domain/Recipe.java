package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;
import java.util.Map;

@AggregateRoot
public class Recipe {
    @Identity
    private RecipeId id;
    private String name;
    private String description;
    private int durationInMinutes;
    private List<String> steps;
    private Map<String, IngredientAmount> ingredients;

    public Recipe(RecipeId id, String name, String description, int durationInMinutes,
                  List<String> steps, Map<String, IngredientAmount> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public static Recipe createRecipe(String name, String description, int durationInMinutes,
                                      List<String> steps, Map<String, IngredientAmount> ingredients) {
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

        return new Recipe(RecipeId.create(), name, description, durationInMinutes, steps, ingredients);
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

    public Map<String, IngredientAmount> getIngredients() {
        return ingredients;
    }
}
