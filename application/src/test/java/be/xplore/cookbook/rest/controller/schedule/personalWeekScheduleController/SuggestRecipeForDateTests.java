package be.xplore.cookbook.rest.controller.schedule.personalWeekScheduleController;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.controller.schedule.AbstractSuggestRecipeForDateTests;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class SuggestRecipeForDateTests extends AbstractSuggestRecipeForDateTests {

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
