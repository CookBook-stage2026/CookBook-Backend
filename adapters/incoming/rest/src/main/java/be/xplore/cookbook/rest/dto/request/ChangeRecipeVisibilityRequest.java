package be.xplore.cookbook.rest.dto.request;

public record ChangeRecipeVisibilityRequest(
        boolean isPublic
) {
}
