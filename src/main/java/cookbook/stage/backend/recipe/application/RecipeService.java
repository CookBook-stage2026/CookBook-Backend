package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.recipe.api.dto.CreateRecipeIngredientDto;
import cookbook.stage.backend.recipe.domain.exception.DataIntegrityException;
import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.recipe.Recipe;
import cookbook.stage.backend.recipe.domain.recipe.RecipeId;
import cookbook.stage.backend.recipe.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.recipe.RecipeRepository;
import cookbook.stage.backend.recipe.domain.recipe.RecipeSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;

    public RecipeService(RecipeRepository recipeRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
    }

    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, List<CreateRecipeIngredientDto> ingredientDtos, int servings) {
        List<IngredientId> ingredientIds = ingredientDtos.stream()
                .map(i -> new IngredientId(i.ingredientId()))
                .toList();

        int foundCount = ingredientService.getIngredientsByIds(ingredientIds).size();
        if (foundCount != ingredientIds.size()) {
            throw new DataIntegrityException("One or more ingredients do not exist");
        }

        List<RecipeIngredient> recipeIngredients = ingredientDtos.stream()
                .map(i -> new RecipeIngredient(
                        new IngredientId(i.ingredientId()),
                        i.baseQuantity()
                ))
                .toList();

        return recipeRepository.save(new Recipe(
                RecipeId.create(), name, description, durationInMinutes, steps, recipeIngredients, servings
        ));
    }

    public Recipe findById(RecipeId id) {
        return recipeRepository.findById(id).orElseThrow(id::notFound);
    }

    public Map<IngredientId, Ingredient> getIngredientMapForRecipe(Recipe recipe) {
        List<IngredientId> ids = recipe.getIngredients().stream()
                .map(RecipeIngredient::ingredientId)
                .toList();

        return ingredientService.getIngredientsByIds(ids).stream()
                .collect(toMap(Ingredient::id, identity()));
    }

    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return recipeRepository.findAllSummaries(pageable);
    }
}
