package be.xplore.cookbook.config;

import be.xplore.cookbook.config.properties.HouseholdInviteProperties;
import be.xplore.cookbook.core.port.recipe.RecipeImportPort;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.port.weekschedule.ScheduleSuggestionsPort;
import be.xplore.cookbook.core.repository.HouseholdInviteRepository;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.core.service.household.HouseholdInviteService;
import be.xplore.cookbook.core.service.household.HouseholdService;
import be.xplore.cookbook.core.service.ingredient.IngredientService;
import be.xplore.cookbook.core.service.recipe.RecipeCommandService;
import be.xplore.cookbook.core.service.recipe.RecipeQueryService;
import be.xplore.cookbook.core.service.schedule.WeekScheduleService;
import be.xplore.cookbook.core.service.user.UserPreferenceService;
import be.xplore.cookbook.core.service.user.UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(HouseholdInviteProperties.class)
public class AppConfig {

    @Bean
    @Transactional(readOnly = true)
    public IngredientService ingredientService(
            IngredientRepository ingredientRepository,
            UserRepository userRepository
    ) {
        return new IngredientService(ingredientRepository, userRepository);
    }

    @Bean
    @Transactional(readOnly = true)
    public RecipeCommandService recipeCommandService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            UserRepository userRepository,
            WeekScheduleRepository weekScheduleRepository,
            RecipeImportPort recipeImportPort,
            RecipeSuggestionsPort recipeSuggestionsPort
    ) {
        return new RecipeCommandService(recipeRepository, ingredientRepository, userRepository,
                weekScheduleRepository, recipeImportPort, recipeSuggestionsPort);
    }

    @Bean
    @Transactional(readOnly = true)
    public RecipeQueryService recipeQueryService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            UserRepository userRepository,
            UserPreferenceRepository userPreferenceRepository,
            HouseholdRepository householdRepository,
            RecipeSuggestionsPort recipeSuggestionsPort
    ) {
        return new RecipeQueryService(recipeRepository, ingredientRepository, userRepository, userPreferenceRepository,
                householdRepository, recipeSuggestionsPort);
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
            RecipeRepository recipeRepository,
            UserPreferenceRepository userPreferenceRepository,
            HouseholdRepository householdRepository,
            ScheduleSuggestionsPort scheduleSuggestionsPort
    ) {
        return new WeekScheduleService(weekScheduleRepository, userRepository, recipeRepository,
                userPreferenceRepository, householdRepository, scheduleSuggestionsPort);
    }

    @Bean
    @Transactional(readOnly = true)
    public HouseholdService householdService(
            HouseholdRepository houseHoldRepository,
            UserRepository userRepository,
            WeekScheduleRepository weekScheduleRepository
    ) {
        return new HouseholdService(houseHoldRepository, userRepository, weekScheduleRepository);
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
