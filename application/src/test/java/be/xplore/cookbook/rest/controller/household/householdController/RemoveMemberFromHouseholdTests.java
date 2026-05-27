package be.xplore.cookbook.rest.controller.household.householdController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RemoveMemberFromHouseholdTests extends BaseIntegrationTest {

    private static final UserId CREATOR_ID = UserId.create();
    private static final UserId MEMBER_ID1 = UserId.create();
    private static final UserId MEMBER_ID2 = UserId.create();
    private static final LocalDate MONDAY = LocalDate.of(2026, 5, 4);

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

    @Test
    void removeMemberFromHousehold_shouldReturn204AndRemoveRecipesFromHouseholdSchedule_WhenCreator() throws Exception {
        // Arrange
        User creator = createUserWithId(CREATOR_ID);
        User member = createUserWithId(MEMBER_ID1);
        Household household = createHouseholdWithMembers(List.of(member), creator);

        Recipe recipe = createAndSaveRecipe("Creator", creator);
        Recipe recipe2 = createAndSaveRecipe("Member", member);

        Map<DayOfWeek, Recipe> householdSchedule = new EnumMap<>(DayOfWeek.class);
        householdSchedule.put(DayOfWeek.MONDAY, recipe);
        householdSchedule.put(DayOfWeek.TUESDAY, recipe2);
        createWeekSchedule(ScheduleOwner.forHousehold(household.id()), householdSchedule, MONDAY);

        Map<DayOfWeek, Recipe> personalSchedule = new EnumMap<>(DayOfWeek.class);
        personalSchedule.put(DayOfWeek.MONDAY, recipe2);
        createWeekSchedule(ScheduleOwner.forUser(member.id()), personalSchedule, MONDAY);

        // Act & Assert
        getMockMvc().perform(delete("/api/households/{id}/members/{userId}", household.id().id(), MEMBER_ID1.id())
                        .with(validJwtFromUserId(CREATOR_ID))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        var savedSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forHousehold(household.id()))
                .getFirst();

        boolean hasCreatorRecipe = savedSchedule.dailyRecipes().stream()
                .anyMatch(ds -> ds.recipe().getId().equals(recipe.getId()));

        boolean hasMemberRecipe = savedSchedule.dailyRecipes().stream()
                .anyMatch(ds -> ds.recipe().getId().equals(recipe2.getId()));

        assertThat(hasCreatorRecipe).isTrue();
        assertThat(hasMemberRecipe).isFalse();

        var savedPersonalSchedule = getWeekScheduleRepository().findAllByOwner(ScheduleOwner.forUser(member.id()))
                .getFirst();

        boolean hasRecipe = savedPersonalSchedule.dailyRecipes().stream()
                .anyMatch(ds -> ds.recipe().getId().equals(recipe2.getId()));

        assertThat(hasRecipe).isTrue();
    }
}
