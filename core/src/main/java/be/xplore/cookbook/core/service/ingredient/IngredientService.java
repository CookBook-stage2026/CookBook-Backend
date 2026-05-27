package be.xplore.cookbook.core.service.ingredient;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.command.CreateIngredientCommand;
import be.xplore.cookbook.core.domain.ingredient.command.SearchIngredientsQuery;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.List;

public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    public IngredientService(IngredientRepository ingredientRepository, UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
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

    public List<Ingredient> searchByNameExcludingIds(SearchIngredientsQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        return ingredientRepository.searchByNameExcludingIds(query.name(), query.excludedIds(), query.paging(), user);
    }
}
