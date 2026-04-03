package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.ingredient.shared.IngredientsApi;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.domain.RecipeSummary;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientsApi ingredientsApi;

    public RecipeService(RecipeRepository recipeRepository, IngredientsApi ingredientsApi) {
        this.recipeRepository = recipeRepository;
        this.ingredientsApi = ingredientsApi;
    }

    public Recipe createRecipe(RecipeId id, String name, String description, int durationInMinutes,
                               List<String> steps, List<RecipeIngredient> ingredients, int servings) {
        ingredientsApi.assertAllExist(ingredients.stream()
                .map(RecipeIngredient::ingredientId)
                .toList());

        Recipe recipe = new Recipe(id, name, description, durationInMinutes, steps, ingredients, servings);
        return recipeRepository.save(recipe);
    }

    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return recipeRepository.findAllSummaries(pageable);
    }
}