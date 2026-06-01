package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractSuggestWeekScheduleTests extends BaseIntegrationTest {

    private static final LocalDate WEEK_START_DATE = LocalDate.now()
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    private static final int DAYS_IN_WEEK = 7;

    @AfterEach
    void resetWireMock() {
        getWireMockServer().resetAll();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipes", "ingredients", "recipe_ingredients", "recipe_steps",
                "ingredient_categories", "week_schedules", "day_schedules", "users"
        };
    }

    protected abstract WireMockServer getWireMockServer();

    protected abstract MockHttpServletRequestBuilder suggestRequest(LocalDate weekStartDate);

    protected abstract ScheduleOwner setupOwner(User user);

    protected abstract String getBaseUrl();

    @Test
    void suggestWeekSchedule_shouldReturnWeekScheduleWithSevenDays_whenValidRequest() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);
        setupOwner(user);
        stubAiWithRecipe(recipe);

        getMockMvc().perform(suggestRequest(WEEK_START_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.weekStartDate", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(DAYS_IN_WEEK)));
    }

    @Test
    void suggestWeekSchedule_shouldReturnWeekSchedule_whenAdjacentWeekSchedulesExist() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);
        ScheduleOwner owner = setupOwner(user);
        stubAiWithRecipe(recipe);

        createWeekSchedule(owner, Map.of(DayOfWeek.MONDAY, recipe), WEEK_START_DATE.minusWeeks(1));
        createWeekSchedule(owner, Map.of(DayOfWeek.MONDAY, recipe), WEEK_START_DATE.plusWeeks(1));

        getMockMvc().perform(suggestRequest(WEEK_START_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days", hasSize(DAYS_IN_WEEK)));
    }

    @Test
    void suggestWeekSchedule_shouldReturn401_whenNotAuthenticated() throws Exception {
        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(get(getBaseUrl() + "/{weekStartDate}", WEEK_START_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void suggestWeekSchedule_shouldReturn502_whenAiReturnsInvalidResponse() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent("not-json"))));

        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(suggestRequest(WEEK_START_DATE))
                .andExpect(status().isBadGateway());
    }

    @Test
    void suggestWeekSchedule_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(suggestRequest(WEEK_START_DATE))
                .andExpect(status().isServiceUnavailable());
    }

    private void stubAiWithRecipe(Recipe recipe) {
        getWireMockServer().stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent(buildAiResponse(recipe)))));
    }

    private String buildAiResponse(Recipe recipe) {
        record DayEntry(String day, String recipeId) {
        }
        record WeekResponse(List<DayEntry> days) {
        }

        String recipeId = recipe.getId().id().toString();
        List<DayEntry> days = List.of(
                new DayEntry("MONDAY", recipeId),
                new DayEntry("TUESDAY", recipeId),
                new DayEntry("WEDNESDAY", recipeId),
                new DayEntry("THURSDAY", recipeId),
                new DayEntry("FRIDAY", recipeId),
                new DayEntry("SATURDAY", recipeId),
                new DayEntry("SUNDAY", recipeId)
        );

        return getMapper().writeValueAsString(new WeekResponse(days));
    }

    private String buildOllamaResponseBodyWithContent(String content) {
        record Message(String role, String content) {
        }
        record OllamaResponse(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(
                new OllamaResponse(new Message("assistant", content), true)
        );
    }
}
