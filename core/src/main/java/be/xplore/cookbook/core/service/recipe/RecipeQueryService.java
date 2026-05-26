package be.xplore.cookbook.core.service.recipe;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.domain.exception.ForbiddenException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.recipe.command.EnhanceRecipeQuery;
import be.xplore.cookbook.core.domain.recipe.command.FilterRecipesQuery;
import be.xplore.cookbook.core.domain.recipe.command.FindRecipeByIdQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchHouseholdRecipesByNameQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchPersonalRecipesByNameQuery;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class RecipeQueryService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final HouseholdRepository householdRepository;
    private final RecipeSuggestionsPort recipeSuggestionsPort;

    public RecipeQueryService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                              UserRepository userRepository, UserPreferenceRepository preferenceRepository,
                              HouseholdRepository householdRepository, RecipeSuggestionsPort recipeSuggestionsPort) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.householdRepository = householdRepository;
        this.recipeSuggestionsPort = recipeSuggestionsPort;
    }

    public Recipe findById(FindRecipeByIdQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        return recipeRepository.findById(query.recipeId(), user)
                .orElseThrow(query.recipeId()::notFound);
    }

    public PagedResult<RecipeSummary> findAllSummariesWithFilter(FilterRecipesQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(query.userId()::notFound);

        UserPreferences preferences = UserPreferences.empty(user);

        if (query.shouldApplyPreferences()) {
            preferences = preferenceRepository.findPreferences(user)
                    .orElseThrow(query.userId()::notFound);
        }

        return recipeRepository.findAllSummariesWithFilter(query.ingredientIds(), preferences,
                query.includeAccessibleRecipes(), user, query.paging());
    }

    public List<RecipeSummary> searchPersonalSummariesByName(SearchPersonalRecipesByNameQuery query) {
        var user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        return recipeRepository.queryPersonalSummaries(query.paging(), user, query.query());
    }

    public List<RecipeSummary> searchHouseholdSummariesByName(SearchHouseholdRecipesByNameQuery query) {
        var user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        Household household = householdRepository.findById(query.householdId())
                .orElseThrow(() -> new NotFoundException("Household not found"));

        List<UserId> memberIds = new ArrayList<>(household.members()
                .stream()
                .map(User::id)
                .toList());

        memberIds.add(household.creator().id());

        if (!memberIds.contains(user.id())) {
            throw new ForbiddenException("User is not part of this household");
        }

        return recipeRepository.querySummaries(query.paging(), memberIds, query.query());
    }

    public Recipe enhanceRecipe(EnhanceRecipeQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findById(query.recipeId(), user)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        SuggestedRecipeEnhancement suggestion = recipeSuggestionsPort.enhanceRecipe(recipe);

        RecipeIngredient newRecipeIngredient = resolveRecipeIngredient(suggestion.newIngredient(), user);

        List<RecipeIngredient> updatedIngredients = new ArrayList<>(recipe.getIngredients());
        updatedIngredients.add(newRecipeIngredient);

        return new Recipe(
                recipe.getId(),
                new RecipeDetails(
                        recipe.getName(),
                        recipe.getDescription(),
                        suggestion.durationInMinutes(),
                        recipe.getServings(),
                        suggestion.updatedSteps()
                ),
                updatedIngredients,
                recipe.isPublic(),
                recipe.getUser()
        );
    }

    private RecipeIngredient resolveRecipeIngredient(
            SuggestedRecipeEnhancement.SuggestedIngredient newIngredient, User user) {
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCaseGlobalOrUser(newIngredient.name(), user)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(
                        IngredientId.create(),
                        newIngredient.name(),
                        newIngredient.unit(),
                        newIngredient.categories(),
                        user
                )));
        return new RecipeIngredient(ingredient, newIngredient.quantity());
    }
}
