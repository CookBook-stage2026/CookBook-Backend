package be.xplore.cookbook.rest.controller.householdController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetAllHouseholdsTests extends BaseIntegrationTest {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID = UserId.create();
    private static final UserId STRANGER_ID = UserId.create();

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "households",
                "users"
        };
    }

    @Test
    void getAllHouseholds_UserIsCreator_ReturnsHouseholds() throws Exception {
        User creator = createUserWithId(CREATOR_ID);
        Household household = createHouseholdWithMembers(List.of(creator), creator);

        getMockMvc().perform(get("/api/households")
                        .with(validJwtFromUserId(CREATOR_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(household.id().id().toString()))
                .andExpect(jsonPath("$[0].name").value("Test Household"))
                .andExpect(jsonPath("$[0].creator.userId").value(CREATOR_ID.id().toString()));
    }

    @Test
    void getAllHouseholds_UserIsMember_ReturnsHouseholds() throws Exception {
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID);
        Household household = createHouseholdWithMembers(List.of(creator, member), creator);

        getMockMvc().perform(get("/api/households")
                        .with(validJwtFromUserId(MEMBER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(household.id().id().toString()))
                .andExpect(jsonPath("$[0].name").value("Test Household"));
    }

    @Test
    void getAllHouseholds_UserIsCreatorAndMember_ReturnsDeduplicatedHouseholds() throws Exception {
        User creator = createUserWithId(CREATOR_ID);
        createHouseholdWithMembers(List.of(creator), creator);

        getMockMvc().perform(get("/api/households")
                        .with(validJwtFromUserId(CREATOR_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllHouseholds_UserHasNoHouseholds_ReturnsEmptyList() throws Exception {
        createUserWithId(STRANGER_ID);

        getMockMvc().perform(get("/api/households")
                        .with(validJwtFromUserId(STRANGER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllHouseholds_UserBelongsToMultipleHouseholds_ReturnsAll() throws Exception {
        User creator = createUserWithId(CREATOR_ID);
        createHouseholdWithMembers(List.of(creator), creator);
        createHouseholdWithMembers(List.of(creator), creator);

        getMockMvc().perform(get("/api/households")
                        .with(validJwtFromUserId(CREATOR_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllHouseholds_Unauthenticated_Returns401() throws Exception {
        getMockMvc().perform(get("/api/households"))
                .andExpect(status().isUnauthorized());
    }
}
