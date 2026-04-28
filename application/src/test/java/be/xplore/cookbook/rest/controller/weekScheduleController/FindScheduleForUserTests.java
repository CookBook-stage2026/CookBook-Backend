package be.xplore.cookbook.rest.controller.weekScheduleController;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FindScheduleForUserTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users"};
    }

    @Test
    void findForUser_ExistingSchedule_ShouldReturnSchedule() throws Exception {
        var user = createUser();
        var recipes = seedRecipesForAllDays(user);
        seedWeekSchedule(user, recipes);

        getMockMvc().perform(get("/api/schedules")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(DayOfWeek.values().length)))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='TUESDAY')].recipeSummary.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='THURSDAY')].recipeSummary.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Friday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SATURDAY')].recipeSummary.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SUNDAY')].recipeSummary.name").value("Sunday Recipe"));
    }

    @Test
    void findForUser_PartialSchedule_ShouldReturnSchedule() throws Exception {
        var user = createUser();
        var recipe = createAndSaveRecipe("Monday Recipe", user);

        Map<DayOfWeek, Recipe> partialSchedule = new EnumMap<>(DayOfWeek.class);
        partialSchedule.put(DayOfWeek.MONDAY, recipe);
        partialSchedule.put(DayOfWeek.WEDNESDAY, recipe);
        partialSchedule.put(DayOfWeek.FRIDAY, recipe);
        seedWeekSchedule(user, partialSchedule);

        getMockMvc().perform(get("/api/schedules")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(partialSchedule.size())))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Monday Recipe"));
    }

    @Test
    void findForUser_NoSchedule_ShouldReturnNotFound() throws Exception {
        createUser();

        getMockMvc().perform(get("/api/schedules")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void findForUser_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        getMockMvc().perform(get("/api/schedules")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
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
