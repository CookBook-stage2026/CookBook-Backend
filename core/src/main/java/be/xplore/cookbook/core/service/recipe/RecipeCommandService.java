package be.xplore.cookbook.core.service.recipe;

import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.command.ChangeVisibilityCommand;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.DeleteRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.ImportRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.IngredientWithQuantity;
import be.xplore.cookbook.core.domain.recipe.command.UpdateRecipeCommand;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.core.port.recipe.ImportedIngredient;
import be.xplore.cookbook.core.port.recipe.ImportedRecipe;
import be.xplore.cookbook.core.port.recipe.RecipeImportPort;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecipeCommandService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final WeekScheduleRepository scheduleRepository;
    private final RecipeImportPort recipeImportPort;

    public RecipeCommandService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                                UserRepository userRepository, WeekScheduleRepository scheduleRepository,
                                RecipeImportPort recipeImportPort
    ) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
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
                .map(i -> resolveRecipeIngredient(i, user))
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

        if (!command.isPublic()) {
            scheduleRepository.deleteByRecipeIdAndOwnerType(recipe.getId(), ScheduleOwnerType.HOUSEHOLD);
        }
    }

    public void deleteRecipe(DeleteRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        scheduleRepository.deleteByRecipeIdAndOwnerType(recipe.getId(), ScheduleOwnerType.PERSONAL);
        scheduleRepository.deleteByRecipeIdAndOwnerType(recipe.getId(), ScheduleOwnerType.HOUSEHOLD);

        recipeRepository.delete(recipe);
    }

    private RecipeIngredient resolveRecipeIngredient(ImportedIngredient scraped, User user) {
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCaseGlobalOrUser(scraped.name(), user)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(
                        IngredientId.create(),
                        scraped.name(),
                        scraped.unit(),
                        scraped.categories(),
                        user
                )));
        return new RecipeIngredient(ingredient, scraped.quantity());
    }

    private List<RecipeIngredient> mapToRecipeIngredients(List<IngredientWithQuantity> ingredientQuantities) {
        List<Ingredient> foundIngredients = ingredientRepository.findByIds(
                ingredientQuantities.stream().map(IngredientWithQuantity::ingredientId).toList()
        );

        Map<IngredientId, Ingredient> ingredientMap = foundIngredients.stream()
                .collect(Collectors.toMap(
                        Ingredient::id,
                        ingredient -> ingredient
                ));

        List<IngredientId> missingIds = ingredientQuantities.stream()
                .map(IngredientWithQuantity::ingredientId)
                .filter(id -> !ingredientMap.containsKey(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new DataIntegrityException(
                    String.format("Ingredients not found with IDs: %s", missingIds)
            );
        }

        return ingredientQuantities.stream()
                .map(iwq -> new RecipeIngredient(
                        ingredientMap.get(iwq.ingredientId()),
                        iwq.quantity()
                ))
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
