package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.DataIntegrityException;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.recipe.RecipeSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;

    public RecipeService(RecipeRepository recipeRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
    }

    @Transactional
    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, Map<IngredientId, Double> ingredientQuantities, int servings) {
        List<IngredientId> ingredientIds = ingredientQuantities.keySet().stream()
                .toList();

        List<Ingredient> foundIngredients = ingredientService.getIngredientsByIds(ingredientIds);

        if (foundIngredients.size() != ingredientIds.size()) {
            throw new DataIntegrityException("One or more ingredients do not exist");
        }

        List<RecipeIngredient> recipeIngredients = foundIngredients.stream()
                .map(ingredient -> new RecipeIngredient(
                        ingredient,
                        ingredientQuantities.get(ingredient.id())
                ))
                .toList();

        return recipeRepository.save(new Recipe(
                RecipeId.create(), name, description, durationInMinutes, steps, recipeIngredients, servings
        ));
    }

    public Recipe findById(RecipeId id) {
        return recipeRepository.findById(id).orElseThrow(id::notFound);
    }

    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return recipeRepository.findAllSummaries(pageable);
    }
}
