package be.xplore.cookbook.rest.controller.householdInvite.householdInviteController;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetInviteByTokenTests extends BaseIntegrationTest {

    private static final int DEFAULT_INVITE_DURATION = 15;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "household_invites", "users"};
    }

    @Test
    void getInviteByToken_WithValidInvite_ReturnsOkWithInviteData() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(get("/api/household-invites/{token}", token.plainToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.householdInviteId").value(token.invite().id().id().toString()))
                .andExpect(jsonPath("$.revoked").value(false));
    }

    @Test
    void getInviteByToken_WithRevokedInvite_ReturnsOkWithRevokedTrue() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        var invite = getHouseholdInviteRepository().findById(token.invite().id())
                .orElseThrow(() -> new NotFoundException("Household invite not found"));
        getHouseholdInviteRepository().save(invite.revoke());

        getMockMvc().perform(get("/api/household-invites/{token}", token.plainToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.householdInviteId").value(token.invite().id().id().toString()))
                .andExpect(jsonPath("$.revoked").value(true));
    }

    @Test
    void getInviteByToken_WithNonExistentToken_ReturnsNotFound() throws Exception {
        getMockMvc().perform(get("/api/household-invites/{token}", "non-existent-token"))
                .andExpect(status().isNotFound());
    }
}
