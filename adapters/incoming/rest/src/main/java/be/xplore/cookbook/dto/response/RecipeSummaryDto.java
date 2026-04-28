package be.xplore.cookbook.dto.response;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;

import java.util.UUID;

public record RecipeSummaryDto(
        UUID id,
        String name,
        String description,
        int durationInMinutes
) {
    public static RecipeSummaryDto fromDomain(RecipeSummary recipe) {
        return new RecipeSummaryDto(
                recipe.id().id(),
                recipe.name(),
                recipe.description(),
                recipe.durationInMinutes()
        );
    }
}
