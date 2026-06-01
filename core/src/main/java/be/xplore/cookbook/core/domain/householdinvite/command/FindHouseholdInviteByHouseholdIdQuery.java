package be.xplore.cookbook.core.domain.householdinvite.command;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

public record FindHouseholdInviteByHouseholdIdQuery(HouseholdId householdId, UserId loggedInUserId) {
}
