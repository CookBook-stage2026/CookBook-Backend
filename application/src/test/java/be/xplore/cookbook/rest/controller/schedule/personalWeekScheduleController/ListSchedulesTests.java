package be.xplore.cookbook.rest.controller.schedule.personalWeekScheduleController;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.controller.schedule.AbstractListSchedulesTests;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ListSchedulesTests extends AbstractListSchedulesTests {

    @Override
    protected String getBaseUrl() {
        return "/api/schedules/personal";
    }

    @Override
    protected ScheduleOwner setupOwner(User user) {
        return ScheduleOwner.forUser(user.id());
    }

    @Override
    protected MockHttpServletRequestBuilder listRequest() {
        return get(getBaseUrl())
                .with(validJwt())
                .contentType(MediaType.APPLICATION_JSON);
    }
}
