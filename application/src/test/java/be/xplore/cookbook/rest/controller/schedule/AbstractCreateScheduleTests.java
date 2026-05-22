package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.schedule.request.CreateDayScheduleDto;
import be.xplore.cookbook.rest.dto.schedule.request.CreateWeekScheduleDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

public abstract class AbstractCreateScheduleTests extends BaseIntegrationTest {

    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static final int AMOUNT_OF_RECIPES = 3;
    private static final int REMAINING_DAYS_IN_WEEK = 6;
    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    protected abstract MockHttpServletRequestBuilder createRequest(CreateWeekScheduleDto dto);

    protected abstract User setupOwnerAndReturnUser();

    protected abstract String getBaseUrl();

    @Test
    void createSchedule_ValidFullWeekSchedule_ShouldCreateAndReturn() throws Exception {
        User user = setupOwnerAndReturnUser();
        var recipes = seedRecipesForAllDays(user);

        List<CreateDayScheduleDto> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            days.add(new CreateDayScheduleDto(recipes.get(day).getId().id(), day));
        }

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, days)))
                .andExpect(status().isCreated())
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
        User user = setupOwnerAndReturnUser();
        var recipe = createAndSaveRecipe("Monday Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.WEDNESDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.FRIDAY)
        );

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, days)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()))
                .andExpect(jsonPath("$.days", hasSize(AMOUNT_OF_RECIPES)))
                .andExpect(jsonPath("$.days[*].day",
                        containsInAnyOrder("MONDAY", "WEDNESDAY", "FRIDAY")));
    }

    @Test
    void createSchedule_EmptySchedule_ShouldReturnCreated() throws Exception {
        setupOwnerAndReturnUser();

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, List.of())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.weekStartDate").value(MONDAY.toString()));
    }

    @Test
    void createSchedule_NullDaysList_ShouldReturnBadRequest() throws Exception {
        setupOwnerAndReturnUser();

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_NonExistentRecipe_ShouldReturnNotFound() throws Exception {
        setupOwnerAndReturnUser();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(UUID.randomUUID(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, days)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        setupOwnerAndReturnUser();

        getMockMvc().perform(post(getBaseUrl())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(
                                new CreateWeekScheduleDto(MONDAY, List.of()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_DuplicateDays_ShouldReturnBadRequest() throws Exception {
        User user = setupOwnerAndReturnUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, days)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_InvalidDayScheduleDto_ShouldReturnBadRequest() throws Exception {
        setupOwnerAndReturnUser();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(null, DayOfWeek.MONDAY)
        );

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY, days)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_WeekStartNotMonday_ShouldReturnBadRequest() throws Exception {
        User user = setupOwnerAndReturnUser();
        var recipe = createAndSaveRecipe("Test Recipe", user);

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        getMockMvc().perform(createRequest(new CreateWeekScheduleDto(MONDAY.plusDays(2), days)))
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
