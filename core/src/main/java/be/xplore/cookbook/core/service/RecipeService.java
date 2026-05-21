package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.recipe.command.ChangeVisibilityCommand;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.DeleteRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.EnhanceRecipeQuery;
import be.xplore.cookbook.core.domain.recipe.command.FilterRecipesQuery;
import be.xplore.cookbook.core.domain.recipe.command.FindRecipeByIdQuery;
import be.xplore.cookbook.core.domain.recipe.command.ImportRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.IngredientWithQuantity;
import be.xplore.cookbook.core.domain.recipe.command.SearchRecipesByNameQuery;
import be.xplore.cookbook.core.domain.recipe.command.UpdateRecipeCommand;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.port.recipe.ImportedIngredient;
import be.xplore.cookbook.core.port.recipe.ImportedRecipe;
import be.xplore.cookbook.core.port.recipe.RecipeImportPort;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final RecipeSuggestionsPort aiPort;
    private final RecipeImportPort recipeImportPort;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                         UserRepository userRepository, UserPreferenceRepository userPreferenceRepository,
                         RecipeSuggestionsPort aiPort, RecipeImportPort recipeImportPort) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.aiPort = aiPort;
        this.recipeImportPort = recipeImportPort;
    }

    public Recipe createRecipe(CreateRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(command.userId()::notFound);

        List<RecipeIngredient> raw = mapToRecipeIngredients(command.ingredientQuantities());
        List<RecipeIngredient> unique = deduplicateIngredients(raw);

        return recipeRepository.save(new Recipe(
                RecipeId.create(),
                command.details(),
                unique,
                command.isPublic(),
                user
        ));
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
            preferences = userPreferenceRepository.findPreferences(user)
                    .orElseThrow(query.userId()::notFound);
        }

        return recipeRepository.findAllSummariesWithFilter(query.ingredientIds(), preferences,
                query.includeAccessibleRecipes(), user, query.paging());
    }

    public List<RecipeSummary> searchSummariesByName(SearchRecipesByNameQuery query) {
        var user = userRepository.findById(query.userId()).orElseThrow(UserNotFoundException::new);
        return recipeRepository.querySummaries(query.paging(), user, query.query());
    }

    public Recipe enhanceRecipe(EnhanceRecipeQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findById(query.recipeId(), user)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        SuggestedRecipeEnhancement suggestion = aiPort.enhanceRecipe(recipe);

        Ingredient ingredient = ingredientRepository
                .findByNameIgnoreCase(suggestion.newIngredient().name())
                .orElseGet(() -> ingredientRepository.save(new Ingredient(
                        IngredientId.create(),
                        suggestion.newIngredient().name(),
                        suggestion.newIngredient().unit(),
                        suggestion.newIngredient().categories()
                )));

        RecipeIngredient newRecipeIngredient = new RecipeIngredient(ingredient, suggestion.newIngredient().quantity());

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

    public void updateRecipe(UpdateRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(command.userId()::notFound);

        Recipe recipe = recipeRepository.findById(command.id(), user)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));

        List<RecipeIngredient> raw = mapToRecipeIngredients(command.ingredientQuantities());
        List<RecipeIngredient> unique = deduplicateIngredients(raw);

        recipe.updateDetails(
                command.details(),
                unique
        );

        recipe.changeVisibility(command.isPublic());

        recipeRepository.save(recipe);
    }

    public Recipe importRecipe(ImportRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        ImportedRecipe scraped = recipeImportPort.scrape(command.url());

        List<RecipeIngredient> raw = scraped.ingredients().stream()
                .map(this::resolveRecipeIngredient)
                .toList();

        List<RecipeIngredient> unique = deduplicateIngredients(raw);

        return recipeRepository.save(new Recipe(
                RecipeId.create(),
                new RecipeDetails(
                        scraped.title(),
                        scraped.description(),
                        scraped.durationInMinutes(),
                        scraped.servings(),
                        scraped.steps()
                ),
                unique,
                false,
                user
        ));
    }

    public void changeVisibility(ChangeVisibilityCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        recipe.changeVisibility(command.isPublic());

        recipeRepository.save(recipe);
    }

    public void deleteRecipe(DeleteRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        recipeRepository.delete(recipe);
    }

    private RecipeIngredient resolveRecipeIngredient(ImportedIngredient scraped) {
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(scraped.name())
                .orElseGet(() -> ingredientRepository.save(new Ingredient(
                        IngredientId.create(),
                        scraped.name(),
                        scraped.unit(),
                        scraped.categories()
                )));
        return new RecipeIngredient(ingredient, scraped.quantity());
    }

    private List<RecipeIngredient> mapToRecipeIngredients(List<IngredientWithQuantity> ingredientQuantities) {
        List<Ingredient> foundIngredients = ingredientRepository.findByIds(
                ingredientQuantities.stream().map(IngredientWithQuantity::ingredientId).toList());

        if (foundIngredients.size() != ingredientQuantities.size()) {
            throw new DataIntegrityException("One or more ingredients do not exist");
        }

        return ingredientQuantities.stream()
                .map(iwq -> {
                    Ingredient ingredient = foundIngredients.stream()
                            .filter(i -> i.id().equals(iwq.ingredientId()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new DataIntegrityException("Ingredient not found: " + iwq.ingredientId()));
                    return new RecipeIngredient(ingredient, iwq.quantity());
                })
                .toList();
    }

    private static List<RecipeIngredient> deduplicateIngredients(List<RecipeIngredient> ingredients) {
        return ingredients.stream()
                .collect(Collectors.toMap(
                        RecipeIngredient::ingredient,
                        Function.identity(),
                        RecipeIngredient::merge,
                        LinkedHashMap::new
                ))
                .values().stream()
                .toList();
    }
}
