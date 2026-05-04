package be.xplore.cookbook.core.domain.user.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record FindUserByIdQuery(UserId userId) {
}
