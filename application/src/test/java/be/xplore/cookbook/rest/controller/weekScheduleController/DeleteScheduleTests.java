package be.xplore.cookbook.rest.controller.weekScheduleController;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        seedWeekSchedule(user, originalSchedule, MONDAY);

        WeekSchedule savedSchedule = getWeekScheduleRepository().findAllByUserId(user.id()).getFirst();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSchedule_NonExistentSchedule_ShouldReturnNotFound() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", UUID.randomUUID())
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchedule_ScheduleBelongingToOtherUser_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe("Test Recipe", owner);

        var originalSchedule = new EnumMap<DayOfWeek, Recipe>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        seedWeekSchedule(owner, originalSchedule, MONDAY);

        WeekSchedule savedSchedule = getWeekScheduleRepository().findAllByUserId(owner.id()).getFirst();

        // Act & Assert
        getMockMvc().perform(delete("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        getMockMvc().perform(delete("/api/schedules/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
