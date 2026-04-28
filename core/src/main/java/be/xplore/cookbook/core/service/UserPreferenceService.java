package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final IngredientService ingredientService;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 IngredientService ingredientService) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.ingredientService = ingredientService;
    }

    public UserPreferences findPreferences(UserId userId) {
        return userPreferenceRepository.findPreferences(userId)
                .orElseThrow(() -> new NotFoundException("User preferences not found"));
    }

    @Transactional
    public void updatePreferences(UserId userId, List<Category> categories, List<IngredientId> ingredientIds) {
        List<Ingredient> ingredients = ingredientService.getIngredientsByIds(ingredientIds);
        userPreferenceRepository.save(new UserPreferences(userId, categories, ingredients));
    }

    public void createNewPreference(UserId userId) {
        userPreferenceRepository.save(UserPreferences.empty(userId));
    }
}
