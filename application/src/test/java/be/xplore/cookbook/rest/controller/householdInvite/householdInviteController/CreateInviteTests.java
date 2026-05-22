<<<<<<<< HEAD:application/src/test/java/be/xplore/cookbook/rest/controller/householdInvite/householdInviteController/CreateInviteTests.java
package be.xplore.cookbook.rest.controller.householdInvite.householdInviteController;
========
package be.xplore.cookbook.rest.controller.household.householdInviteController;
>>>>>>>> 1a52978 (Feat: #82 Split user and household schedules):application/src/test/java/be/xplore/cookbook/rest/controller/household/householdInviteController/CreateInviteTests.java

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateInviteTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "household_invites", "users"};
    }

    @Test
    void createInvite_WithDefaultDuration_ReturnsCreatedInvite() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(post("/api/household-invites/{id}/invites", household.id().id())
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());
    }

    @Test
    void createInvite_WithCustomDuration_ReturnsCreatedInvite() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(post("/api/household-invites/{id}/invites", household.id().id())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\": 30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void createInvite_WhenNotCreator_ReturnsForbidden() throws Exception {
        User creator = createUser();
        User other = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(post("/api/household-invites/{id}/invites", household.id().id())
                        .with(jwt().jwt(j -> j.subject(other.id().id().toString())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void createInvite_WhenHouseholdNotFound_ReturnsNotFound() throws Exception {
        User creator = createUser();

        getMockMvc().perform(post("/api/household-invites/{id}/invites", UUID.randomUUID())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createInvite_WithDurationBelowMinimum_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);

        getMockMvc().perform(post("/api/household-invites/{id}/invites", household.id().id())
                        .with(jwt().jwt(j -> j.subject(creator.id().id().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\": 0}"))
                .andExpect(status().isBadRequest());
    }
}
