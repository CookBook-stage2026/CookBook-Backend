package be.xplore.cookbook.core.service.ingredient;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.command.CreateIngredientCommand;
import be.xplore.cookbook.core.domain.ingredient.command.DeleteIngredientCommand;
import be.xplore.cookbook.core.domain.ingredient.command.SearchIngredientsQuery;
import be.xplore.cookbook.core.domain.ingredient.command.UpdateIngredientCommand;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserRepository;

public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public IngredientService(IngredientRepository ingredientRepository, UserRepository userRepository,
                             RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public Ingredient createIngredient(CreateIngredientCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        var oldIngredient = ingredientRepository.findByNameIgnoreCaseAndUser(command.name(), user);
        if (oldIngredient.isEmpty()) {
            return ingredientRepository.save(new Ingredient(
                    IngredientId.create(),
                    command.name(),
                    command.defaultUnit(),
                    command.categories(),
                    user
            ));
        }
        return ingredientRepository.save(
                new Ingredient(
                        oldIngredient.get().id(),
                        command.name(),
                        command.defaultUnit(),
                        command.categories(),
                        user
                )
        );
    }

    public PagedResult<Ingredient> searchByNameExcludingIds(SearchIngredientsQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        return query.onlyPersonal()
                ? ingredientRepository
                .searchPersonalByNameExcludingIds(query.name(), query.excludedIds(), query.paging(), user)
                : ingredientRepository
                .searchByNameExcludingIds(query.name(), query.excludedIds(), query.paging(), user);
    }

    public void updateIngredient(UpdateIngredientCommand command) {
        Ingredient ingredient = getOwnedIngredient(command.userId(), command.ingredientId());

        Ingredient updated = ingredient.update(command.name(), command.defaultUnit(), command.categories());
        ingredientRepository.save(updated);
    }

    public void deleteIngredient(DeleteIngredientCommand command) {
        Ingredient ingredient = getOwnedIngredient(command.userId(), command.ingredientId());

        recipeRepository.removeIngredient(ingredient.id());

        ingredientRepository.delete(ingredient);
    }

    private Ingredient getOwnedIngredient(UserId userId, IngredientId ingredientId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));

        if (!user.equals(ingredient.user())) {
            throw new NotFoundException("Ingredient not found");
        }

        return ingredient;
    }
}
