package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.UUID;

public record RecipeSummaryDto(
        UUID id,
        String name,
        String description,
        int durationInMinutes,
        String creator,
        boolean isOwner
) {
    public static RecipeSummaryDto fromDomain(RecipeSummary recipe, UserId requestingUserId) {
        if (requestingUserId.equals(recipe.user().id())) {
            return new RecipeSummaryDto(
                    recipe.id().id(),
                    recipe.name(),
                    recipe.description(),
                    recipe.durationInMinutes(),
                    recipe.user().displayName(),
                    true
            );
        }

        return new RecipeSummaryDto(
                recipe.id().id(),
                recipe.name(),
                recipe.description(),
                recipe.durationInMinutes(),
                recipe.user().displayName(),
                false
                );
    }
}
