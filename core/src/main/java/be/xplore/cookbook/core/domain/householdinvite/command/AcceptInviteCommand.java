package be.xplore.cookbook.core.domain.householdinvite.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record AcceptInviteCommand(String token, UserId userId) {
}
