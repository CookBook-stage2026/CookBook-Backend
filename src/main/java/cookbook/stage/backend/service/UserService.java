package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.NotFoundException;
import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.user.SocialConnection;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final IngredientService ingredientService;

    public UserService(UserRepository userRepository, IngredientService ingredientService) {
        this.userRepository = userRepository;
        this.ingredientService = ingredientService;
    }

    public Optional<User> findById(UserId id) {
        if (id == null) {
            throw new NotFoundException("User ID cannot be null");
        }
        return userRepository.findById(id);
    }

    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return userRepository.findBySocialConnection(provider, providerId);
    }

    @Transactional
    public User autoSaveAfterLogin(String email, String name, String provider, String providerId) {
        User user = new User(email, name, new ArrayList<>());
        user.getSocialConnections().add(new SocialConnection(provider, providerId));
        return userRepository.save(user);
    }

    public UserPreferences findPreferences(UserId userId) {
        return userRepository.findPreferences(userId);
    }

    @Transactional
    public void updatePreferences(UserId userId, List<Category> categories, List<IngredientId> ingredientIds) {
        List<Ingredient> ingredients = ingredientService.getIngredientsByIds(ingredientIds);
        userRepository.updatePreferences(userId, new UserPreferences(categories, ingredients));
    }
}
