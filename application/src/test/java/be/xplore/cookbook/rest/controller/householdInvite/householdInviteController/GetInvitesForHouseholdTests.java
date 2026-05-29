package be.xplore.cookbook.rest.controller.householdInvite.householdInviteController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetInvitesForHouseholdTests extends BaseIntegrationTest {

    private static final int DEFAULT_INVITE_DURATION = 15;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "household_invites", "users"};
    }

    @Test
    void getInvitesForHousehold_WithMultipleInvites_ReturnsAllInvites() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        createHouseholdInvite(household.id(), creator.id(), Duration.ofMinutes(DEFAULT_INVITE_DURATION));
        createHouseholdInvite(household.id(), creator.id(), Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(get("/api/household-invites/{householdId}/invites", household.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getInvitesForHousehold_WithNoInvites_ReturnsEmptyArray() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(get("/api/household-invites/{householdId}/invites", household.id().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getInvitesForHousehold_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(get("/api/household-invites/{householdId}/invites", household.id().id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getInvitesForHousehold_WithNonExistentHousehold_ReturnsNotFound() throws Exception {
        createUser();

        getMockMvc().perform(get("/api/household-invites/{householdId}/invites", HouseholdId.create().id())
                        .with(validJwt())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
