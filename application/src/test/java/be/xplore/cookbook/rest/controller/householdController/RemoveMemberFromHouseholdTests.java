package be.xplore.cookbook.rest.controller.householdController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RemoveMemberFromHouseholdTests extends BaseIntegrationTest {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID1 = UserId.create();
    private static final UserId MEMBER_ID2 = UserId.create();

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "households_members", "users"};
    }

    @Test
    void removeMemberFromHousehold_shouldReturn204_WhenCreator() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID1);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), MEMBER_ID1.id())
                        .with(validJwtFromUserId(CREATOR_ID))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeMemberFromHousehold_shouldReturn204_whenMemberRemovingSelf() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID1);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), MEMBER_ID1.id())
                        .with(validJwtFromUserId(MEMBER_ID1))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeMemberFromHousehold_shouldReturn403_whenOtherMemberRemovingMember() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member1 = createUserWithId(MEMBER_ID1);
        User member2 = createUserWithId(MEMBER_ID2);
        Household household = createHouseholdWithMembers(List.of(member1, member2), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), MEMBER_ID1.id())
                        .with(validJwtFromUserId(MEMBER_ID2))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeMemberFromHousehold_shouldReturn403_whenOtherMemberRemovingCreator() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID1);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), CREATOR_ID.id())
                        .with(validJwtFromUserId(MEMBER_ID1))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID1);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), MEMBER_ID1.id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
