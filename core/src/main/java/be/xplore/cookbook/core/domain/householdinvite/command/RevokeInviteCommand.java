package be.xplore.cookbook.core.domain.householdinvite.command;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteId;
import be.xplore.cookbook.core.domain.user.UserId;

public record RevokeInviteCommand(HouseholdInviteId inviteId, HouseholdId householdId, UserId requesterId) {
}
