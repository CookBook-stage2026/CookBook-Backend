package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
public class Recipe {
    @Identity
    private RecipeId id;
    private String name;
    private String description;
    private int durationInMinutes;
    private List<String> steps;
    private List<RecipeIngredient> ingredients;

    public Recipe(RecipeId id, String name, String description, int durationInMinutes, List<String> steps, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public static Recipe createRecipe(String name, String description, int durationInMinutes, List<String> steps, List<RecipeIngredient> ingredients){
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

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }
}
