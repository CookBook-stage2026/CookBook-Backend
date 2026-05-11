package be.xplore.cookbook.core.domain.householdinvite.command;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.time.Duration;

public record CreateInviteCommand(HouseholdId householdId, UserId requesterId, Duration duration) {
}

