package be.xplore.cookbook.core.domain.household.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record CreateHouseholdCommand(String name, String description, UserId creatorId) {
}
