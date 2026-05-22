package be.xplore.cookbook.rest.controller.household.householdInviteController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RevokeInviteTests extends BaseIntegrationTest {

    private static final int DEFAULT_INVITE_DURATION = 15;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "household_invites", "users"};
    }

    @Test
    void revokeInvite_WhenCreator_ReturnsNoContent() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(delete("/api/household-invites/{householdId}/invites/{inviteId}",
                        household.id().id(), token.invite().id().id())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void revokeInvite_WhenNotCreator_ReturnsForbidden() throws Exception {
        User creator = createUser();
        User other = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(delete("/api/household-invites/{householdId}/invites/{inviteId}",
                        household.id().id(), token.invite().id().id())
                        .with(jwt().jwt(j -> j.subject(other.id().id().toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void revokeInvite_WhenInviteNotFound_ReturnsNotFound() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(delete("/api/household-invites/{householdId}/invites/{inviteId}",
                        household.id().id(), UUID.randomUUID())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void revokeInvite_WhenInviteBelongsToDifferentHousehold_ReturnsNotFound() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        Household otherHousehold = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(otherHousehold.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(delete("/api/household-invites/{householdId}/invites/{inviteId}",
                        household.id().id(), token.invite().id().id())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString()))))
                .andExpect(status().isNotFound());
    }
}
