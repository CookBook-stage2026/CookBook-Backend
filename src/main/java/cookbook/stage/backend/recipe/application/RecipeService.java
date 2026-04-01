package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.recipe.api.dto.IngredientDto;
import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, List<IngredientDto> ingredients) {
        List<Ingredient> domainIngredients = ingredients.stream()
                .map(i -> new Ingredient(i.name(), i.quantity(), i.unit()))
                .toList();

        Recipe recipe = Recipe.createRecipe(name, description, durationInMinutes, steps, domainIngredients);

        return recipeRepository.save(recipe);
    }
}
