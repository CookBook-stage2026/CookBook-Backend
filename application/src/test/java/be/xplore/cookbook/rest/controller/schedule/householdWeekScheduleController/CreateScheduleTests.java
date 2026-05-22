package be.xplore.cookbook.rest.controller.schedule.householdWeekScheduleController;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.controller.schedule.AbstractCreateScheduleTests;
import be.xplore.cookbook.rest.dto.schedule.request.CreateWeekScheduleDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class CreateScheduleTests extends AbstractCreateScheduleTests {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID = UserId.create();

    private HouseholdId householdId;

    @Override
    protected String getBaseUrl() {
        return "/api/schedules/households/" + householdId.id();
    }

    @Override
    protected User setupOwnerAndReturnUser() {
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID);

        householdId = createHouseholdWithMembers(List.of(member), creator).id();
        return creator;
    }

    @Override
    protected MockHttpServletRequestBuilder createRequest(CreateWeekScheduleDto dto) {
        return post(getBaseUrl())
                .with(validJwtFromUserId(CREATOR_ID))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto));
    }
}
