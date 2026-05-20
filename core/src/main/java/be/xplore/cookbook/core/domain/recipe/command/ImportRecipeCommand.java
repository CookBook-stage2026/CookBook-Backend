package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record ImportRecipeCommand(String url, UserId userId) {
    public ImportRecipeCommand {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be blank");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
