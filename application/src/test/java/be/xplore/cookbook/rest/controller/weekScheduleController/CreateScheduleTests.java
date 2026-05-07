package be.xplore.cookbook.rest.controller.weekScheduleController;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.CreateDayScheduleDto;
import be.xplore.cookbook.rest.dto.request.CreateWeekScheduleDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateScheduleTests extends BaseIntegrationTest {

    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static final int AMOUNT_OF_RECIPES = 3;
    private static final int REMAINING_DAYS_IN_WEEK = 6;
    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    @Test
    void createSchedule_ValidFullWeekSchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipes = seedRecipesForAllDays(user);

        List<CreateDayScheduleDto> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            days.add(new CreateDayScheduleDto(recipes.get(day).getId().id(), day));
        }

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()))
                .andExpect(jsonPath("$.weekEndDate").value(MONDAY.plusDays(REMAINING_DAYS_IN_WEEK).toString()))
                .andExpect(jsonPath("$.days", hasSize(NUMBER_OF_DAYS_IN_WEEK)))
                .andExpect(jsonPath("$.days[*].day", containsInAnyOrder(
                        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                        "FRIDAY", "SATURDAY", "SUNDAY")))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='TUESDAY')].recipeSummary.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='THURSDAY')].recipeSummary.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Friday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SATURDAY')].recipeSummary.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SUNDAY')].recipeSummary.name").value("Sunday Recipe"));
    }

    @Test
    void createSchedule_PartialWeekSchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Monday Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.WEDNESDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.FRIDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()))
                .andExpect(jsonPath("$.days", hasSize(AMOUNT_OF_RECIPES)))
                .andExpect(jsonPath("$.days[*].recipeSummary.name",
                        containsInAnyOrder("Monday Recipe", "Monday Recipe", "Monday Recipe")))
                .andExpect(jsonPath("$.days[*].day",
                        containsInAnyOrder("MONDAY", "WEDNESDAY", "FRIDAY")));
    }

    @Test
    void createSchedule_SingleDaySchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Single Day Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.TUESDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()))
                .andExpect(jsonPath("$.days", hasSize(1)))
                .andExpect(jsonPath("$.days[0].recipeSummary.name").value("Single Day Recipe"))
                .andExpect(jsonPath("$.days[0].day").value("TUESDAY"));
    }

    @Test
    void createSchedule_EmptySchedule_ShouldReturnOk() throws Exception {
        createUser();
        List<CreateDayScheduleDto> days = List.of();

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()));
    }

    @Test
    void createSchedule_NullDaysList_ShouldReturnBadRequest() throws Exception {
        createUser();

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, null))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_NonExistentRecipe_ShouldReturnUnauthorized() throws Exception {
        UUID nonExistentRecipeId = UUID.randomUUID();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(nonExistentRecipeId, DayOfWeek.MONDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(UUID.randomUUID(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_DuplicateDays_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_InvalidDayScheduleDto_ShouldReturnBadRequest() throws Exception {
        createUser();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(null, DayOfWeek.MONDAY)
        );

        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(new CreateWeekScheduleDto(MONDAY, days))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_WeekStartNotMonday_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        // Wednesday
        getMockMvc().perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(
                                new CreateWeekScheduleDto(MONDAY.plusDays(2), days))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Map<DayOfWeek, Recipe> seedRecipesForAllDays(User user) {
        Map<DayOfWeek, Recipe> recipes = new EnumMap<>(DayOfWeek.class);
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < DayOfWeek.values().length; i++) {
            String recipeName = dayNames[i] + " Recipe";
            recipes.put(DayOfWeek.values()[i], createAndSaveRecipe(recipeName, user));
        }
        return recipes;
    }
}
