package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

public record SearchHouseholdRecipesByNameQuery(Paging paging, HouseholdId householdId, UserId userId, String query) {
}
