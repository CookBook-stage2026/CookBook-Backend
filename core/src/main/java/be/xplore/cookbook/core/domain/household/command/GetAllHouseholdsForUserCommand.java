package be.xplore.cookbook.core.domain.household.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record GetAllHouseholdsForUserCommand(UserId userId) {
}
