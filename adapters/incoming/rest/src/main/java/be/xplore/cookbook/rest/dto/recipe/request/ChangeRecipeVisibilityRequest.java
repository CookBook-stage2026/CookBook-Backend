package be.xplore.cookbook.rest.dto.recipe.request;

public record ChangeRecipeVisibilityRequest(
        boolean isPublic
) {
}
