package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
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
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.List;
import java.util.Map;

public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                         UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public Recipe createRecipe(RecipeDetails recipeDetails,
                               Map<IngredientId, Double> ingredientQuantities, UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(userId::notFound);

        List<IngredientId> ingredientIds = ingredientQuantities.keySet().stream()
                .toList();

        List<Ingredient> foundIngredients = ingredientRepository.findByIds(ingredientIds);

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
                recipeIngredients, user.id()
        ));
    }

    public Recipe findById(RecipeId id, UserId userId) {
        return recipeRepository.findById(id, userId)
                .orElseThrow(id::notFound);
    }

    public PagedResult<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, Paging pageable,
                                                                 boolean shouldApplyPreferences, UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(userId::notFound);

        if (shouldApplyPreferences) {
            UserPreferences preferences = userPreferenceRepository.findPreferences(user)
                    .orElseThrow(userId::notFound);
            return recipeRepository.findAllSummariesWithFilter(ingredientIds, preferences, userId, pageable);
        }

        return recipeRepository.findAllSummariesWithFilter(
                ingredientIds, UserPreferences.empty(user), userId, pageable);
    }

    public List<RecipeSummary> searchSummariesByName(Paging pageable, UserId userId, String query) {
        return recipeRepository.querySummaries(pageable, userId, query);
    }
}
