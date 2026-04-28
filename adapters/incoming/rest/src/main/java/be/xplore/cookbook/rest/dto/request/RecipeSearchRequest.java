package be.xplore.cookbook.rest.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RecipeSearchRequest(
        @NotNull List<UUID> ingredientIds,
        boolean shouldApplyPreferences,
        int page,
        int size
) {
}
