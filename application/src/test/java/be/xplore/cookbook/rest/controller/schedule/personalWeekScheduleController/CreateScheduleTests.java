package be.xplore.cookbook.rest.controller.schedule.personalWeekScheduleController;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.controller.schedule.AbstractCreateScheduleTests;
import be.xplore.cookbook.rest.dto.schedule.request.CreateWeekScheduleDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class CreateScheduleTests extends AbstractCreateScheduleTests {

    @Override
    protected String getBaseUrl() {
        return "/api/schedules/personal";
    }

    @Override
    protected User setupOwnerAndReturnUser() {
        return createUser();
    }

    @Override
    protected MockHttpServletRequestBuilder createRequest(CreateWeekScheduleDto dto) {
        return post(getBaseUrl())
                .with(validJwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto));
    }
}
