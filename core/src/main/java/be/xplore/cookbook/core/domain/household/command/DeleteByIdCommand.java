package be.xplore.cookbook.core.domain.household.command;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

public record DeleteByIdCommand(HouseholdId householdId, UserId userId) {
}
