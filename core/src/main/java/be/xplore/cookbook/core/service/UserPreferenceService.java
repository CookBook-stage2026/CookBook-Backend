package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.domain.user.command.FindUserPreferencesQuery;
import be.xplore.cookbook.core.domain.user.command.UpdateUserPreferencesCommand;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.List;

public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 UserRepository userRepository,
                                 IngredientRepository ingredientRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public UserPreferences findPreferences(FindUserPreferencesQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);
        return userPreferenceRepository.findPreferences(user)
                .orElseThrow(() -> new NotFoundException("User preferences not found"));
    }

    public void updatePreferences(UpdateUserPreferencesCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);
        List<Ingredient> ingredients = ingredientRepository.findByIds(command.excludedIngredientIds());
        userPreferenceRepository.save(new UserPreferences(user, command.excludedCategories(), ingredients));
    }
}
