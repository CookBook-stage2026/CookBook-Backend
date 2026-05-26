package be.xplore.cookbook.rest.controller.schedule.weekScheduleController;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.schedule.request.CreateDayScheduleDto;
import be.xplore.cookbook.rest.dto.schedule.request.UpdateWeekScheduleDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateScheduleTests extends BaseIntegrationTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    @Test
    void updateSchedule_ValidFullWeekUpdate_ShouldReturnNoContent() throws Exception {
        var user = createUser();
        var originalRecipe = createAndSaveRecipe("Original Recipe", user);
        var updatedRecipe = createAndSaveRecipe("Updated Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, originalRecipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.TUESDAY),
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.WEDNESDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isOk());

        var updatedSchedule = getWeekScheduleRepository()
                .findById(savedSchedule.id())
                .orElseThrow();

        assertThat(updatedSchedule.dailyRecipes())
                .isNotEmpty()
                .allMatch(day -> day.recipe().getId().equals(updatedRecipe.getId()));
    }

    @Test
    void updateSchedule_ValidFullWeekUpdateByHousehold_ShouldReturnNoContent() throws Exception {
        var user = createUser();
        var household = createHouseholdWithMembers(List.of(), user);
        var originalRecipe = createAndSaveRecipe("Original Recipe", user);
        var updatedRecipe = createAndSaveRecipe("Updated Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, originalRecipe);
        createWeekSchedule(ScheduleOwner.forHousehold(household.id()), originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository()
                .findAllByOwner(ScheduleOwner.forHousehold(household.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.TUESDAY),
                new CreateDayScheduleDto(updatedRecipe.getId().id(), DayOfWeek.WEDNESDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isOk());

        var updatedSchedule = getWeekScheduleRepository()
                .findById(savedSchedule.id())
                .orElseThrow();

        assertThat(updatedSchedule.dailyRecipes())
                .isNotEmpty()
                .allMatch(day -> day.recipe().getId().equals(updatedRecipe.getId()));
    }

    @Test
    void updateSchedule_EmptyDays_ShouldReturnNoContent() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of();

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isOk());

        var updatedSchedule = getWeekScheduleRepository()
                .findById(savedSchedule.id())
                .orElseThrow();

        assertThat(updatedSchedule.dailyRecipes()).isEmpty();
    }

    @Test
    void updateSchedule_NonExistentSchedule_ShouldReturnNotFound() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);
        UUID nonExistentId = UUID.randomUUID();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", nonExistentId)
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_NonExistentRecipe_ShouldReturnNotFound() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(UUID.randomUUID(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_NullDaysList_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_DuplicateDays_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(UUID.randomUUID(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateSchedule_InvalidDayScheduleDto_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        Map<DayOfWeek, Recipe> originalSchedule = new EnumMap<>(DayOfWeek.class);
        originalSchedule.put(DayOfWeek.MONDAY, recipe);
        createWeekSchedule(user, originalSchedule, MONDAY);

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(user.id())).getFirst();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(null, DayOfWeek.MONDAY)
        );

        getMockMvc().perform(put("/api/schedules/{id}", savedSchedule.id().id())
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new UpdateWeekScheduleDto(days))))
                .andExpect(status().isBadRequest());
    }
}
