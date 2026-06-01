package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractListSchedulesTests extends BaseIntegrationTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);
    private static final int REMAINING_DAYS_IN_WEEK = 6;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    protected abstract MockHttpServletRequestBuilder listRequest();

    protected abstract ScheduleOwner setupOwner(User user);

    protected abstract String getBaseUrl();

    @Test
    void listSchedules_ExistingSchedule_ShouldReturnListOfSchedules() throws Exception {
        User user = createUser();
        Map<DayOfWeek, Recipe> recipes = seedRecipesForAllDays(user);
        ScheduleOwner owner = setupOwner(user);
        createWeekSchedule(owner, recipes, MONDAY);

        getMockMvc().perform(listRequest())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].weekStartDate").value(MONDAY.toString()))
                .andExpect(jsonPath("$[0].weekEndDate").value(MONDAY.plusDays(REMAINING_DAYS_IN_WEEK).toString()))
                .andExpect(jsonPath("$[0].days", hasSize(DayOfWeek.values().length)))
                .andExpect(jsonPath("$[0].days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='TUESDAY')].recipeSummary.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='THURSDAY')].recipeSummary.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='FRIDAY')].recipeSummary.name").value("Friday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='SATURDAY')].recipeSummary.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$[0].days[?(@.day=='SUNDAY')].recipeSummary.name").value("Sunday Recipe"));
    }

    @Test
    void listSchedulesForUser_NoSchedule_ShouldReturnEmptyList() throws Exception {
        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(listRequest())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listSchedules_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(get(getBaseUrl()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listSchedules_MultipleSchedules_ShouldReturnAllForUser() throws Exception {
        var user = createUser();
        var recipe1 = createAndSaveRecipe("Recipe One", user);
        var recipe2 = createAndSaveRecipe("Recipe Two", user);
        ScheduleOwner owner = setupOwner(user);

        Map<DayOfWeek, Recipe> week1 = Map.of(DayOfWeek.MONDAY, recipe1);
        Map<DayOfWeek, Recipe> week2 = Map.of(DayOfWeek.TUESDAY, recipe2);
        createWeekSchedule(owner, week1, MONDAY);
        createWeekSchedule(owner, week2, MONDAY.plusWeeks(1));

        getMockMvc().perform(listRequest())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].weekStartDate").value(MONDAY.plusWeeks(1).toString()))
                .andExpect(jsonPath("$[1].weekStartDate").value(MONDAY.toString()));
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
