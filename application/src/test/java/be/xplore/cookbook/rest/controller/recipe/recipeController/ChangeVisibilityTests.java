package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.recipe.request.ChangeRecipeVisibilityRequest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangeVisibilityTests extends BaseIntegrationTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void changeVisibility_shouldUpdate_whenRequestIsValid() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(false, user);

        ChangeRecipeVisibilityRequest dto = new ChangeRecipeVisibilityRequest(true);

        // Act & Assert
        performChangeVisibility(recipe.getId().id(), dto)
                .andExpect(status().isOk());

        Recipe updated = getRecipeRepository()
                .findById(recipe.getId(), user)
                .orElseThrow(recipe.getId()::notFound);

        assertThat(updated.isPublic()).isTrue();
    }

    @Test
    void changeVisibility_shouldReturn404_whenRecipeDoesNotExist() throws Exception {
        // Arrange
        createUser();

        ChangeRecipeVisibilityRequest dto = new ChangeRecipeVisibilityRequest(true);

        // Act & Assert
        performChangeVisibility(UUID.randomUUID(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void changeVisibility_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(false, user);

        ChangeRecipeVisibilityRequest dto = new ChangeRecipeVisibilityRequest(true);

        // Act & Assert
        getMockMvc().perform(put("/api/recipes/{id}/visibility", recipe.getId().id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeVisibility_shouldReturn404_whenUserDoesNotOwnRecipe() throws Exception {
        // Arrange
        User owner = createUser();
        User otherUser = createUserWithId(UserId.create());

        Recipe recipe = createAndSaveRecipe(false, owner);

        ChangeRecipeVisibilityRequest dto = new ChangeRecipeVisibilityRequest(true);

        // Act & Assert
        performChangeVisibilityAsUser(otherUser, recipe.getId().id(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void changeVisibility_shouldUpdateHouseholdWeekSchedule_whenRequestIsValid() throws Exception {
        // Arrange
        User creator = createUserWithId(UserId.create());
        User member = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(List.of(member), creator);

        Recipe recipe = createAndSaveRecipe("Recipe", true, member);

        Map<DayOfWeek, Recipe> householdSchedule = new EnumMap<>(DayOfWeek.class);
        householdSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(ScheduleOwner.forHousehold(household.id()), householdSchedule, MONDAY);

        Map<DayOfWeek, Recipe> personalSchedule = new EnumMap<>(DayOfWeek.class);
        personalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(ScheduleOwner.forUser(member.id()), personalSchedule, MONDAY);

        ChangeRecipeVisibilityRequest dto = new ChangeRecipeVisibilityRequest(false);

        // Act & Assert
        performChangeVisibilityAsUser(member, recipe.getId().id(), dto)
                .andExpect(status().isOk());

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forHousehold(household.id()))
                .getFirst();

        boolean hasRecipeHousehold = savedSchedule.dailyRecipes().stream()
                .anyMatch(ds -> ds.recipe().getId().equals(recipe.getId()));

        AssertionsForClassTypes.assertThat(hasRecipeHousehold).isFalse();


        var savedPersonalSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(member.id()))
                .getFirst();

        boolean hasRecipe = savedPersonalSchedule.dailyRecipes().stream()
                .anyMatch(ds -> ds.recipe().getId().equals(recipe.getId()));

        AssertionsForClassTypes.assertThat(hasRecipe).isTrue();
    }

    private ResultActions performChangeVisibility(UUID recipeId,
                                                  ChangeRecipeVisibilityRequest dto) throws Exception {
        return getMockMvc().perform(put("/api/recipes/{id}/visibility", recipeId)
                .with(validJwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto)));
    }

    private ResultActions performChangeVisibilityAsUser(User user, UUID recipeId,
                                                        ChangeRecipeVisibilityRequest dto) throws Exception {
        return getMockMvc().perform(put("/api/recipes/{id}/visibility", recipeId)
                .with(validJwtFromUserId(user.id()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto)));
    }
}
