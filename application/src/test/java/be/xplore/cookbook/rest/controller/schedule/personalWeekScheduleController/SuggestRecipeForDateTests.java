package be.xplore.cookbook.rest.controller.schedule.personalWeekScheduleController;

import be.xplore.cookbook.core.domain.user.User;
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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class SuggestRecipeForDateTests extends AbstractSuggestRecipeForDateTests {

    private static final WireMockServer WIRE_MOCK = new WireMockServer(wireMockConfig().dynamicPort());

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
        return "/api/schedules/personal/suggest/day";
    }

    @Override
    protected ScheduleOwner setupOwner(User user) {
        return ScheduleOwner.forUser(user.id());
    }

    @Override
    protected MockHttpServletRequestBuilder suggestRequest(LocalDate targetDate) {
        return get(getBaseUrl() + "/{date}", targetDate)
                .with(validJwt())
                .contentType(MediaType.APPLICATION_JSON);
    }
}
