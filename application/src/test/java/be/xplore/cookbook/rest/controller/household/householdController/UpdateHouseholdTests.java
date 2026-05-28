package be.xplore.cookbook.rest.controller.household.householdController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.household.request.EditHouseholdDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateHouseholdTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "users"};
    }

    @Test
    void updateHousehold_WithValidRequest_ReturnsNoContentAndUpdatesInDatabase() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                "Updated Name",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        Household updatedHousehold = getHouseholdRepository().findById(household.id()).orElseThrow();
        assertThat(updatedHousehold.name()).isEqualTo("Updated Name");
        assertThat(updatedHousehold.description()).isEqualTo("Updated Description");
    }

    @Test
    void updateHousehold_WithEmptyDescription_ReturnsNoContentAndUpdatesInDatabase() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                "Updated Name",
                ""
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        Household updatedHousehold = getHouseholdRepository().findById(household.id()).orElseThrow();
        assertThat(updatedHousehold.name()).isEqualTo("Updated Name");
        assertThat(updatedHousehold.description()).isEmpty();
    }

    @Test
    void updateHousehold_WithEmptyName_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                "",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHousehold_WithNullName_ReturnsBadRequest() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                null,
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHousehold_WithNullHouseholdId_ReturnsBadRequest() throws Exception {
        createUser();
        EditHouseholdDto dto = new EditHouseholdDto(
                null,
                "Updated Name",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHousehold_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {
        User creator = createUser();
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                "Updated Name",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateHousehold_WithNonExistentHousehold_ReturnsNotFound() throws Exception {
        createUser();
        EditHouseholdDto dto = new EditHouseholdDto(
                java.util.UUID.randomUUID(),
                "Updated Name",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateHousehold_ByNonMember_ReturnsForbidden() throws Exception {
        User creator = createUser();
        User otherUser = createUserWithId(be.xplore.cookbook.core.domain.user.UserId.create());
        Household household = createHouseholdWithMembers(new ArrayList<>(), creator);
        EditHouseholdDto dto = new EditHouseholdDto(
                household.id().id(),
                "Updated Name",
                "Updated Description"
        );

        getMockMvc().perform(put("/api/households")
                        .with(validJwtFromUserId(otherUser.id()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
