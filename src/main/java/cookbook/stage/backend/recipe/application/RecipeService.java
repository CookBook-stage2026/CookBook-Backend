package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.ingredient.shared.IngredientsApi;
import cookbook.stage.backend.recipe.api.dto.RecipeIngredientDto;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
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

    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, List<RecipeIngredientDto> ingredients) {
        List<IngredientId> ingredientIds = ingredients.stream()
                .map(dto -> new IngredientId(dto.ingredientId()))
                .toList();

        ingredientsApi.assertAllExist(ingredientIds);

        RecipeId recipeId = RecipeId.create();

        List<RecipeIngredient> recipeIngredients = ingredients.stream()
                .map(dto -> dto.toDomain(recipeId))
                .toList();

        Recipe recipe = Recipe.createRecipe(recipeId, name, description, durationInMinutes, steps, recipeIngredients);

        return recipeRepository.save(recipe);
    }
}
