package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.ingredient.shared.IngredientApiDto;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.ingredient.shared.IngredientsApi;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.domain.RecipeSummary;
import cookbook.stage.backend.recipe.shared.RecipeId;
import cookbook.stage.backend.shared.domain.DataIntegrityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientsApi ingredientsApi;

    public RecipeService(RecipeRepository recipeRepository, IngredientsApi ingredientsApi) {
        this.recipeRepository = recipeRepository;
        this.ingredientsApi = ingredientsApi;
    }

    public RecipeWithIngredients createRecipe(String name, String description, int durationInMinutes,
                                              List<String> steps, List<RecipeIngredient> ingredients, int servings) {
        List<IngredientId> ingredientIds = ingredients.stream()
                .map(RecipeIngredient::ingredientId)
                .toList();

        Map<IngredientId, IngredientApiDto> ingredientMap = ingredientsApi.getIngredientsByIds(ingredientIds)
                .stream()
                .collect(toMap(IngredientApiDto::id, identity()));

        if (ingredientMap.size() != ingredients.size()) {
            throw new DataIntegrityException("One or more ingredients do not exist");
        }

        Recipe recipe = recipeRepository.save(new Recipe(RecipeId.create(),
                name, description, durationInMinutes, steps, ingredients, servings));

        return new RecipeWithIngredients(recipe, ingredientMap);
    }

    @Transactional(readOnly = true)
    public RecipeWithIngredients findById(RecipeId id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(id::notFound);

        List<IngredientId> ingredientIds = recipe.getIngredients().stream()
                .map(RecipeIngredient::ingredientId)
                .toList();

        Map<IngredientId, IngredientApiDto> ingredientMap = ingredientsApi.getIngredientsByIds(ingredientIds)
                .stream()
                .collect(toMap(IngredientApiDto::id, identity()));

        return new RecipeWithIngredients(recipe, ingredientMap);
    }

    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return recipeRepository.findAllSummaries(pageable);
    }
}
