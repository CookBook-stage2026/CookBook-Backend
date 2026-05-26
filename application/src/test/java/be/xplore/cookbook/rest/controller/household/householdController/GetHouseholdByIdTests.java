package be.xplore.cookbook.rest.controller.household.householdController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetHouseholdByIdTests extends BaseIntegrationTest {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID = UserId.create();
    private static final UserId STRANGER_ID = UserId.create();

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "households_members", "users"};
    }

    @Test
    void getHouseholdById_shouldReturnHousehold_whenValidIdAsCreator() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        Household household = createHouseholdWithMembers(List.of(), creator);

        // Act & Assert
        getMockMvc().perform(get("/api/households/{id}", household.id().id())
                        .with(validJwtFromUserId(CREATOR_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(household.id().id().toString()))
                .andExpect(jsonPath("$.name").value(household.name()));
    }

    @Test
    void getHouseholdById_shouldReturnHousehold_whenValidIdAsMember() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        // Act & Assert
        getMockMvc().perform(get("/api/households/{id}", household.id().id())
                        .with(validJwtFromUserId(MEMBER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(household.id().id().toString()));
    }

    @Test
    void getHouseholdById_shouldReturn404_whenNotMember() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        createUserWithId(STRANGER_ID);
        Household household = createHouseholdWithMembers(List.of(), creator);

        // Act & Assert
        getMockMvc().perform(get("/api/households/{id}", household.id().id())
                        .with(validJwtFromUserId(STRANGER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHouseholdById_shouldReturn404_whenNonExistentId() throws Exception {
        // Arrange
        createUserWithId(STRANGER_ID);

        // Act & Assert
        getMockMvc().perform(get("/api/households/{id}", UUID.randomUUID())
                        .with(validJwtFromUserId(STRANGER_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHouseholdById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        Household household = createHouseholdWithMembers(List.of(), creator);

        // Act & Assert
        getMockMvc().perform(get("/api/households/{id}", household.id().id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
