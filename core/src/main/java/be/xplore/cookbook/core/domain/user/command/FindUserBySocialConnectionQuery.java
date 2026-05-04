package be.xplore.cookbook.core.domain.user.command;

public record FindUserBySocialConnectionQuery(String provider, String providerId) {
}
