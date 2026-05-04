package be.xplore.cookbook.core.domain.user.command;

public record AutoSaveAfterLoginCommand(String email, String name, String provider, String providerId) {
}
