package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;

import java.util.UUID;

public record ShortRecipeInput(
        UUID id,
        String name,
        String description
) {
    public static ShortRecipeInput fromDomain(RecipeSummary recipe) {
        return new ShortRecipeInput(recipe.id().id(), recipe.name(), recipe.description());
    }
}
