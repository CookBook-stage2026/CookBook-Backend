package be.xplore.cookbook.config;

import be.xplore.cookbook.config.properties.HouseholdInviteProperties;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.repository.HouseholdInviteRepository;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.core.service.HouseholdInviteService;
import be.xplore.cookbook.core.service.HouseholdService;
import be.xplore.cookbook.core.service.IngredientService;
import be.xplore.cookbook.core.service.RecipeService;
import be.xplore.cookbook.core.service.UserPreferenceService;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.core.service.WeekScheduleService;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Configuration
@ConfigurationPropertiesScan
@EnableConfigurationProperties(HouseholdInviteProperties.class)
public class AppConfig {

    @Bean
    @Transactional(readOnly = true)
    public IngredientService ingredientService(IngredientRepository ingredientRepository) {
        return new IngredientService(ingredientRepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public RecipeService recipeService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            RecipeSuggestionsPort recipeSuggestionsPort
    ) {
        return new RecipeService(recipeRepository, ingredientRepository, userRepository,
                userPreferenceRepository, recipeSuggestionsPort);
    }

    @Bean
    @Transactional(readOnly = true)
    public UserService userService(
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository) {
        return new UserService(userRepository, userPreferenceRepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public UserPreferenceService userPreferenceService(
            UserPreferenceRepository userPreferenceRepository,
            UserRepository userRepository,
            IngredientRepository ingredientRepository
    ) {
        return new UserPreferenceService(userPreferenceRepository, userRepository, ingredientRepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public WeekScheduleService weekScheduleService(
            WeekScheduleRepository weekScheduleRepository,
            UserRepository userRepository,
            RecipeRepository recipeRepository
    ) {
        return new WeekScheduleService(weekScheduleRepository, userRepository, recipeRepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public HouseholdService householdService(
            HouseholdRepository houseHoldRepository,
            UserRepository userRepository
    ) {
        return new HouseholdService(houseHoldRepository, userRepository);
    }

    @Bean
    public HouseholdInviteService householdInviteService(
            HouseholdRepository householdRepository,
            UserRepository userRepository,
            HouseholdInviteRepository householdInviteRepository,
            HouseholdInviteProperties properties
    ) {
        return new HouseholdInviteService(
                userRepository,
                householdInviteRepository,
                householdRepository,
                Duration.ofMinutes(properties.defaultDurationMinutes()),
                Duration.ofMinutes(properties.minDurationMinutes()),
                Duration.ofMinutes(properties.maxDurationMinutes())
        );
    }
}
