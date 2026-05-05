package be.xplore.cookbook.rest.controller.householdController;

import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.CreateHouseholdDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateHouseholdTests extends BaseIntegrationTest {
    @Override
    protected String[] getTablesToClear() {
        return new String[]{"households", "users"};
    }

    @Test
    void createHousehold_shouldReturnHousehold_whenRequestIsValid() throws Exception {
        // Arrange
        CreateHouseholdDto dto = new CreateHouseholdDto("Test Household", "This is a test household");

        createUser();

        // Act & Assert
        performCreateHouseholdWithValidJwt(dto)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Household"))
                .andExpect(jsonPath("$.description").value("This is a test household"))
                .andExpect(jsonPath("$.creator").exists());
    }

    @Test
    void createHousehold_shouldReturn400_whenRequestIsInvalid() throws Exception {
        // Arrange
        CreateHouseholdDto dto = new CreateHouseholdDto("Test Household", null);

        createUser();

        // Act & Assert
        performCreateHouseholdWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHousehold_shouldReturnHousehold_whenDescriptionIsEmpty() throws Exception {
        // Arrange
        CreateHouseholdDto dto = new CreateHouseholdDto("Test Household", "");

        createUser();

        // Act & Assert
        performCreateHouseholdWithValidJwt(dto)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Household"))
                .andExpect(jsonPath("$.creator").exists());
    }

    @Test
    void createHousehold_shouldReturn400_whenNameIsEmpty() throws Exception {
        // Arrange
        CreateHouseholdDto dto = new CreateHouseholdDto("", "This is a test household");

        createUser();

        // Act & Assert
        performCreateHouseholdWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        CreateHouseholdDto dto = new CreateHouseholdDto("Test Household", "This is a test household");

        // Act & Assert
        getMockMvc().perform(post("/api/households")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }


    private ResultActions performCreateHouseholdWithValidJwt(CreateHouseholdDto dto) throws Exception {
        return getMockMvc().perform(post("/api/households")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andDo(print());
    }
}
