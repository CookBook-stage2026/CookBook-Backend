package be.xplore.cookbook.rest.controller.schedule.householdWeekScheduleController;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.controller.schedule.AbstractSuggestRecipeForDateTests;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class SuggestRecipeForDateTests extends AbstractSuggestRecipeForDateTests {

    private HouseholdId householdId;

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
