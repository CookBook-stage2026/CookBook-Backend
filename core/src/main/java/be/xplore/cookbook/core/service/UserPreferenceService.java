package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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
        return userPreferenceRepository.findPreferences(userId);
    }

    @Transactional
    public void updatePreferences(UserId userId, List<Category> categories, List<IngredientId> ingredientIds) {
        List<Ingredient> ingredients = ingredientService.getIngredientsByIds(ingredientIds);
        userPreferenceRepository.updatePreferences(userId, new UserPreferences(categories, ingredients));
    }
}
