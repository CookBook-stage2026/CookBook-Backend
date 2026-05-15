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

class DeleteByIdTests extends BaseIntegrationTest {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID = UserId.create();

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "households_members", "users"};
    }

    @Test
    void deleteById_shouldReturn204_whenCreator() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        Household household = createHouseholdWithMembers(List.of(), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}", household.id().id())
                        .with(validJwtFromUserId(CREATOR_ID))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_shouldReturn403_whenMember() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}", household.id().id())
                        .with(validJwtFromUserId(MEMBER_ID))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        Household household = createHouseholdWithMembers(List.of(), creator);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}", household.id().id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}