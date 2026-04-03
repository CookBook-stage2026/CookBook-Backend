package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.recipe.domain.RecipeSummary;

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
