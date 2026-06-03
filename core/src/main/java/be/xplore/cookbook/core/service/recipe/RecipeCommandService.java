package be.xplore.cookbook.core.service.recipe;

import be.xplore.cookbook.core.domain.exception.DataIntegrityException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Macro;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.command.CalculateMacrosCommand;
import be.xplore.cookbook.core.domain.recipe.command.ChangeVisibilityCommand;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.DeleteRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.ImportRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.IngredientWithQuantity;
import be.xplore.cookbook.core.domain.recipe.command.UpdateRecipeCommand;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.port.recipe.ImportedIngredient;
import be.xplore.cookbook.core.port.recipe.ImportedRecipe;
import be.xplore.cookbook.core.port.recipe.RecipeImportPort;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeCommandService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final WeekScheduleRepository scheduleRepository;
    private final RecipeImportPort recipeImportPort;
    private final RecipeSuggestionsPort aiPort;

    public RecipeCommandService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                                UserRepository userRepository, WeekScheduleRepository scheduleRepository,
                                RecipeImportPort recipeImportPort, RecipeSuggestionsPort aiPort
    ) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.recipeImportPort = recipeImportPort;
        this.aiPort = aiPort;
    }

    public Recipe createRecipe(CreateRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(command.userId()::notFound);

        List<RecipeIngredient> raw = mapToRecipeIngredients(command.ingredientQuantities());
        List<RecipeIngredient> unique = deduplicateIngredients(raw);

        Recipe recipe = new Recipe(
                RecipeId.create(),
                command.details(),
                unique,
                command.isPublic(),
                user,
                List.of()
        );

        return recipeRepository.save(recipe);
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

        recipe.setMacros(List.of());

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

        Recipe recipe = new Recipe(
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
                user,
                List.of()
        );

        recipe.setMacros(scraped.macros());

        return recipeRepository.save(recipe);
    }

    public void changeVisibility(ChangeVisibilityCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        recipe.changeVisibility(command.isPublic());

        recipeRepository.save(recipe);

        if (!command.isPublic()) {
            List<WeekSchedule> schedules = new ArrayList<>(
                    scheduleRepository.findAllByRecipeAndOwnerType(recipe.getId(), ScheduleOwnerType.HOUSEHOLD)
            );

            for (WeekSchedule schedule : schedules) {
                schedule = schedule.removeByRecipeId(recipe.getId());
                scheduleRepository.save(schedule);
            }
        }
    }

    public void deleteRecipe(DeleteRecipeCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        List<WeekSchedule> schedules = new ArrayList<>();
        schedules.addAll(scheduleRepository.findAllByRecipeAndOwnerType(recipe.getId(), ScheduleOwnerType.HOUSEHOLD));
        schedules.addAll(scheduleRepository.findAllByRecipeAndOwnerType(recipe.getId(), ScheduleOwnerType.PERSONAL));

        for (WeekSchedule schedule : schedules) {
            schedule = schedule.removeByRecipeId(recipe.getId());
            scheduleRepository.save(schedule);
        }

        recipeRepository.delete(recipe);
    }

    public Recipe calculateMacros(CalculateMacrosCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(command.userId()::notFound);

        Recipe recipe = recipeRepository.findOwnById(command.recipeId(), user)
                .orElseThrow(command.recipeId()::notFound);

        List<Macro> macros = aiPort.generateMacros(recipe);
        recipe.setMacros(macros);

        return recipeRepository.save(recipe);
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
        return new RecipeIngredient(ingredient, scraped.quantity(), scraped.unit());
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
                        iwq.quantity(),
                        iwq.unit()
                ))
                .toList();
    }

    private List<RecipeIngredient> deduplicateIngredients(List<RecipeIngredient> ingredients) {
        return ingredients.stream()
                .collect(Collectors.groupingBy(ri -> ri.ingredient().name(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .flatMap(group -> mergeGroup(group).stream())
                .toList();
    }

    private List<RecipeIngredient> mergeGroup(List<RecipeIngredient> group) {
        List<RecipeIngredient> result = new ArrayList<>();
        for (RecipeIngredient candidate : group) {
            List<RecipeIngredient> unmerged = new ArrayList<>();
            boolean merged = false;
            for (RecipeIngredient existing : result) {
                List<RecipeIngredient> mergeResult = RecipeIngredient.merge(existing, candidate);
                if (mergeResult.size() == 1) {
                    unmerged.add(mergeResult.getFirst());
                    merged = true;
                } else {
                    unmerged.addAll(mergeResult);
                    merged = true;
                }
            }
            if (!merged) {
                unmerged.add(candidate);
            }
            result = unmerged;
        }
        return result;
    }
}
