package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.FilterRecipesQuery;
import be.xplore.cookbook.core.domain.recipe.command.FindRecipeByIdQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchRecipesByNameQuery;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.List;

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

    public Recipe createRecipe(CreateRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(command.userId()::notFound);

        List<Ingredient> foundIngredients = ingredientRepository.findByIds(
                command.ingredientQuantities().keySet().stream().toList());

        if (foundIngredients.size() != command.ingredientQuantities().size()) {
            throw new DataIntegrityException("One or more ingredients do not exist");
        }

        List<RecipeIngredient> recipeIngredients = foundIngredients.stream()
                .map(ingredient ->
                        new RecipeIngredient(ingredient, command.ingredientQuantities().get(ingredient.id())))
                .toList();

        return recipeRepository.save(new Recipe(RecipeId.create(), command.details(),
                recipeIngredients, user
        ));
    }

    public Recipe findById(FindRecipeByIdQuery query) {
        return recipeRepository.findById(query.recipeId(), query.userId())
                .orElseThrow(query.recipeId()::notFound);
    }

    public PagedResult<RecipeSummary> findAllSummariesWithFilter(FilterRecipesQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(query.userId()::notFound);

        if (query.shouldApplyPreferences()) {
            UserPreferences preferences = userPreferenceRepository.findPreferences(user)
                    .orElseThrow(query.userId()::notFound);
            return recipeRepository.findAllSummariesWithFilter(query.ingredientIds(), preferences,
                    user, query.paging());
        }

        return recipeRepository.findAllSummariesWithFilter(
                query.ingredientIds(), UserPreferences.empty(user), user, query.paging());
    }

    public List<RecipeSummary> searchSummariesByName(SearchRecipesByNameQuery query) {
        var user = userRepository.findById(query.userId()).orElseThrow(UserNotFoundException::new);
        return recipeRepository.querySummaries(query.paging(), user, query.query());
    }
}
