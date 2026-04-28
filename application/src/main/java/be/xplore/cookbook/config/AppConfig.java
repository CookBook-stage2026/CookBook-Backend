package be.xplore.cookbook.config;

import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.core.service.IngredientService;
import be.xplore.cookbook.core.service.RecipeService;
import be.xplore.cookbook.core.service.UserPreferenceService;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.core.service.WeekScheduleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public IngredientService ingredientService(IngredientRepository ingredientRepository) {
        return new IngredientService(ingredientRepository);
    }

    @Bean
    public RecipeService recipeService(
            RecipeRepository recipeRepository,
            IngredientService ingredientService,
            UserService userService,
            UserPreferenceService userPreferenceService
    ) {
        return new RecipeService(recipeRepository, ingredientService, userService, userPreferenceService);
    }

    @Bean
    public UserService userService(
            UserRepository userRepository,
            UserPreferenceService userPreferenceService) {
        return new UserService(userRepository, userPreferenceService);
    }

    @Bean
    public UserPreferenceService userPreferenceService(
            UserPreferenceRepository userPreferenceRepository,
            IngredientService ingredientService
    ) {
        return new UserPreferenceService(userPreferenceRepository, ingredientService);
    }

    @Bean
    public WeekScheduleService weekScheduleService(
            WeekScheduleRepository weekScheduleRepository,
            UserService userService
    ) {
        return new WeekScheduleService(weekScheduleRepository, userService);
    }
}
