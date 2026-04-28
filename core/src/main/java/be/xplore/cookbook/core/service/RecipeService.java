package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.RecipeRepository;
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
