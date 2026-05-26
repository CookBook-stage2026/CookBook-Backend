package be.xplore.cookbook.rest.controller.schedule.householdWeekScheduleController;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.controller.schedule.AbstractSuggestRecipeForDateTests;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class SuggestRecipeForDateTests extends AbstractSuggestRecipeForDateTests {

    private static final WireMockServer WIRE_MOCK = new WireMockServer(wireMockConfig().dynamicPort());

    private HouseholdId householdId;

    @BeforeAll
    static void startWireMock() {
        WIRE_MOCK.start();
        WireMock.configureFor(WIRE_MOCK.port());
    }

    @AfterAll
    static void stopWireMock() {
        WIRE_MOCK.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("ollama.base-url", WIRE_MOCK::baseUrl);
    }

    @Override
    protected WireMockServer getWireMockServer() {
        return WIRE_MOCK;
    }

    @Override
    protected String getBaseUrl() {
        return "/api/schedules/households/" + householdId.id() + "/suggest/day";
    }

    @Override
    protected ScheduleOwner setupOwner(User user) {
        User member = createUserWithId(UserId.create());

        householdId = createHouseholdWithMembers(List.of(member), user).id();
        return ScheduleOwner.forHousehold(householdId);
    }

    @Override
    protected MockHttpServletRequestBuilder suggestRequest(LocalDate targetDate) {
        return get(getBaseUrl() + "/{date}", targetDate)
                .with(validJwt())
                .contentType(MediaType.APPLICATION_JSON);
    }
}
