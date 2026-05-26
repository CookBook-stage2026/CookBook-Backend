package be.xplore.cookbook.rest.controller.schedule.weekScheduleController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class DeleteScheduleTests extends BaseIntegrationTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    @Test
    void deleteSchedule_ExistingSchedule_ShouldReturnNoContent() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe = createAndSaveRecipe("Test Recipe", user);

        var originalSchedule = new EnumMap<DayOfWeek, Recipe>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        WeekSchedule savedSchedule = getWeekScheduleRepository()
                .findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(
                getWeekScheduleRepository().findById(savedSchedule.id())
        ).isEmpty();
    }

    @Test
    void deleteSchedule_ExistingScheduleBelongingToHousehold_ShouldReturnNoContent() throws Exception {
        // Arrange
        User user = createUser();
        User member = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(List.of(member), user);
        Recipe recipe = createAndSaveRecipe("Test Recipe", user);

        var originalSchedule = new EnumMap<DayOfWeek, Recipe>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(ScheduleOwner.forHousehold(household.id()), originalSchedule, MONDAY);

        WeekSchedule savedSchedule = getWeekScheduleRepository()
                .findAllByOwner(ScheduleOwner.forHousehold(household.id())).getFirst();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(
                getWeekScheduleRepository().findById(savedSchedule.id())
        ).isEmpty();
    }

    @Test
    void deleteSchedule_NonExistentSchedule_ShouldReturnNotFound() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", UUID.randomUUID())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchedule_ScheduleBelongingToOtherUser_ShouldReturnNotFound() throws Exception {
        // Arrange
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe("Test Recipe", owner);

        var originalSchedule = new EnumMap<DayOfWeek, Recipe>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(owner, originalSchedule, MONDAY);

        WeekSchedule savedSchedule = getWeekScheduleRepository()
                .findAllByOwner(ScheduleOwner.forUser(owner.id())).getFirst();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        getMockMvc().perform(delete("/api/schedules/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
