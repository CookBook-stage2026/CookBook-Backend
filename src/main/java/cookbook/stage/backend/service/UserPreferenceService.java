package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferenceRepository;
import cookbook.stage.backend.domain.user.UserPreferences;
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
