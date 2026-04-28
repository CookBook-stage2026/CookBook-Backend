package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.DataIntegrityException;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeDetails;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.recipe.RecipeSummary;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
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
    private final UserService userService;
    private final UserPreferenceService userPreferenceService;

    public RecipeService(RecipeRepository recipeRepository, IngredientService ingredientService,
                         UserService userService, UserPreferenceService userPreferenceService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
    }

    @Transactional
    public Recipe createRecipe(RecipeDetails recipeDetails,
                               Map<IngredientId, Double> ingredientQuantities, UserId userId) {
        User user = userService.findById(userId)
                .orElseThrow(userId::notFound);

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
                RecipeId.create(),
                recipeDetails,
                recipeIngredients, user.getId()
        ));
    }

    public Recipe findById(RecipeId id, UserId userId) {
        return recipeRepository.findById(id, userId)
                .orElseThrow(id::notFound);
    }

    public Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, Pageable pageable,
                                                          boolean shouldApplyPreferences, UserId userId) {
        userService.findById(userId)
                .orElseThrow(userId::notFound);

        if (shouldApplyPreferences) {
            UserPreferences preferences = userPreferenceService.findPreferences(userId);
            return recipeRepository.findAllSummariesWithFilter(ingredientIds, preferences, userId, pageable);
        }

        return recipeRepository.findAllSummariesWithFilter(ingredientIds, UserPreferences.empty(), userId, pageable);
    }

    public List<RecipeSummary> searchSummariesByName(Pageable pageable, UserId userId, String query) {
        return recipeRepository.querySummaries(pageable, userId, query);
    }
}
