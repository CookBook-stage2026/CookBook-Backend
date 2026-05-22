<<<<<<<< HEAD:application/src/test/java/be/xplore/cookbook/rest/controller/householdInvite/householdInviteController/AcceptInviteTests.java
package be.xplore.cookbook.rest.controller.householdInvite.householdInviteController;
========
package be.xplore.cookbook.rest.controller.household.householdInviteController;
>>>>>>>> 1a52978 (Feat: #82 Split user and household schedules):application/src/test/java/be/xplore/cookbook/rest/controller/household/householdInviteController/AcceptInviteTests.java

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AcceptInviteTests extends BaseIntegrationTest {
    private static final int DEFAULT_INVITE_DURATION = 15;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "household_invites", "users"};
    }

    @Test
    void acceptInvite_WithValidToken_ReturnsNoContent() throws Exception {
        User creator = createUser();
        User joiner = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", token.plainToken())
                        .with(jwt().jwt(j -> j.subject(joiner.id().id().toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void acceptInvite_WithExpiredToken_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        User joiner = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createExpiredHouseholdInvite(household.id(), creator.id());

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", token.plainToken())
                        .with(jwt().jwt(j -> j.subject(joiner.id().id().toString()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptInvite_WhenAlreadyMember_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        User joiner = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", token.plainToken())
                        .with(jwt().jwt(j -> j.subject(joiner.id().id().toString()))))
                .andExpect(status().isNoContent());

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", token.plainToken())
                        .with(jwt().jwt(j -> j.subject(joiner.id().id().toString()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptInvite_WithInvalidToken_ReturnsNotFound() throws Exception {
        User joiner = createUser();

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", "invalid-token-that-does-not-exist")
                        .with(jwt().jwt(j -> j.subject(joiner.id().id().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void acceptInvite_WhenCreatorTriesToJoin_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        HouseholdInviteToken token = createHouseholdInvite(household.id(), creator.id(),
                Duration.ofMinutes(DEFAULT_INVITE_DURATION));

        getMockMvc().perform(post("/api/household-invites/invites/{token}/accept", token.plainToken())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString()))))
                .andExpect(status().isBadRequest());
    }
}
