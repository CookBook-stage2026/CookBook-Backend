package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.NotFoundException;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.user.SocialConnection;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserRepository;
import cookbook.stage.backend.domain.user.WeekSchedule;
import cookbook.stage.backend.domain.user.WeekScheduleId;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RecipeService recipeService;

    public UserService(UserRepository userRepository, RecipeService recipeService) {
        this.userRepository = userRepository;
        this.recipeService = recipeService;
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

    public User autoSaveAfterLogin(String email, String name, String provider, String providerId) {
        User user = new User(email, name, new ArrayList<>());
        user.getSocialConnections().add(new SocialConnection(provider, providerId));
        return userRepository.saveUser(user);
    }

    public WeekSchedule saveWeekSchedule(Map<DayOfWeek, RecipeId> dailyRecipeIds,
                                         UserId userId) {
        Map<DayOfWeek, Recipe> dailyRecipes = new EnumMap<>(DayOfWeek.class);

        dailyRecipeIds.forEach((day, recipeId) -> {
            Recipe recipe = recipeService.findById(recipeId, userId);
            dailyRecipes.put(day, recipe);
        });

        return userRepository.saveWeekSchedule(
                new WeekSchedule(
                        new WeekScheduleId(UUID.randomUUID()),
                        dailyRecipes
                ),
                userId
        );
    }
}
